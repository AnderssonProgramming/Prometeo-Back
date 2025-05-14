package edu.eci.cvds.prometeo.model;


import java.util.UUID;

import org.junit.jupiter.api.Test;

import edu.eci.cvds.prometeo.model.base.BaseEntity;

import static org.junit.jupiter.api.Assertions.*;




public class GoalTest {
    
    @Test
    public void testConstructor() {
        Goal goal = new Goal();
        assertNotNull(goal,"New Goal object should not be null" );
    }
    
    @Test
    public void testUserIdGetterAndSetter() {
        Goal goal = new Goal();
        UUID userId = UUID.randomUUID();
        
        goal.setUserId(userId);
        assertEquals(userId, goal.getUserId(),"UserId should be the one that was set");
    }
    
    @Test
    public void testGoalGetterAndSetter() {
        Goal goal = new Goal();
        String goalText = "Complete project by end of month";
        
        goal.setGoal(goalText);
        assertNotEquals("Goal text should be the one that was set", goalText, goal.getGoal());
    }
    
    @Test
    public void testActiveGetterAndSetter() {
        Goal goal = new Goal();
        
        goal.setActive(true);
        assertTrue(goal.isActive(),"Active should be true when set to true");
        
        goal.setActive(false);
        assertFalse(goal.isActive(),"Active should be false when set to false");
    }
    
    @Test
    public void testInheritanceFromBaseEntity() {
        Goal goal = new Goal();
        assertTrue(goal instanceof BaseEntity,"Goal should be an instance of BaseEntity");
    }
}