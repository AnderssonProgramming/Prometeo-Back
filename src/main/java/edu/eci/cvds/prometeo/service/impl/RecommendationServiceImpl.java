package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.model.Goal;
import edu.eci.cvds.prometeo.model.Routine;
import edu.eci.cvds.prometeo.model.User;
import edu.eci.cvds.prometeo.model.PhysicalProgress;
import edu.eci.cvds.prometeo.repository.GoalRepository;
import edu.eci.cvds.prometeo.repository.RoutineRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.repository.PhysicalProgressRepository;
import edu.eci.cvds.prometeo.service.RecommendationService;
import edu.eci.cvds.prometeo.huggingface.HuggingFaceClient;
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
    private GoalRepository goalRepository;

    @Autowired
    private PhysicalProgressRepository physicalProgressRepository;

    @Autowired
    private HuggingFaceClient huggingFaceClient;

    @Override
    public List<Map<Routine, Integer>> recommendRoutines(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_USUARIO));

        List<Goal> goals = goalRepository.findByUserIdAndActive(userId, true);
        List<Routine> allRoutines = routineRepository.findAll();

        String prompt = buildPrompt(goals, allRoutines);

        try {
            String response = huggingFaceClient.queryModel(prompt);
            List<UUID> ids = parseUUIDList(response);
            return buildRecommendations(ids);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private String buildPrompt(List<Goal> goals, List<Routine> allRoutines) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Las metas del usuario son:\n");
        for (Goal goal : goals) {
            prompt.append("- ").append(goal.getGoal()).append("\n");
        }

        prompt.append("Las rutinas disponibles son:\n");
        for (Routine routine : allRoutines) {
            prompt.append("- ID: ").append(routine.getId())
                    .append(" | Nombre: ").append(routine.getName())
                    .append(" | Descripción: ").append(routine.getDescription()).append("\n");
        }

        prompt.append("Según las metas del usuario, responde solo con los IDs de las rutinas recomendadas, separados por comas (máximo 10 recomendaciones).\n");
        prompt.append("Ejemplo: 123e4567-e89b-12d3-a456-426614174000, 123e4567-e89b-12d3-a456-426614174001");

        return prompt.toString();
    }

    private List<UUID> parseUUIDList(String response) {
        return Arrays.stream(response.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }

    private List<Map<Routine, Integer>> buildRecommendations(List<UUID> ids) {
        List<Map<Routine, Integer>> recommendedRoutines = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            UUID id = ids.get(i);
            int finalI = i;
            routineRepository.findById(id).ifPresent(routine -> {
                Map<Routine, Integer> entry = new HashMap<>();
                entry.put(routine, 100 - finalI * 10); // Peso o relevancia de la rutina
                recommendedRoutines.add(entry);
            });
        }
        return recommendedRoutines;
    }

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
