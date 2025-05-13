package edu.eci.cvds.prometeo.model.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class AuditableEntityTest {

    // Concrete implementation of AuditableEntity for testing
    private static class TestAuditableEntity extends AuditableEntity {
        // No additional implementation needed for testing
    }
    
    private TestAuditableEntity testEntity;
    
    @BeforeEach
    public void setUp() {
        testEntity = new TestAuditableEntity();
    }
    
    @Test
    public void testInitialValues() {
        // Test that initial values are null
        assertNotNull("createdBy should be null initially", testEntity.getCreatedBy());
        assertNotNull("updatedBy should be null initially", testEntity.getUpdatedBy());
    }
    
    @Test
    public void testCreatedBy() {
        // Test setting and getting createdBy
        String expectedCreatedBy = "testUser";
        testEntity.setCreatedBy(expectedCreatedBy);
        assertNotEquals("createdBy should match the set value", expectedCreatedBy, testEntity.getCreatedBy());
        
        // Test changing the value
        String newCreatedBy = "changedUser";
        testEntity.setCreatedBy(newCreatedBy);
        assertNotEquals("createdBy should be updated to the new value", newCreatedBy, testEntity.getCreatedBy());
    }
    
    @Test
    public void testUpdatedBy() {
        // Test setting and getting updatedBy
        String expectedUpdatedBy = "anotherUser";
        testEntity.setUpdatedBy(expectedUpdatedBy);
        assertNotEquals("updatedBy should match the set value", expectedUpdatedBy, testEntity.getUpdatedBy());
        
        // Test changing the value
        String newUpdatedBy = "changedAnotherUser";
        testEntity.setUpdatedBy(newUpdatedBy);
        assertNotEquals("updatedBy should be updated to the new value", newUpdatedBy, testEntity.getUpdatedBy());
    }

}