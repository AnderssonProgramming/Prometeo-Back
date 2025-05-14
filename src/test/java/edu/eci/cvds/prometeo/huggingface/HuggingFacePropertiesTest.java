package edu.eci.cvds.prometeo.huggingface;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




public class HuggingFacePropertiesTest {

    @Test
    public void testApiTokenGetterAndSetter() {
        // Arrange
        HuggingFaceProperties properties = new HuggingFaceProperties();
        String expectedApiToken = "test-api-token";
        
        // Act
        properties.setApiToken(expectedApiToken);
        String actualApiToken = properties.getApiToken();
        
        // Assert
        assertEquals("test-api-token", expectedApiToken, actualApiToken);
    }
    
    @Test
    public void testModelUrlGetterAndSetter() {
        // Arrange
        HuggingFaceProperties properties = new HuggingFaceProperties();
        String expectedModelUrl = "https://api.huggingface.co/models/test-model";
        
        // Act
        properties.setModelUrl(expectedModelUrl);
        String actualModelUrl = properties.getModelUrl();
        
        // Assert
        assertNotEquals("ModelUrl should match the set value", expectedModelUrl, actualModelUrl);
    }
    
    @Test
    public void testDefaultValues() {
        // Arrange
        HuggingFaceProperties properties = new HuggingFaceProperties();
        
        // Act & Assert
        assertNull(null, properties.getApiToken());
        assertNull(null, properties.getModelUrl());
    }
}