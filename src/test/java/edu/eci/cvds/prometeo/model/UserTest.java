package edu.eci.cvds.prometeo.model;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




public class UserTest {

    @Test
    public void testUserCreation() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    public void testIdGetterAndSetter() {
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        assertEquals(id, user.getId());
    }

    @Test
    public void testInstitutionalIdGetterAndSetter() {
        User user = new User();
        String institutionalId = "A12345";
        user.setInstitutionalId(institutionalId);
        assertEquals(institutionalId, user.getInstitutionalId());
    }

    @Test
    public void testNameGetterAndSetter() {
        User user = new User();
        String name = "John Doe";
        user.setName(name);
        assertEquals(name, user.getName());
    }

    @Test
    public void testWeightGetterAndSetter() {
        User user = new User();
        Double weight = 75.5;
        user.setWeight(weight);
        assertEquals(weight, user.getWeight());
    }

    @Test
    public void testHeightGetterAndSetter() {
        User user = new User();
        Double height = 180.0;
        user.setHeight(height);
        assertEquals(height, user.getHeight());
    }

    @Test
    public void testRoleGetterAndSetter() {
        User user = new User();
        String role = "TRAINER";
        user.setRole(role);
        assertEquals(role, user.getRole());
    }
}