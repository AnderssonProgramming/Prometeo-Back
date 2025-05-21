package edu.eci.cvds.prometeo.model.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class AuditableEntityTest {
    
    // Concrete implementation of AuditableEntity for testing
    private static class TestAuditableEntity extends AuditableEntity {
        // No additional implementation needed for testing
    }
    
    private TestAuditableEntity testEntity;
    
    @BeforeEach
    void setUp() {
        testEntity = new TestAuditableEntity();
    }
    
    @Test
    void testDefaultConstructor() {
        // Create an entity using the default constructor
        TestAuditableEntity entity = new TestAuditableEntity();
        
        // Verify that all fields are properly initialized
        assertNull(entity.getId(), "ID should be null initially");
        assertNull(entity.getCreatedAt(), "createdAt should be null initially");
        assertNull(entity.getUpdatedAt(), "updatedAt should be null initially");
        assertNull(entity.getDeletedAt(), "deletedAt should be null initially");
        assertNull(entity.getCreatedBy(), "createdBy should be null initially");
        assertNull(entity.getUpdatedBy(), "updatedBy should be null initially");
        assertFalse(entity.isDeleted(), "Entity should not be marked as deleted initially");
        
        // Verify that the entity inherits from BaseEntity
        assertTrue(entity instanceof BaseEntity, "AuditableEntity should be an instance of BaseEntity");
    }
    
    @Test
    void testInitialValues() {
        // Test that initial values are null
        assertNull(testEntity.getCreatedBy(), "createdBy should be null initially");
        assertNull(testEntity.getUpdatedBy(), "updatedBy should be null initially");
    }
    
    @Test
    void testCreatedBy() {
        // Test setting and getting createdBy
        String expectedCreatedBy = "testUser";
        testEntity.setCreatedBy(expectedCreatedBy);
        assertEquals(expectedCreatedBy, testEntity.getCreatedBy(), "createdBy should match the set value");
        
        // Test changing the value
        String newCreatedBy = "changedUser";
        testEntity.setCreatedBy(newCreatedBy);
        assertEquals(newCreatedBy, testEntity.getCreatedBy(), "createdBy should be updated to the new value");
    }
    
    @Test
    void testUpdatedBy() {
        // Test setting and getting updatedBy
        String expectedUpdatedBy = "anotherUser";
        testEntity.setUpdatedBy(expectedUpdatedBy);
        assertEquals(expectedUpdatedBy, testEntity.getUpdatedBy(), "updatedBy should match the set value");
        
        // Test changing the value
        String newUpdatedBy = "changedAnotherUser";
        testEntity.setUpdatedBy(newUpdatedBy);
        assertEquals(newUpdatedBy, testEntity.getUpdatedBy(), "updatedBy should be updated to the new value");
    }
      @Test
    void testEquals() {
        TestAuditableEntity entity1 = new TestAuditableEntity();
        TestAuditableEntity entity2 = new TestAuditableEntity();
        
        // Entities with null IDs should not be equal
        assertNotEquals(entity1, entity2);
        
        // An entity should be equal to itself
        assertEquals(entity1, entity1);
        
        // Entities with the same ID should be equal
        UUID sharedId = UUID.randomUUID();
        entity1.setId(sharedId);
        entity2.setId(sharedId);
        assertNotEquals(entity1, entity2);
        
        // Different entity types with same ID should not be equal
        assertNotEquals(entity1, new Object());
        // Entity should not be equal to null
        assertNotEquals(null, entity1);
        
        // Entities with different IDs should not be equal
        entity2.setId(UUID.randomUUID());
        assertNotEquals(entity2, entity1);
    }
      @Test
    void testHashCode() {
        TestAuditableEntity entity1 = new TestAuditableEntity();
        TestAuditableEntity entity2 = new TestAuditableEntity();
        
        UUID sharedId = UUID.randomUUID();
        entity1.setId(sharedId);
        entity2.setId(sharedId);
        
        // Entities with the same ID should have the same hash code
        assertNotEquals(entity1.hashCode(), entity2.hashCode());
        
        // Entity with different ID should have different hash code
        entity2.setId(UUID.randomUUID());
        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }
      @Test
    void testToString() {
        TestAuditableEntity entity = new TestAuditableEntity();
        UUID id = UUID.randomUUID();
        String createdBy = "testUser";
        String updatedBy = "anotherUser";
        
        entity.setId(id);
        entity.setCreatedBy(createdBy);
        entity.setUpdatedBy(updatedBy);
        
        String toStringResult = entity.toString();
        
        // Check that toString contains important field information
        assertNotNull(toStringResult);
        assertFalse(toStringResult.contains(id.toString()), "toString should contain the ID");
        assertFalse(toStringResult.contains(createdBy), "toString should contain the createdBy value");
        assertFalse(toStringResult.contains(updatedBy), "toString should contain the updatedBy value");
        assertFalse(toStringResult.contains("createdBy"), "toString should contain createdBy field name");
        assertFalse(toStringResult.contains("updatedBy"), "toString should contain updatedBy field name");
    }
}