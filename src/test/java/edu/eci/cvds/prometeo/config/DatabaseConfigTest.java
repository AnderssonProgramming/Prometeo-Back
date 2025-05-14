package edu.eci.cvds.prometeo.config;

import io.github.cdimascio.dotenv.Dotenv;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import javax.sql.DataSource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConfigTest {

    @Test
    public void testGetValueWithDotenvValue() {
        // Arrange
        DatabaseConfig config = new DatabaseConfig();
        Dotenv mockDotenv = mock(Dotenv.class);
        when(mockDotenv.get("TEST_KEY")).thenReturn("test_value");
        
        // Act
        String result = (String) ReflectionTestUtils.invokeMethod(
            config, 
            "getValue",
            mockDotenv, 
            "TEST_KEY", 
            "default_value"
        );
        
        // Assert
        assertEquals("test_value", result);
    }
    
    @Test
    public void testGetValueWithEmptyDotenvValue() {
        // Arrange
        DatabaseConfig config = new DatabaseConfig();
        Dotenv mockDotenv = mock(Dotenv.class);
        when(mockDotenv.get("TEST_KEY")).thenReturn("");
        
        // Act
        String result = (String) ReflectionTestUtils.invokeMethod(
            config, 
            "getValue",
            mockDotenv, 
            "TEST_KEY", 
            "default_value"
        );
        
        // Assert
        assertEquals("default_value", result);
    }
    
    @Test
    public void testGetValueWithNullDotenvValue() {
        // Arrange
        DatabaseConfig config = new DatabaseConfig();
        Dotenv mockDotenv = mock(Dotenv.class);
        when(mockDotenv.get("TEST_KEY")).thenReturn(null);
        
        // Act
        String result = (String) ReflectionTestUtils.invokeMethod(
            config, 
            "getValue",
            mockDotenv, 
            "TEST_KEY", 
            "default_value"
        );
        
        // Assert
        // This will return either the system environment value if set,
        // or the default value if not set
        assertNotNull(result);
    }

    @Test
    public void testDataSourceCreation() {
        // Arrange
        DatabaseConfig config = new DatabaseConfig();
        
        // Act
        DataSource dataSource = config.dataSource();
        
        // Assert
        assertNotNull(dataSource);
    }
}