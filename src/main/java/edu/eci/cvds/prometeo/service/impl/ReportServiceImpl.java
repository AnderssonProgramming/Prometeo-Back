package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.service.ReportService;
import edu.eci.cvds.prometeo.repository.ReservationRepository;
import edu.eci.cvds.prometeo.repository.UserRoutineRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.repository.RoutineRepository;
import edu.eci.cvds.prometeo.model.Reservation;
import edu.eci.cvds.prometeo.model.UserRoutine;
import edu.eci.cvds.prometeo.model.Routine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReservationRepository reservationRepository;
    private final UserRoutineRepository userRoutineRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;

    @Autowired
    public ReportServiceImpl(
            ReservationRepository reservationRepository,
            UserRoutineRepository userRoutineRepository,
            UserRepository userRepository,
            RoutineRepository routineRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.userRoutineRepository = userRoutineRepository;
        this.userRepository = userRepository;
        this.routineRepository = routineRepository;
    }

    // @Override
    // public Map<String, Object> generateUserProgressReport(UUID userId, LocalDate startDate, LocalDate endDate, String format) {
    //     // Ejemplo sencillo: solo cuenta rutinas asignadas y reservas hechas en el periodo
    //     Map<String, Object> report = new HashMap<>();
    //     List<UserRoutine> userRoutines = userRoutineRepository.findByUserIdAndAssignmentDateBetween(userId, startDate, endDate);
    //     List<Reservation> reservations = reservationRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

    //     report.put("userId", userId);
    //     report.put("routinesAssigned", userRoutines.size());
    //     report.put("reservations", reservations.size());
    //     report.put("period", Map.of("start", startDate, "end", endDate));
    //     return report;
    // }

    @Override
    public List<Map<String, Object>> generateGymUsageReport(LocalDate startDate, LocalDate endDate, String groupBy, String format) {
        List<Reservation> reservations = reservationRepository.findByDateBetween(startDate, endDate);
        Map<String, Long> grouped;
        DateTimeFormatter formatter;
        if ("week".equalsIgnoreCase(groupBy)) {
            formatter = DateTimeFormatter.ofPattern("YYYY-'W'ww");
            grouped = reservations.stream().collect(Collectors.groupingBy(
                    r -> r.getDate().format(formatter), Collectors.counting()));
        } else if ("month".equalsIgnoreCase(groupBy)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            grouped = reservations.stream().collect(Collectors.groupingBy(
                    r -> r.getDate().format(formatter), Collectors.counting()));
        } else {
            formatter = DateTimeFormatter.ISO_DATE;
            grouped = reservations.stream().collect(Collectors.groupingBy(
                    r -> r.getDate().format(formatter), Collectors.counting()));
        }
        List<Map<String, Object>> report = new ArrayList<>();
        for (Map.Entry<String, Long> entry : grouped.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("period", entry.getKey());
            item.put("reservations", entry.getValue());
            report.add(item);
        }
        return report;
    }

    // @Override
    // public List<Map<String, Object>> generateTrainerReport(Optional<UUID> trainerId, LocalDate startDate, LocalDate endDate, String format) {
    //     List<Reservation> reservations;
    //     if (trainerId.isPresent()) {
    //         reservations = reservationRepository.findByTrainerIdAndDateBetween(trainerId.get(), startDate, endDate);
    //     } else {
    //         reservations = reservationRepository.findByDateBetween(startDate, endDate);
    //     }
    //     List<Map<String, Object>> report = new ArrayList<>();
    //     for (Reservation r : reservations) {
    //         Map<String, Object> item = new HashMap<>();
    //         item.put("date", r.getDate());
    //         item.put("userId", r.getUserId());
    //         item.put("trainerId", r.getTrainerId());
    //         item.put("status", r.getStatus());
    //         report.add(item);
    //     }
    //     return report;
    // }

    @Override
    public Map<String, Integer> getAttendanceStatistics(LocalDate startDate, LocalDate endDate) {
        List<Reservation> reservations = reservationRepository.findByDateBetween(startDate, endDate);
        int attended = 0;
        int missed = 0;
        for (Reservation r : reservations) {
            if (Boolean.TRUE.equals(r.getAttended())) {
                attended++;
            } else {
                missed++;
            }
        }
        Map<String, Integer> stats = new HashMap<>();
        stats.put("attended", attended);
        stats.put("missed", missed);
        stats.put("total", reservations.size());
        return stats;
    }

    // @Override
    // public Map<UUID, Integer> getRoutineUsageStatistics(LocalDate startDate, LocalDate endDate) {
    //     List<UserRoutine> userRoutines = userRoutineRepository.findByAssignmentDateBetween(startDate, endDate);
    //     Map<UUID, Integer> usage = new HashMap<>();
    //     for (UserRoutine ur : userRoutines) {
    //         usage.put(ur.getRoutineId(), usage.getOrDefault(ur.getRoutineId(), 0) + 1);
    //     }
    //     return usage;
    // }

    // @Override
    // public Map<String, Object> getUserProgressStatistics(UUID userId, int months) {
    //     LocalDate now = LocalDate.now();
    //     LocalDate from = now.minusMonths(months);
    //     List<UserRoutine> userRoutines = userRoutineRepository.findByUserIdAndAssignmentDateBetween(userId, from, now);
    //     Map<String, Object> stats = new HashMap<>();
    //     stats.put("routinesAssigned", userRoutines.size());
    //     stats.put("period", Map.of("start", from, "end", now));
    //     return stats;
    // }

    @Override
    public Map<String, Double> getCapacityUtilization(LocalDate startDate, LocalDate endDate, String groupBy) {
        List<Reservation> reservations = reservationRepository.findByDateBetween(startDate, endDate);
        Map<String, Integer> countByGroup = new HashMap<>();
        Map<String, Integer> capacityByGroup = new HashMap<>();
        DateTimeFormatter formatter;
        if ("day".equalsIgnoreCase(groupBy)) {
            formatter = DateTimeFormatter.ISO_DATE;
        } else if ("week".equalsIgnoreCase(groupBy)) {
            formatter = DateTimeFormatter.ofPattern("YYYY-'W'ww");
        } else {
            formatter = DateTimeFormatter.ofPattern("YYYY-MM");
        }
        for (Reservation r : reservations) {
            String key = r.getDate().format(formatter);
            countByGroup.put(key, countByGroup.getOrDefault(key, 0) + 1);
            // Para demo, capacidad fija de 10 por grupo
            capacityByGroup.put(key, 10);
        }
        Map<String, Double> utilization = new HashMap<>();
        for (String key : countByGroup.keySet()) {
            int used = countByGroup.get(key);
            int cap = capacityByGroup.getOrDefault(key, 10);
            utilization.put(key, cap == 0 ? 0.0 : (used * 100.0 / cap));
        }
        return utilization;
    }
}