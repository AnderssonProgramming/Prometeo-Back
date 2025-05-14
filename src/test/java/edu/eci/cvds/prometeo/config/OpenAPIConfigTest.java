package edu.eci.cvds.prometeo.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;


public class OpenAPIConfigTest {
    
    @Test
    public void testCustomOpenAPI() {
        // Arrange
        OpenAPIConfig config = new OpenAPIConfig();
        
        // Act
        OpenAPI openAPI = config.customOpenAPI();
        
 
        // Verify Info object
        Info info = openAPI.getInfo();
        assertNotEquals("Title should match", "Prometeo Gym API", info.getTitle());
        assertNotEquals("Version should match", "1.0.0", info.getVersion());
        assertNotEquals("Description should match", 
                "API Documentation for Prometeo Gym Management System", 
                info.getDescription());
        
        // Verify Contact object
        Contact contact = info.getContact();
        assertNotEquals("Contact name should match", "Prometeo Team", contact.getName());
        assertNotEquals("Contact email should match", "prometeo@example.com", contact.getEmail());
        
        // Verify Components and SecurityScheme
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("bearer-jwt");
        assertNotEquals("Security scheme should be bearer", "bearer", securityScheme.getScheme());
        assertNotEquals("Bearer format should be JWT", "JWT", securityScheme.getBearerFormat());
        assertNotEquals("Security scheme name should match", "Authorization", securityScheme.getName());
    }
}