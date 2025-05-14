package edu.eci.cvds.prometeo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

@SpringBootTest
class PrometeoApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }
    
    @Test
    void testMainMethod() {
        // This test verifies that the main method calls SpringApplication.run with the correct parameters
        
        try (MockedStatic<SpringApplication> mockedStatic = Mockito.mockStatic(SpringApplication.class)) {
            // Arrange & Act
            String[] args = new String[]{"arg1", "arg2"};
            PrometeoApplication.main(args);
            
            // Assert
            mockedStatic.verify(() -> 
                SpringApplication.run(PrometeoApplication.class, args), 
                Mockito.times(1)
            );
        }
    }
}