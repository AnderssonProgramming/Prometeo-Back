package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.repository.GymSessionRepository;
import edu.eci.cvds.prometeo.service.GymSessionService;
import edu.eci.cvds.prometeo.PrometeoExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class GymSessionServiceImpl implements GymSessionService {

    private final GymSessionRepository gymSessionRepository;

    @Autowired
    public GymSessionServiceImpl(GymSessionRepository gymSessionRepository) {
        this.gymSessionRepository = gymSessionRepository;
    }

    @Override
    @Transactional
    public UUID createSession(LocalDate date, LocalTime startTime, LocalTime endTime, int capacity, Optional<String> description, UUID trainerId) {
        // Prevent overlapping sessions
        Optional<GymSession> overlapping = gymSessionRepository
                .findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(date, startTime, endTime);
        if (overlapping.isPresent()) {
            throw new PrometeoExceptions(PrometeoExceptions.SESION_YA_EXISTE_HORARIO);
        }
        GymSession session = new GymSession();
        session.setSessionDate(date);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setCapacity(capacity);
        session.setReservedSpots(0);
        session.setTrainerId(trainerId);
        // If you have a description field, set it here
        return gymSessionRepository.save(session).getId();
    }

    @Override
    @Transactional
    public boolean updateSession(UUID sessionId, LocalDate date, LocalTime startTime, LocalTime endTime, int capacity, UUID trainerId) {
        GymSession session = gymSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.SESION_NO_ENCONTRADA));
        // Prevent overlapping with other sessions
        Optional<GymSession> overlapping = gymSessionRepository
                .findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(date, startTime, endTime);
        if (overlapping.isPresent() && !overlapping.get().getId().equals(sessionId)) {
            throw new PrometeoExceptions(PrometeoExceptions.SESION_YA_EXISTE_HORARIO);
        }
        session.setSessionDate(date);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setCapacity(capacity);
        session.setTrainerId(trainerId);
        gymSessionRepository.save(session);
        return true;
    }

    @Override
    @Transactional
    public boolean cancelSession(UUID sessionId, String reason, UUID trainerId) {
        GymSession session = gymSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.SESION_NO_ENCONTRADA));
        // If you have a status or cancellation field, set it here
        // session.setStatus("CANCELLED");
        gymSessionRepository.delete(session);
        return true;
    }

    @Override
    public List<Object> getSessionsByDate(LocalDate date) {
        List<GymSession> sessions = gymSessionRepository.findBySessionDateOrderByStartTime(date);
        List<Object> result = new ArrayList<>();
        for (GymSession session : sessions) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", session.getId());
            map.put("date", session.getSessionDate());
            map.put("startTime", session.getStartTime());
            map.put("endTime", session.getEndTime());
            map.put("capacity", session.getCapacity());
            map.put("reservedSpots", session.getReservedSpots());
            map.put("trainerId", session.getTrainerId());
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Object> getSessionsByTrainer(UUID trainerId) {
        List<GymSession> sessions = gymSessionRepository.findBySessionDateAndTrainerId(LocalDate.now(), trainerId);
        List<Object> result = new ArrayList<>();
        for (GymSession session : sessions) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", session.getId());
            map.put("date", session.getSessionDate());
            map.put("startTime", session.getStartTime());
            map.put("endTime", session.getEndTime());
            map.put("capacity", session.getCapacity());
            map.put("reservedSpots", session.getReservedSpots());
            map.put("trainerId", session.getTrainerId());
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getAvailableTimeSlots(LocalDate date) {
        List<GymSession> sessions = gymSessionRepository.findBySessionDateOrderByStartTime(date);
        List<Map<String, Object>> result = new ArrayList<>();
        for (GymSession session : sessions) {
            if (session.hasAvailability()) {
                Map<String, Object> map = new HashMap<>();
                map.put("sessionId", session.getId());
                map.put("date", session.getSessionDate());
                map.put("startTime", session.getStartTime());
                map.put("endTime", session.getEndTime());
                map.put("availableSpots", session.getAvailableSpots());
                map.put("trainerId", session.getTrainerId());
                result.add(map);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public int configureRecurringSessions(int dayOfWeek, LocalTime startTime, LocalTime endTime, int capacity, Optional<String> description, UUID trainerId, LocalDate startDate, LocalDate endDate) {
        int count = 0;
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            if (date.getDayOfWeek().getValue() == dayOfWeek) {
                // Prevent overlapping sessions for each recurrence
                Optional<GymSession> overlapping = gymSessionRepository
                        .findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(date, startTime, endTime);
                if (overlapping.isEmpty()) {
                    GymSession session = new GymSession();
                    session.setSessionDate(date);
                    session.setStartTime(startTime);
                    session.setEndTime(endTime);
                    session.setCapacity(capacity);
                    session.setReservedSpots(0);
                    session.setTrainerId(trainerId);
                    // If you have a description field, set it here
                    gymSessionRepository.save(session);
                    count++;
                }
            }
            date = date.plusDays(1);
        }
        return count;
    }

    @Override
    public Map<LocalDate, Integer> getOccupancyStatistics(LocalDate startDate, LocalDate endDate) {
        List<GymSession> sessions = gymSessionRepository.findBySessionDateBetween(startDate, endDate);
        Map<LocalDate, Integer> stats = new HashMap<>();
        Map<LocalDate, List<GymSession>> grouped = new HashMap<>();
        for (GymSession session : sessions) {
            grouped.computeIfAbsent(session.getSessionDate(), k -> new ArrayList<>()).add(session);
        }
        for (Map.Entry<LocalDate, List<GymSession>> entry : grouped.entrySet()) {
            int total = 0;
            int reserved = 0;
            for (GymSession session : entry.getValue()) {
                total += session.getCapacity();
                reserved += session.getReservedSpots();
            }
            int percent = total > 0 ? (reserved * 100) / total : 0;
            stats.put(entry.getKey(), percent);
        }
        return stats;
    }
}
