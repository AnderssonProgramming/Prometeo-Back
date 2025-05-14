// package edu.eci.cvds.prometeo.config;

// import org.junit.jupiter.api.Test;
// import org.springframework.web.servlet.config.annotation.CorsRegistration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.ArgumentMatchers.anyBoolean;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;





// class CorsConfigTest {

//     @Test
//     void testAddCorsMappings() {
//         // Create the class to test
//         CorsConfig corsConfig = new CorsConfig();
        
//         // Create mocks
//         CorsRegistry registry = mock(CorsRegistry.class);
//         CorsRegistration registration = mock(CorsRegistration.class);
        
//         // Set up method chain
//         when(registry.addMapping(anyString())).thenReturn(registration);
//         when(registration.allowedOrigins(any())).thenReturn(registration);
//         when(registration.allowedMethods(any())).thenReturn(registration);
//         when(registration.allowedHeaders(any())).thenReturn(registration);
//         when(registration.allowCredentials(anyBoolean())).thenReturn(registration);
        
//         // Call the method being tested
//         corsConfig.addCorsMappings(registry);
        
//         // Verify the expected interactions
//         verify(registry).addMapping("/**");
//         verify(registration).allowedOrigins("*");
//         verify(registration).allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
//         verify(registration).allowedHeaders("*");
//         verify(registration).allowCredentials(false);
//     }
// }