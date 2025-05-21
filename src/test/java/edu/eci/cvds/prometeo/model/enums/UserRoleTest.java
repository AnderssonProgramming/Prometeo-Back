package edu.eci.cvds.prometeo.model.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserRoleTest {

    @Test
    public void testEnumValues() {
        // Verify all expected values exist
        UserRole[] roles = UserRole.values();
        assertEquals(3, roles.length);
        
        assertEquals(UserRole.STUDENT, roles[0]);
        assertEquals(UserRole.TRAINER, roles[1]);
        assertEquals(UserRole.ADMIN, roles[2]);
    }
    
    @Test
    public void testEnumValueOf() {
        // Test valueOf method (provided by all enums)
        assertEquals(UserRole.STUDENT, UserRole.valueOf("STUDENT"));
        assertEquals(UserRole.TRAINER, UserRole.valueOf("TRAINER"));
        assertEquals(UserRole.ADMIN, UserRole.valueOf("ADMIN"));
    }
    
}