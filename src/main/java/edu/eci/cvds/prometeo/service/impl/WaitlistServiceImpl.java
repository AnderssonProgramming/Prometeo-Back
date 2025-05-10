package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.WaitlistEntry;
import edu.eci.cvds.prometeo.repository.GymSessionRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.repository.WaitlistRepository;
import edu.eci.cvds.prometeo.service.NotificationService;
import edu.eci.cvds.prometeo.service.WaitlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WaitlistServiceImpl implements WaitlistService {

    @Autowired
    private WaitlistRepository waitlistRepository;
    
    @Autowired
    private GymSessionRepository gymSessionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    @Transactional
    public UUID addToWaitlist(UUID userId, UUID sessionId) {
        // Verificar si el usuario ya está en la lista de espera
        List<WaitlistEntry> existingEntries = waitlistRepository.findByUserIdAndSessionId(userId, sessionId);
        if (!existingEntries.isEmpty()) {
            return existingEntries.get(0).getId();
        }
        
        // Verificar si la sesión existe
        if (!gymSessionRepository.existsById(sessionId)) {
            throw new IllegalArgumentException("La sesión de gimnasio no existe");
        }
        
        // Verificar si el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        
        // Crear nueva entrada en la lista de espera
        WaitlistEntry entry = new WaitlistEntry();
        entry.setUserId(userId);
        entry.setSessionId(sessionId);
        entry.setRequestTime(LocalDateTime.now());
        entry.setNotificationSent(false);
        
        WaitlistEntry saved = waitlistRepository.save(entry);
        return saved.getId();
    }
    
    @Override
    public int getWaitlistPosition(UUID userId, UUID sessionId) {
        List<WaitlistEntry> entries = waitlistRepository.findBySessionIdOrderByRequestTimeAsc(sessionId);
        
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getUserId().equals(userId)) {
                return i + 1; // Posiciones basadas en 1 (primera posición = 1)
            }
        }
        
        return 0; // Usuario no está en la lista de espera
    }
    
    @Override
    @Transactional
    public boolean notifyNextInWaitlist(UUID sessionId) {
        Optional<WaitlistEntry> nextEntryOpt = 
                waitlistRepository.findFirstBySessionIdAndNotificationSentFalseOrderByRequestTimeAsc(sessionId);
        
        if (nextEntryOpt.isEmpty()) {
            return false; // No hay nadie en la lista de espera
        }
        
        WaitlistEntry entry = nextEntryOpt.get();
        
        // Enviar notificación
        boolean notified = notificationService.sendSpotAvailableNotification(entry.getUserId(), sessionId);
        
        if (notified) {
            // Actualizar estado de la entrada
            entry.setNotificationSent(true);
            entry.setNotificationTime(LocalDateTime.now());
            waitlistRepository.save(entry);
        }
        
        return notified;
    }
    
    @Override
    @Transactional
    public boolean removeFromWaitlist(UUID userId, UUID sessionId) {
        List<WaitlistEntry> entries = waitlistRepository.findByUserIdAndSessionId(userId, sessionId);
        
        if (entries.isEmpty()) {
            return false;
        }
        
        waitlistRepository.deleteAll(entries);
        return true;
    }
    
    @Override
    public Map<String, Object> getWaitlistStats(UUID sessionId) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalCount = waitlistRepository.countBySessionId(sessionId);
        List<WaitlistEntry> entries = waitlistRepository.findBySessionIdOrderByRequestTimeAsc(sessionId);
        
        stats.put("totalCount", totalCount);
        stats.put("notifiedCount", entries.stream().filter(WaitlistEntry::isNotificationSent).count());
        stats.put("pendingCount", entries.stream().filter(e -> !e.isNotificationSent()).count());
        
        if (!entries.isEmpty()) {
            stats.put("oldestRequest", entries.get(0).getRequestTime());
            stats.put("newestRequest", entries.get(entries.size() - 1).getRequestTime());
        }
        
        return stats;
    }
    
    @Override
    public List<Map<String, Object>> getUserWaitlistSessions(UUID userId) {
        List<WaitlistEntry> entries = waitlistRepository.findByUserIdAndNotificationSentFalse(userId);
        
        return entries.stream().map(entry -> {
            Map<String, Object> entryMap = new HashMap<>();
            entryMap.put("entryId", entry.getId());
            entryMap.put("requestTime", entry.getRequestTime());
            entryMap.put("position", getWaitlistPosition(userId, entry.getSessionId()));
            
            // Añadir información de la sesión
            Optional<GymSession> sessionOpt = gymSessionRepository.findById(entry.getSessionId());
            if (sessionOpt.isPresent()) {
                GymSession session = sessionOpt.get();
                Map<String, Object> sessionMap = new HashMap<>();
                sessionMap.put("id", session.getId());
                sessionMap.put("date", session.getSessionDate());
                sessionMap.put("startTime", session.getStartTime());
                sessionMap.put("endTime", session.getEndTime());
                sessionMap.put("capacity", session.getCapacity());
                sessionMap.put("reservedSpots", session.getReservedSpots());
                entryMap.put("session", sessionMap);
            }
            
            return entryMap;
        }).collect(Collectors.toList());
    }
}