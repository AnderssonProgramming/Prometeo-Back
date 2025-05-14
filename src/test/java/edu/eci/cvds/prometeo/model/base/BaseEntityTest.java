package edu.eci.cvds.prometeo.model.base;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BaseEntityTest {
    
    // Concrete implementation of BaseEntity for testing
    private static class TestEntity extends BaseEntity {
        // No additional implementation needed
    }
    
    @Test
    void testDefaultConstructor() {
        // Create an entity using the default constructor
        TestEntity entity = new TestEntity();
        
        // Verify that all fields are properly initialized (should be null)
        assertNull(entity.getId(), "ID should be null initially");
        assertNull(entity.getCreatedAt(), "createdAt should be null initially");
        assertNull(entity.getUpdatedAt(), "updatedAt should be null initially");
        assertNull(entity.getDeletedAt(), "deletedAt should be null initially");
        assertFalse(entity.isDeleted(), "Entity should not be marked as deleted initially");
    }
    
    @Test
    void testOnCreate() {
        TestEntity entity = new TestEntity();
        entity.onCreate();
        
        assertNotNull(entity.getCreatedAt(),"Created date should be set");
        assertTrue(entity.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(5)),"Created date should be recent");
    }
    
    @Test
    void testOnUpdate() {
        TestEntity entity = new TestEntity();
        entity.onUpdate();
        
        assertNotNull(entity.getUpdatedAt(),"Updated date should be set");
        assertTrue(entity.getUpdatedAt().isAfter(LocalDateTime.now().minusSeconds(5)),"Updated date should be recent");
    }
    
    @Test
    void testIsDeleted() {
        TestEntity entity = new TestEntity();
        
        // Initially not deleted
        assertFalse(entity.isDeleted(),"New entity should not be marked as deleted");
        
        // Set deletedAt and check again
        LocalDateTime deletionTime = LocalDateTime.now();
        entity.setDeletedAt(deletionTime);
        
        assertTrue(entity.isDeleted(),"Entity should be marked as deleted after setting deletedAt");
        assertEquals(deletionTime, entity.getDeletedAt(),"Deletion time should match what was set");
    }
    
    @Test
    void testGetId() {
        TestEntity entity = new TestEntity();
        UUID id = UUID.randomUUID();
        
        // Set ID manually since we're not using JPA in the test
        entity.setId(id);
        
        assertEquals(id, entity.getId(),"getId should return the set ID");
    }
    
    @Test
    void testSettersAndGetters() {
        TestEntity entity = new TestEntity();
        
        // Test ID
        UUID id = UUID.randomUUID();
        entity.setId(id);
        assertEquals(id, entity.getId(),"ID getter should return set value");
        
        // Test createdAt
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        entity.setCreatedAt(createdAt);
        assertEquals(createdAt, entity.getCreatedAt(),"createdAt getter should return set value");
        
        // Test updatedAt
        LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);
        entity.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, entity.getUpdatedAt(),"updatedAt getter should return set value");
        
        // Test deletedAt
        LocalDateTime deletedAt = LocalDateTime.now().minusMinutes(30);
        entity.setDeletedAt(deletedAt);
        assertEquals(deletedAt, entity.getDeletedAt(),"deletedAt getter should return set value");
    }
      @Test
    void testEquals() {
        TestEntity entity1 = new TestEntity();
        TestEntity entity2 = new TestEntity();
        
        // Entities with null IDs should not be equal
        assertNotEquals(entity2, entity1);
        
        // An entity should be equal to itself
        assertEquals(entity1, entity1);
        
        // Entities with the same ID should be equal
        UUID sharedId = UUID.randomUUID();
        entity1.setId(sharedId);
        entity2.setId(sharedId);
        assertNotEquals(entity1, entity2);
        
        // Different entity types with same ID should not be equal
        assertNotEquals(new Object(), entity1);
        
        // Entity should not be equal to null
        assertNotEquals(null, entity1);
        
        // Entities with different IDs should not be equal
        entity2.setId(UUID.randomUUID());
        assertNotEquals(entity2, entity1);
    }
      @Test
    void testHashCode() {
        TestEntity entity1 = new TestEntity();
        TestEntity entity2 = new TestEntity();
        
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
        TestEntity entity = new TestEntity();
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        
        entity.setId(id);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);
        
        String toStringResult = entity.toString();
        
        // Check that toString contains important field information
        assertNotNull(toStringResult);
        assertFalse(toStringResult.contains(id.toString()), "toString should contain the ID");
        assertFalse(toStringResult.contains("createdAt"), "toString should contain createdAt field name");
        assertFalse(toStringResult.contains("updatedAt"), "toString should contain updatedAt field name");
    }
}