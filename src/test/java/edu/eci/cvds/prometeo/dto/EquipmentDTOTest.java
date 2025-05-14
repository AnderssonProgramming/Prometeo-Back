package edu.eci.cvds.prometeo.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;





public class EquipmentDTOTest {

    @Test
    public void testDefaultConstructor() {
        EquipmentDTO equipment = new EquipmentDTO();
        assertNull(equipment.getId());
        assertNull(equipment.getName());
        assertNull(equipment.getDescription());
        assertNull(equipment.getType());
        assertNull(equipment.getLocation());
        assertNull(equipment.getStatus());
        assertNull(equipment.getSerialNumber());
        assertNull(equipment.getBrand());
        assertNull(equipment.getModel());
        assertNull(equipment.getAcquisitionDate());
        assertNull(equipment.getLastMaintenanceDate());
        assertNull(equipment.getNextMaintenanceDate());
        assertTrue(equipment.isReservable());
        assertNull(equipment.getMaxReservationHours());
        assertNull(equipment.getImageUrl());
        assertNull(equipment.getWeight());
        assertNull(equipment.getDimensions());
        assertNull(equipment.getPrimaryMuscleGroup());
        assertNull(equipment.getSecondaryMuscleGroups());
    }

    @Test
    public void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        String name = "Test Equipment";
        String description = "Test Description";
        String type = "Test Type";
        String location = "Test Location";
        String status = "Available";
        String serialNumber = "SN12345";
        String brand = "Test Brand";
        String model = "Test Model";
        LocalDate acquisitionDate = LocalDate.now();
        LocalDate lastMaintenanceDate = LocalDate.now().minusDays(30);
        LocalDate nextMaintenanceDate = LocalDate.now().plusDays(30);
        boolean reservable = false;
        Integer maxReservationHours = 2;
        String imageUrl = "http://example.com/image.jpg";
        Double weight = 10.5;
        String dimensions = "10x20x30";
        String primaryMuscleGroup = "Chest";
        String secondaryMuscleGroups = "Triceps, Shoulders";

        EquipmentDTO equipment = new EquipmentDTO(id, name, description, type, location, status, serialNumber, 
                                  brand, model, acquisitionDate, lastMaintenanceDate, nextMaintenanceDate,
                                  reservable, maxReservationHours, imageUrl, weight, dimensions, 
                                  primaryMuscleGroup, secondaryMuscleGroups);

        assertEquals(id, equipment.getId());
        assertEquals(name, equipment.getName());
        assertEquals(description, equipment.getDescription());
        assertEquals(type, equipment.getType());
        assertEquals(location, equipment.getLocation());
        assertEquals(status, equipment.getStatus());
        assertEquals(serialNumber, equipment.getSerialNumber());
        assertEquals(brand, equipment.getBrand());
        assertEquals(model, equipment.getModel());
        assertEquals(acquisitionDate, equipment.getAcquisitionDate());
        assertEquals(lastMaintenanceDate, equipment.getLastMaintenanceDate());
        assertEquals(nextMaintenanceDate, equipment.getNextMaintenanceDate());
        assertEquals(reservable, equipment.isReservable());
        assertEquals(maxReservationHours, equipment.getMaxReservationHours());
        assertEquals(imageUrl, equipment.getImageUrl());
        assertEquals(weight, equipment.getWeight());
        assertEquals(dimensions, equipment.getDimensions());
        assertEquals(primaryMuscleGroup, equipment.getPrimaryMuscleGroup());
        assertEquals(secondaryMuscleGroups, equipment.getSecondaryMuscleGroups());
    }

    @Test
    public void testGettersAndSetters() {
        EquipmentDTO equipment = new EquipmentDTO();
        
        UUID id = UUID.randomUUID();
        equipment.setId(id);
        assertEquals(id, equipment.getId());
        
        String name = "Test Equipment";
        equipment.setName(name);
        assertEquals(name, equipment.getName());
        
        String description = "Test Description";
        equipment.setDescription(description);
        assertEquals(description, equipment.getDescription());
        
        String type = "Test Type";
        equipment.setType(type);
        assertEquals(type, equipment.getType());
        
        String location = "Test Location";
        equipment.setLocation(location);
        assertEquals(location, equipment.getLocation());
        
        String status = "Available";
        equipment.setStatus(status);
        assertEquals(status, equipment.getStatus());
        
        String serialNumber = "SN12345";
        equipment.setSerialNumber(serialNumber);
        assertEquals(serialNumber, equipment.getSerialNumber());
        
        String brand = "Test Brand";
        equipment.setBrand(brand);
        assertEquals(brand, equipment.getBrand());
        
        String model = "Test Model";
        equipment.setModel(model);
        assertEquals(model, equipment.getModel());
        
        LocalDate acquisitionDate = LocalDate.now();
        equipment.setAcquisitionDate(acquisitionDate);
        assertEquals(acquisitionDate, equipment.getAcquisitionDate());
        
        LocalDate lastMaintenanceDate = LocalDate.now().minusDays(30);
        equipment.setLastMaintenanceDate(lastMaintenanceDate);
        assertEquals(lastMaintenanceDate, equipment.getLastMaintenanceDate());
        
        LocalDate nextMaintenanceDate = LocalDate.now().plusDays(30);
        equipment.setNextMaintenanceDate(nextMaintenanceDate);
        assertEquals(nextMaintenanceDate, equipment.getNextMaintenanceDate());
        
        boolean reservable = false;
        equipment.setReservable(reservable);
        assertEquals(reservable, equipment.isReservable());
        
        Integer maxReservationHours = 2;
        equipment.setMaxReservationHours(maxReservationHours);
        assertEquals(maxReservationHours, equipment.getMaxReservationHours());
        
        String imageUrl = "http://example.com/image.jpg";
        equipment.setImageUrl(imageUrl);
        assertEquals(imageUrl, equipment.getImageUrl());
        
        Double weight = 10.5;
        equipment.setWeight(weight);
        assertEquals(weight, equipment.getWeight());
        
        String dimensions = "10x20x30";
        equipment.setDimensions(dimensions);
        assertEquals(dimensions, equipment.getDimensions());
        
        String primaryMuscleGroup = "Chest";
        equipment.setPrimaryMuscleGroup(primaryMuscleGroup);
        assertEquals(primaryMuscleGroup, equipment.getPrimaryMuscleGroup());
        
        String secondaryMuscleGroups = "Triceps, Shoulders";
        equipment.setSecondaryMuscleGroups(secondaryMuscleGroups);
        assertEquals(secondaryMuscleGroups, equipment.getSecondaryMuscleGroups());
    }

    @Test
    public void testEqualsAndHashCode() {
        EquipmentDTO equipment1 = new EquipmentDTO();
        EquipmentDTO equipment2 = new EquipmentDTO();
        
        UUID id = UUID.randomUUID();
        equipment1.setId(id);
        equipment2.setId(id);
        
        equipment1.setName("Equipment");
        equipment2.setName("Equipment");
        
        assertEquals(equipment1, equipment2);
        assertEquals(equipment1.hashCode(), equipment2.hashCode());
        
        equipment2.setName("Different Equipment");
        assertNotEquals(equipment1, equipment2);
    }

    @Test
    void testToString() {
        // Arrange
        EquipmentDTO equipment = new EquipmentDTO();
        UUID id = UUID.randomUUID();
        String name = "Treadmill";
        String brand = "FitPro";
        
        equipment.setId(id);
        equipment.setName(name);
        equipment.setBrand(brand);
        
        // Act
        String toString = equipment.toString();
        
        // Assert
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(name));
        assertTrue(toString.contains(brand));
    }
}