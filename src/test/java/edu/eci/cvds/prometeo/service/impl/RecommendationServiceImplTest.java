package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.model.*;
import edu.eci.cvds.prometeo.openai.OpenAiClient;
import edu.eci.cvds.prometeo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;






@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private PhysicalProgressRepository physicalProgressRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private OpenAiClient openAiClient;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private UUID userId;
    private User user;
    private List<Goal> goals;
    private List<Routine> routines;
    private String openAiResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        
        // Setup goals
        goals = new ArrayList<>();
        Goal goal1 = new Goal();
        goal1.setGoal("Perder peso");
        goals.add(goal1);
        
        // Setup routines
        routines = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Routine routine = new Routine();
            routine.setId(UUID.randomUUID());
            routine.setName("Routine " + i);
            routine.setDescription("Description " + i);
            routines.add(routine);
        }
        
        // Setup OpenAI mock response
        openAiResponse = "{\"choices\":[{\"message\":{\"content\":\"" + routines.get(0).getId() + ", " + routines.get(1).getId() + "\"}}]}";
    }

    @Test
    void testRecommendRoutinesSuccess() {
        // Setup mocks
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findByUserIdAndActive(userId, true)).thenReturn(goals);
        when(routineRepository.findAll()).thenReturn(routines);
        when(openAiClient.queryModel(anyString())).thenReturn(openAiResponse);
        when(routineRepository.findById(any(UUID.class))).thenReturn(Optional.of(routines.get(0)), Optional.of(routines.get(1)));
        when(recommendationRepository.findByUserIdAndRoutineId(any(UUID.class), any(UUID.class))).thenReturn(Optional.empty());
        
        // Execute
        List<Map<Routine, Integer>> result = recommendationService.recommendRoutines(userId);
        
        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(recommendationRepository, times(2)).save(any(Recommendation.class));
    }

    @Test
    void testRecommendRoutinesUserNotFound() {
        // Setup
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Execute & Verify
        assertThrows(PrometeoExceptions.class, () -> recommendationService.recommendRoutines(userId));
    }

    @Test
    void testRecommendRoutinesOpenAiException() {
        // Setup mocks
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findByUserIdAndActive(userId, true)).thenReturn(goals);
        when(routineRepository.findAll()).thenReturn(routines);
        when(openAiClient.queryModel(anyString())).thenThrow(new RuntimeException("OpenAI error"));
        
        // Execute
        List<Map<Routine, Integer>> result = recommendationService.recommendRoutines(userId);
        
        // Verify
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindUserRoutinesSuccess() {
        // Setup
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        List<Recommendation> recommendations = new ArrayList<>();
        for (Routine routine : routines) {
            Recommendation rec = new Recommendation();
            rec.setRoutine(routine);
            recommendations.add(rec);
        }
        
        when(recommendationRepository.findByUserIdAndActive(userId, true)).thenReturn(recommendations);
        
        // Execute
        List<Routine> result = recommendationService.findUserRoutines(userId);
        
        // Verify
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(routines.get(0), result.get(0));
        assertEquals(routines.get(1), result.get(1));
        assertEquals(routines.get(2), result.get(2));
    }

    @Test
    void testFindUserRoutinesUserNotFound() {
        // Setup
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Execute & Verify
        assertThrows(PrometeoExceptions.class, () -> recommendationService.findUserRoutines(userId));
    }

    @Test
    void testParseUUIDListWithValidResponse() {
        // Setup 
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        String validResponse = "{\"choices\":[{\"message\":{\"content\":\"" + uuid1 + ", " + uuid2 + "\"}}]}";
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findByUserIdAndActive(userId, true)).thenReturn(goals);
        when(routineRepository.findAll()).thenReturn(routines);
        when(openAiClient.queryModel(anyString())).thenReturn(validResponse);
        when(routineRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        
        // Execute
        List<Map<Routine, Integer>> result = recommendationService.recommendRoutines(userId);
        
        // Verify
        // Since routines aren't found, the result list should be empty but internal method still processes UUIDs
        assertTrue(result.isEmpty());
        // Verify that findById was called for both UUIDs
        verify(routineRepository, times(2)).findById(any(UUID.class));
    }

    @Test
    void testParseUUIDListWithInvalidResponse() {
        // Setup
        String invalidResponse = "{\"choices\":[{\"message\":{\"content\":\"Invalid UUID format\"}}]}";
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findByUserIdAndActive(userId, true)).thenReturn(goals);
        when(routineRepository.findAll()).thenReturn(routines);
        when(openAiClient.queryModel(anyString())).thenReturn(invalidResponse);
        
        // Execute
        List<Map<Routine, Integer>> result = recommendationService.recommendRoutines(userId);
        
        // Verify
        assertTrue(result.isEmpty());
        // No routines should be looked up since no valid UUIDs were found
        verify(routineRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testBuildRecommendationsWithExistingRecommendation() {
        // Setup
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findByUserIdAndActive(userId, true)).thenReturn(goals);
        when(routineRepository.findAll()).thenReturn(routines);
        
        UUID routineId = routines.get(0).getId();
        String response = "{\"choices\":[{\"message\":{\"content\":\"" + routineId + "\"}}]}";
        when(openAiClient.queryModel(anyString())).thenReturn(response);
        when(routineRepository.findById(routineId)).thenReturn(Optional.of(routines.get(0)));
        
        Recommendation existingRec = new Recommendation();
        existingRec.setUser(user);
        existingRec.setRoutine(routines.get(0));
        existingRec.setWeight(50);
        existingRec.setActive(false);
        
        when(recommendationRepository.findByUserIdAndRoutineId(userId, routineId)).thenReturn(Optional.of(existingRec));
        
        // Execute
        List<Map<Routine, Integer>> result = recommendationService.recommendRoutines(userId);
        
        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recommendationRepository, times(1)).save(existingRec);
        assertTrue(existingRec.isActive());
        assertEquals(100, existingRec.getWeight());
    }
}