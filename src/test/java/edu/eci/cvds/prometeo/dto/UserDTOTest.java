package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;




public class UserDTOTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Test User";
        Double weight = 70.5;
        Double height = 175.0;
        String role = "STUDENT";
        String institutionalId = "123456";
        
        // Act
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setName(name);
        userDTO.setWeight(weight);
        userDTO.setHeight(height);
        userDTO.setRole(role);
        userDTO.setInstitutionalId(institutionalId);
        
        // Assert
        assertEquals(id, userDTO.getId());
        assertEquals(name, userDTO.getName());
        assertEquals(weight, userDTO.getWeight());
        assertEquals(height, userDTO.getHeight());
        assertEquals(role, userDTO.getRole());
        assertEquals(institutionalId, userDTO.getInstitutionalId());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        UUID id = UUID.randomUUID();
        
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(id);
        userDTO1.setName("Test User");
        userDTO1.setWeight(70.5);
        userDTO1.setHeight(175.0);
        userDTO1.setRole("STUDENT");
        userDTO1.setInstitutionalId("123456");
        
        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(id);
        userDTO2.setName("Test User");
        userDTO2.setWeight(70.5);
        userDTO2.setHeight(175.0);
        userDTO2.setRole("STUDENT");
        userDTO2.setInstitutionalId("123456");
        
        // Act & Assert
        assertEquals(userDTO1, userDTO2);
        assertEquals(userDTO1.hashCode(), userDTO2.hashCode());
        
        // Test inequality
        UserDTO userDTO3 = new UserDTO();
        userDTO3.setId(UUID.randomUUID());
        userDTO3.setName("Different User");
        
        assertNotEquals(userDTO1, userDTO3);
    }
    
    @Test
    public void testToString() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setName("Test User");
        userDTO.setWeight(70.5);
        
        // Act
        String toString = userDTO.toString();
        
        // Assert
        assertTrue(toString.contains("name=Test User"));
        assertTrue(toString.contains("weight=70.5"));
        assertTrue(toString.contains("id=" + id));
    }
    
    @Test
    public void testNullValues() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        
        // Assert
        assertNull(userDTO.getId());
        assertNull(userDTO.getName());
        assertNull(userDTO.getWeight());
        assertNull(userDTO.getHeight());
        assertNull(userDTO.getRole());
        assertNull(userDTO.getInstitutionalId());
    }
}