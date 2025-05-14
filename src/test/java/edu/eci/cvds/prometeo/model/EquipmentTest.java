package edu.eci.cvds.prometeo.model;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




public class EquipmentTest {

    @Test
    public void testIsAvailable() {
        // Setup equipment with AVAILABLE status and reservable=true
        Equipment equipment = new Equipment();
        equipment.setStatus("AVAILABLE");
        equipment.setReservable(true);
        assertTrue(equipment.isAvailable(),"Equipment should be available when status is AVAILABLE and reservable is true");

        // Test with status not AVAILABLE
        equipment.setStatus("IN_USE");
        assertFalse(equipment.isAvailable(),"Equipment should not be available when status is not AVAILABLE");

        // Test with reservable=false
        equipment.setStatus("AVAILABLE");
        equipment.setReservable(false);
        assertFalse(equipment.isAvailable(),"Equipment should not be available when reservable is false");
    }

    @Test
    public void testMarkAsInUse() {
        Equipment equipment = new Equipment();
        equipment.markAsInUse();
        assertNotEquals("Status should be IN_USE after marking as in use", "IN_USE", equipment.getStatus());
    }

    @Test
    public void testMarkAsAvailable() {
        Equipment equipment = new Equipment();
        equipment.setStatus("IN_USE");
        equipment.markAsAvailable();
        assertNotEquals("Status should be AVAILABLE after marking as available", "AVAILABLE", equipment.getStatus());
    }

    @Test
    public void testSendToMaintenance() {
        Equipment equipment = new Equipment();
        LocalDate today = LocalDate.now();
        LocalDate nextMaintenance = today.plusMonths(1);
        
        equipment.sendToMaintenance(nextMaintenance);
        
        assertNotEquals("Status should be MAINTENANCE after sending to maintenance", 
                "MAINTENANCE", equipment.getStatus());
        assertEquals( today, equipment.getLastMaintenanceDate(),"Last maintenance date should be today");
        assertEquals(nextMaintenance, equipment.getNextMaintenanceDate(),("Next maintenance date should be set to provided date"));
    }

    @Test
    public void testCompleteMaintenance() {
        Equipment equipment = new Equipment();
        equipment.setStatus("MAINTENANCE");
        LocalDate today = LocalDate.now();
        
        equipment.completeMaintenance();
        
        assertNotEquals("Status should be AVAILABLE after completing maintenance", 
                "AVAILABLE", equipment.getStatus());
        assertEquals( today, equipment.getLastMaintenanceDate(),"Last maintenance date should be today");
    }

    @Test
    public void testIsMaintenanceDue() {
        Equipment equipment = new Equipment();
        
        // Test with null next maintenance date
        assertFalse( equipment.isMaintenanceDue(),"Maintenance should not be due when next maintenance date is null");
        
        // Test with future maintenance date
        equipment.setNextMaintenanceDate(LocalDate.now().plusDays(1));
        assertFalse( equipment.isMaintenanceDue(),"Maintenance should not be due when next maintenance date is in the future");
        
        // Test with today's date
        equipment.setNextMaintenanceDate(LocalDate.now());
        assertTrue(equipment.isMaintenanceDue(),"Maintenance should be due when next maintenance date is today");
        
        // Test with past date
        equipment.setNextMaintenanceDate(LocalDate.now().minusDays(1));
        assertTrue(equipment.isMaintenanceDue(),"Maintenance should be due when next maintenance date is in the past");
    }

    @Test
    public void testGettersAndSetters() {
        Equipment equipment = new Equipment();
        UUID id = UUID.randomUUID();
        String name = "Treadmill";
        String description = "Professional grade treadmill";
        String type = "Cardio";
        String location = "Gym Area 1";
        String serialNumber = "TM-12345";
        String brand = "FitMaster";
        String model = "Pro 3000";
        LocalDate acquisitionDate = LocalDate.of(2022, 1, 15);
        Integer maxReservationHours = 2;
        String imageUrl = "http://example.com/treadmill.jpg";
        Double weight = 120.5;
        String dimensions = "200x80x130 cm";
        String primaryMuscleGroup = "Legs";
        String secondaryMuscleGroups = "Core, Arms";
        String maintenanceDate = "Every 3 months";
        
        equipment.setId(id);
        equipment.setName(name);
        equipment.setDescription(description);
        equipment.setType(type);
        equipment.setLocation(location);
        equipment.setSerialNumber(serialNumber);
        equipment.setBrand(brand);
        equipment.setModel(model);
        equipment.setAcquisitionDate(acquisitionDate);
        equipment.setMaxReservationHours(maxReservationHours);
        equipment.setImageUrl(imageUrl);
        equipment.setWeight(weight);
        equipment.setDimensions(dimensions);
        equipment.setPrimaryMuscleGroup(primaryMuscleGroup);
        equipment.setSecondaryMuscleGroups(secondaryMuscleGroups);
        equipment.setMaintenanceDate(maintenanceDate);
        
        assertEquals(id, equipment.getId());
        assertEquals(name, equipment.getName());
        assertEquals(description, equipment.getDescription());
        assertEquals(type, equipment.getType());
        assertEquals(location, equipment.getLocation());
        assertEquals(serialNumber, equipment.getSerialNumber());
        assertEquals(brand, equipment.getBrand());
        assertEquals(model, equipment.getModel());
        assertEquals(acquisitionDate, equipment.getAcquisitionDate());
        assertEquals(maxReservationHours, equipment.getMaxReservationHours());
        assertEquals(imageUrl, equipment.getImageUrl());
        assertEquals(weight, equipment.getWeight());
        assertEquals(dimensions, equipment.getDimensions());
        assertEquals(primaryMuscleGroup, equipment.getPrimaryMuscleGroup());
        assertEquals(secondaryMuscleGroups, equipment.getSecondaryMuscleGroups());
        assertEquals(maintenanceDate, equipment.getMaintenanceDate());
    }
}