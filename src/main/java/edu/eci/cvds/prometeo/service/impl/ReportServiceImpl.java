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

    @Override
    public byte[] generateUserProgressReport(UUID userId, LocalDate startDate, LocalDate endDate, String format) {
        // Simple dummy implementation: just returns a string as bytes
        String report = "User Progress Report for " + userId + " from " + startDate + " to " + endDate;
        return report.getBytes();
    }

    @Override
    public byte[] generateGymUsageReport(LocalDate startDate, LocalDate endDate, String groupBy, String format) {
        // Simple dummy implementation: just returns a string as bytes
        String report = "Gym Usage Report from " + startDate + " to " + endDate + " grouped by " + groupBy;
        return report.getBytes();
    }

    @Override
    public byte[] generateTrainerReport(Optional<UUID> trainerId, LocalDate startDate, LocalDate endDate, String format) {
        // Simple dummy implementation: just returns a string as bytes
        String report = "Trainer Report for " + trainerId.orElse(null) + " from " + startDate + " to " + endDate;
        return report.getBytes();
    }

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
    //     // Dummy: just returns the number of routines assigned in the last X months
    //     LocalDate now = LocalDate.now();
    //     LocalDate from = now.minusMonths(months);
    //     List<UserRoutine> userRoutines = userRoutineRepository.findByUserIdAndAssignmentDateBetween(userId, from, now);
    //     Map<String, Object> stats = new HashMap<>();
    //     stats.put("routinesAssigned", userRoutines.size());
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
            // For demo, assume each reservation is for 1 slot, and max capacity is 10
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
