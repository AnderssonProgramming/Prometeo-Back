package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.Routine;
import edu.eci.cvds.prometeo.model.User;
import edu.eci.cvds.prometeo.model.PhysicalProgress;
import edu.eci.cvds.prometeo.repository.RoutineRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.repository.PhysicalProgressRepository;
import edu.eci.cvds.prometeo.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhysicalProgressRepository physicalProgressRepository;

    // @Override
    // public List<Map<Routine, Integer>> recommendRoutines(UUID userId, Optional<String> goal, int limit) {
    //     User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    //     List<Routine> routines;
    //     if (goal.isPresent()) {
    //         routines = routineRepository.findByGoal(goal.get());
    //     } else {
    //         routines = routineRepository.findAll();
    //     }
    //     // Simple compatibility: routines matching user's goal or profile
    //     List<Map<Routine, Integer>> recommendations = new ArrayList<>();
    //     for (Routine routine : routines) {
    //         int score = calculateRoutineCompatibility(user, routine);
    //         Map<Routine, Integer> map = new HashMap<>();
    //         map.put(routine, score);
    //         recommendations.add(map);
    //     }
    //     return recommendations.stream()
    //             .sorted((a, b) -> b.values().iterator().next() - a.values().iterator().next())
    //             .limit(limit)
    //             .collect(Collectors.toList());
    // }

    @Override
    public Map<LocalTime, Integer> recommendTimeSlots(UUID userId, LocalDate date) {
        // Dummy implementation: returns time slots with random occupancy
        Map<LocalTime, Integer> result = new LinkedHashMap<>();
        for (int hour = 6; hour <= 22; hour += 2) {
            result.put(LocalTime.of(hour, 0), new Random().nextInt(100));
        }
        return result;
    }

    // @Override
    // public Map<UUID, Integer> findSimilarUsers(UUID userId, int limit) {
    //     User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    //     List<User> allUsers = userRepository.findAll();
    //     Map<UUID, Integer> similarityMap = new HashMap<>();
    //     for (User other : allUsers) {
    //         if (!other.getId().equals(userId)) {
    //             int score = calculateUserSimilarity(user, other);
    //             similarityMap.put(other.getId(), score);
    //         }
    //     }
    //     return similarityMap.entrySet().stream()
    //             .sorted((a, b) -> b.getValue() - a.getValue())
    //             .limit(limit)
    //             .collect(Collectors.toMap(
    //                     Map.Entry::getKey,
    //                     Map.Entry::getValue,
    //                     (a, b) -> a,
    //                     LinkedHashMap::new
    //             ));
    // }

    @Override
    public List<String> generateImprovementSuggestions(UUID userId) {
        List<PhysicalProgress> progresses = physicalProgressRepository.findByUserId(userId);
        List<String> suggestions = new ArrayList<>();
        if (progresses.isEmpty()) {
            suggestions.add("Start tracking your progress to receive personalized suggestions.");
            return suggestions;
        }
        // Dummy logic: if progress is stagnant, suggest increasing intensity
        PhysicalProgress last = progresses.get(progresses.size() - 1);
        // For demonstration, let's assume improvement is based on weight change (you can adjust as needed)
        double improvement = 0.0;
        if (progresses.size() > 1) {
            PhysicalProgress prev = progresses.get(progresses.size() - 2);
            if (last.getWeight() != null && prev.getWeight() != null) {
                improvement = last.getWeight().getValue() - prev.getWeight().getValue();
            }
        }
        if (improvement < 1.0) {
            suggestions.add("Try increasing your workout intensity or frequency.");
        } else {
            suggestions.add("Keep up the good work!");
        }
        return suggestions;
    }

    @Override
    public Map<String, Double> predictProgress(UUID userId, int weeksAhead) {
        List<PhysicalProgress> progresses = physicalProgressRepository.findByUserId(userId);
        Map<String, Double> prediction = new HashMap<>();
        if (progresses.isEmpty()) {
            prediction.put("weight", 0.0);
            prediction.put("strength", 0.0);
            return prediction;
        }
        // Dummy linear prediction based on weight (strength is not available in PhysicalProgress, so set to 0)
        PhysicalProgress last = progresses.get(progresses.size() - 1);
        double lastWeight = last.getWeight() != null ? last.getWeight().getValue() : 0.0;
        prediction.put("weight", lastWeight - weeksAhead * 0.5);
        prediction.put("strength", 0.0);
        return prediction;
    }

    @Override
    public int evaluateRoutineEffectiveness(UUID userId, UUID routineId) {
        // Dummy: returns a random effectiveness score
        return new Random().nextInt(101);
    }

    // --- Helper methods ---

    // private int calculateRoutineCompatibility(User user, Routine routine) {
    //     int score = 0;
    //     if (routine.getGoal().equalsIgnoreCase(user.getGoal())) score += 50;
    //     // Add more sophisticated logic as needed
    //     return score + new Random().nextInt(50);
    // }

    // private int calculateUserSimilarity(User u1, User u2) {
    //     int score = 0;
    //     if (Objects.equals(u1.getGoal(), u2.getGoal())) score += 50;
    //     // Add more sophisticated logic as needed
    //     return score + new Random().nextInt(50);
    // }
}
