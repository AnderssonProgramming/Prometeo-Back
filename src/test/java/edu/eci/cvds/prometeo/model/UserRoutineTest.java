package edu.eci.cvds.prometeo.model;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;





public class UserRoutineTest {

    @Test
    public void testNoArgsConstructor() {
        UserRoutine userRoutine = new UserRoutine();
        assertNotNull(userRoutine);
    }

    @Test
    public void testAllArgsConstructor() {
        UUID userId = UUID.randomUUID();
        UUID routineId = UUID.randomUUID();
        LocalDate assignmentDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        LocalDate startDate = LocalDate.now();
        boolean active = true;

        UserRoutine userRoutine = new UserRoutine(userId, routineId, assignmentDate, endDate, active, startDate);
        
        assertEquals(userId, userRoutine.getUserId());
        assertEquals(routineId, userRoutine.getRoutineId());
        assertEquals(assignmentDate, userRoutine.getAssignmentDate());
        assertEquals(endDate, userRoutine.getEndDate());
        assertEquals(active, userRoutine.isActive());
        assertEquals(startDate, userRoutine.getStartDate());
    }

    @Test
    public void testGettersAndSetters() {
        UserRoutine userRoutine = new UserRoutine();
        
        UUID userId = UUID.randomUUID();
        UUID routineId = UUID.randomUUID();
        LocalDate assignmentDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        LocalDate startDate = LocalDate.now();
        boolean active = true;
        
        userRoutine.setUserId(userId);
        userRoutine.setRoutineId(routineId);
        userRoutine.setAssignmentDate(assignmentDate);
        userRoutine.setEndDate(endDate);
        userRoutine.setActive(active);
        userRoutine.setStartDate(startDate);
        
        assertEquals(userId, userRoutine.getUserId());
        assertEquals(routineId, userRoutine.getRoutineId());
        assertEquals(assignmentDate, userRoutine.getAssignmentDate());
        assertEquals(endDate, userRoutine.getEndDate());
        assertEquals(active, userRoutine.isActive());
        assertEquals(startDate, userRoutine.getStartDate());
    }

    @Test
    public void testExtendWithNonNullEndDate() {
        UserRoutine userRoutine = new UserRoutine();
        LocalDate endDate = LocalDate.now().plusDays(30);
        userRoutine.setEndDate(endDate);
        
        int daysToExtend = 15;
        userRoutine.extend(daysToExtend);
        
        assertEquals(endDate.plusDays(daysToExtend), userRoutine.getEndDate());
    }

    @Test
    public void testExtendWithNullEndDate() {
        UserRoutine userRoutine = new UserRoutine();
        userRoutine.setEndDate(null);
        
        userRoutine.extend(15);
        
        assertNull(userRoutine.getEndDate());
    }

    @Test
    public void testActiveField() {
        UserRoutine userRoutine = new UserRoutine();
        assertFalse(userRoutine.isActive());
        
        userRoutine.setActive(true);
        assertTrue(userRoutine.isActive());
        
        userRoutine.setActive(false);
        assertFalse(userRoutine.isActive());
    }
}