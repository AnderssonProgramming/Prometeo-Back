package edu.eci.cvds.prometeo.openai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OpenAiPropertiesTest {

    private OpenAiProperties properties;

    @BeforeEach
    public void setUp() {
        properties = new OpenAiProperties();
    }

    @Test
    public void testDefaultValues() {
        // Default values should be null
        assertNull(properties.getApiKey());
        assertNull(properties.getApiUrl());
    }

    @Test
    public void testApiKeyGetterAndSetter() {
        String testApiKey = "test-api-key-12345";
        properties.setApiKey(testApiKey);
        assertEquals(testApiKey, properties.getApiKey());
    }

    @Test
    public void testApiUrlGetterAndSetter() {
        String testApiUrl = "https://api.openai.com/v1";
        properties.setApiUrl(testApiUrl);
        assertEquals(testApiUrl, properties.getApiUrl());
    }
}