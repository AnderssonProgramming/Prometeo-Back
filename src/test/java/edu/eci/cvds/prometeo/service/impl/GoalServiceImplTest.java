package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.model.Goal;
import edu.eci.cvds.prometeo.model.Recommendation;
import edu.eci.cvds.prometeo.model.User;
import edu.eci.cvds.prometeo.repository.GoalRepository;
import edu.eci.cvds.prometeo.repository.RecommendationRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.service.RecommendationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private GoalServiceImpl goalService;

    private UUID userId;
    private UUID goalId;
    private Goal testGoal;
    private List<Goal> goalList;
    private List<Recommendation> recommendationList;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        goalId = UUID.randomUUID();
        
        testGoal = new Goal();
        testGoal.setId(goalId);
        testGoal.setUserId(userId);
        testGoal.setGoal("Test goal");
        testGoal.setActive(true);
        
        goalList = new ArrayList<>();
        goalList.add(testGoal);
        
        recommendationList = new ArrayList<>();
        Recommendation testRecommendation = new Recommendation();
        testRecommendation.setId(UUID.randomUUID());

        testRecommendation.setActive(true);
        recommendationList.add(testRecommendation);
    }

    @Test
    public void testGetGoalsByUser() {
        when(goalRepository.findByUserIdAndActive(userId, true)).thenReturn(goalList);
        
        List<Goal> result = goalService.getGoalsByUser(userId);
        
        assertEquals(1, result.size());
        assertEquals(testGoal.getId(), result.get(0).getId());
        assertEquals(testGoal.getGoal(), result.get(0).getGoal());
        verify(goalRepository, times(1)).findByUserIdAndActive(userId, true);
    }

    @Test
    public void testAddUserGoal() {
        List<String> goals = Arrays.asList("Goal 1", "Goal 2");
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);
        when(recommendationRepository.findByUserIdAndActive(userId, true)).thenReturn(recommendationList);
        
        goalService.addUserGoal(userId, goals);
        
        verify(userRepository, times(1)).findById(userId);
        verify(recommendationRepository, times(1)).findByUserIdAndActive(userId, true);
        verify(recommendationRepository, times(1)).saveAll(recommendationList);
        verify(goalRepository, times(2)).save(any(Goal.class));
        verify(recommendationService, times(1)).recommendRoutines(userId);
    }
      @Test
    public void testAddUserGoalWithInvalidUser() {
        List<String> goals = Arrays.asList("Goal 1");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Verificar que se lanza la excepción esperada
        PrometeoExceptions exception = org.junit.jupiter.api.Assertions.assertThrows(
            PrometeoExceptions.class,
            () -> goalService.addUserGoal(userId, goals)
        );
        assertEquals("El usuario no existe", exception.getMessage());
    }

    @Test
    public void testUpdateUserGoal() {
        Map<UUID, String> updatedGoals = new HashMap<>();
        updatedGoals.put(goalId, "Updated goal");
        
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(testGoal));
        when(recommendationRepository.findByUserIdAndActive(userId, true)).thenReturn(recommendationList);
        
        goalService.updateUserGoal(updatedGoals);
        
        verify(goalRepository, times(2)).findById(goalId);
        verify(goalRepository, times(1)).save(any(Goal.class));
        verify(recommendationRepository, times(1)).findByUserIdAndActive(userId, true);
        verify(recommendationRepository, times(1)).saveAll(recommendationList);
        verify(recommendationService, times(1)).recommendRoutines(userId);
    }    @Test
    public void testUpdateUserGoalWithInvalidGoalId() {
        Map<UUID, String> updatedGoals = new HashMap<>();
        updatedGoals.put(goalId, "Updated goal");
        
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        
        // Verificar que se lanza la excepción esperada
        PrometeoExceptions exception = org.junit.jupiter.api.Assertions.assertThrows(
            PrometeoExceptions.class,
            () -> goalService.updateUserGoal(updatedGoals)
        );
        assertEquals("Meta no encontrada.", exception.getMessage());
    }

    @Test
    public void testUpdateUserGoalWithEmptyMap() {
        Map<UUID, String> updatedGoals = new HashMap<>();
        
        goalService.updateUserGoal(updatedGoals);
        
        verify(goalRepository, never()).findById(any());
        verify(recommendationService, never()).recommendRoutines(any());
    }

    @Test
    public void testDeleteGoal() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(testGoal));
        when(recommendationRepository.findByUserIdAndActive(userId, true)).thenReturn(recommendationList);
        
        goalService.deleteGoal(goalId);
        
        verify(goalRepository, times(1)).findById(goalId);
        verify(goalRepository, times(1)).save(testGoal);
        assertFalse(testGoal.isActive());
        verify(recommendationRepository, times(1)).findByUserIdAndActive(userId, true);
        verify(recommendationRepository, times(1)).saveAll(recommendationList);
        verify(recommendationService, times(1)).recommendRoutines(userId);
    }    @Test
    public void testDeleteGoalWithInvalidGoalId() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        
        // Verificar que se lanza la excepción esperada
        PrometeoExceptions exception = org.junit.jupiter.api.Assertions.assertThrows(
            PrometeoExceptions.class,
            () -> goalService.deleteGoal(goalId)
        );
        assertEquals("Meta no encontrada.", exception.getMessage());
    }
}