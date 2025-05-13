package edu.eci.cvds.prometeo.model.base;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BaseEntityTest {
    
    // Concrete implementation of BaseEntity for testing
    private static class TestEntity extends BaseEntity {
        // No additional implementation needed
    }
    
    @Test
    public void testOnCreate() {
        TestEntity entity = new TestEntity();
        entity.onCreate();
        
        assertNotNull(entity.getCreatedAt(),"Created date should be set");
        assertTrue(entity.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(5)),"Created date should be recent");
    }
    
    @Test
    public void testOnUpdate() {
        TestEntity entity = new TestEntity();
        entity.onUpdate();
        
        assertNotNull(entity.getUpdatedAt(),"Updated date should be set");
        assertTrue(entity.getUpdatedAt().isAfter(LocalDateTime.now().minusSeconds(5)),"Updated date should be recent");
    }
    
    @Test
    public void testIsDeleted() {
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
    public void testGetId() {
        TestEntity entity = new TestEntity();
        UUID id = UUID.randomUUID();
        
        // Set ID manually since we're not using JPA in the test
        entity.setId(id);
        
        assertEquals(id, entity.getId(),"getId should return the set ID");
    }
    
    @Test
    public void testSettersAndGetters() {
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
}