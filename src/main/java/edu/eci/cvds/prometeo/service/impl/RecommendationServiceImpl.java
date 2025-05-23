package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.model.*;
import edu.eci.cvds.prometeo.openai.OpenAiClient;
import edu.eci.cvds.prometeo.repository.*;
import edu.eci.cvds.prometeo.service.RecommendationService;
import edu.eci.cvds.prometeo.huggingface.HuggingFaceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private RecommendationRepository recommendationRepository;

    @Autowired
    private OpenAiClient openAiClient;

    @Override
    public List<Map<Routine, Integer>> recommendRoutines(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_USUARIO));

        List<Goal> goals = goalRepository.findByUserIdAndActive(userId, true);
        List<Routine> allRoutines = routineRepository.findAll();

        String prompt = buildPrompt(goals, allRoutines);

        try {
            String response = openAiClient.queryModel(prompt);
            List<UUID> ids = parseUUIDList(response);
            return buildRecommendations(ids, user);
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
    List<UUID> result = new ArrayList<>();
    try {
        // Extraer la respuesta del formato JSON de OpenAI
        JsonNode responseJson = new ObjectMapper().readTree(response);
        String content = responseJson.path("choices").path(0).path("message").path("content").asText("");
        
        // Buscar texto que parezca un UUID en la respuesta
        Pattern uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", 
                                              Pattern.CASE_INSENSITIVE);
        Matcher matcher = uuidPattern.matcher(content);
        
        // Añadir todos los UUIDs encontrados
        while (matcher.find() && result.size() < 10) {
            try {
                UUID uuid = UUID.fromString(matcher.group());
                result.add(uuid);
            } catch (IllegalArgumentException e) {
                // Ignora los formatos UUID inválidos
            }
        }
    } catch (Exception e) {
        // Log the error
        System.err.println("Error parsing OpenAI response: " + e.getMessage());
    }
    
    return result;
}

    private List<Map<Routine, Integer>> buildRecommendations(List<UUID> routineIds, User user) {
        List<Map<Routine, Integer>> recommendedRoutines = new ArrayList<>();
        for (int i = 0; i < routineIds.size(); i++) {
            UUID routineId = routineIds.get(i);
            int weight = 100 - i * 10;

            routineRepository.findById(routineId).ifPresent(routine -> {
                Optional<Recommendation> existing = recommendationRepository.findByUserIdAndRoutineId(user.getId(), routineId);

                if (existing.isPresent()) {
                    Recommendation rec = existing.get();
                    rec.setActive(true);
                    rec.setWeight(weight);
                    recommendationRepository.save(rec);
                } else {
                    Recommendation newRec = new Recommendation();
                    newRec.setUser(user);
                    newRec.setRoutine(routine);
                    newRec.setWeight(weight);
                    newRec.setActive(true);
                    recommendationRepository.save(newRec);
                }

                Map<Routine, Integer> entry = new HashMap<>();
                entry.put(routine, weight);
                recommendedRoutines.add(entry);
            });
        }

        return recommendedRoutines;
    }

    @Override
    public List<Routine> findUserRoutines(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_USUARIO));

        List<Recommendation> userRecommendations = recommendationRepository.findByUserIdAndActive(userId, true);
        return userRecommendations.stream()
                .map(Recommendation::getRoutine)
                .collect(Collectors.toList());
    }
}
