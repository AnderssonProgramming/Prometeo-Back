package edu.eci.cvds.prometeo.model;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import edu.eci.cvds.prometeo.model.base.BaseEntity;

import static org.junit.jupiter.api.Assertions.*;




public class RecommendationTest {

    @Test
    public void testNoArgsConstructor() {
        Recommendation recommendation = new Recommendation();
        assertNull(recommendation.getUser());
        assertNull(recommendation.getRoutine());
        assertFalse(recommendation.isActive());
        assertEquals(0, recommendation.getWeight());
    }

    @Test
    public void testAllArgsConstructor() {
        // Create mock objects
        User user = new User();
        Routine routine = new Routine();
        boolean active = true;
        int weight = 5;
        
        Recommendation recommendation = new Recommendation(user, routine, active, weight);
        
        assertEquals(user, recommendation.getUser());
        assertEquals(routine, recommendation.getRoutine());
        assertTrue(recommendation.isActive());
        assertEquals(weight, recommendation.getWeight());
    }

    @Test
    public void testUserGetterSetter() {
        Recommendation recommendation = new Recommendation();
        User user = new User();
        
        recommendation.setUser(user);
        assertEquals(user, recommendation.getUser());
    }

    @Test
    public void testRoutineGetterSetter() {
        Recommendation recommendation = new Recommendation();
        Routine routine = new Routine();
        
        recommendation.setRoutine(routine);
        assertEquals(routine, recommendation.getRoutine());
    }

    @Test
    public void testActiveGetterSetter() {
        Recommendation recommendation = new Recommendation();
        
        recommendation.setActive(true);
        assertTrue(recommendation.isActive());
        
        recommendation.setActive(false);
        assertFalse(recommendation.isActive());
    }

    @Test
    public void testWeightGetterSetter() {
        Recommendation recommendation = new Recommendation();
        int weight = 10;
        
        recommendation.setWeight(weight);
        assertEquals(weight, recommendation.getWeight());
    }
    
    @Test
    public void testInheritanceFromBaseEntity() {
        Recommendation recommendation = new Recommendation();
        assertTrue(recommendation instanceof BaseEntity);
    }
}