package edu.eci.cvds.prometeo.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;





@WebMvcTest
@Import(SecurityConfig.class)
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // @Test
    // public void shouldAllowAccessToAllEndpoints() throws Exception {
    //     // Test that any path is accessible without authentication
    //     mockMvc.perform(MockMvcRequestBuilders.get("/any/path"))
    //            .andExpect(status().isOk());
    // }

    // @Test
    // public void shouldAllowPostRequestsWithoutCsrfToken() throws Exception {
    //     // Test that POST requests are allowed without CSRF token (since CSRF is disabled)
    //     mockMvc.perform(MockMvcRequestBuilders.post("/any/path"))
    //            .andExpect(status().isOk());
    // }

    // @Test
    // public void shouldNotUseFormLogin() throws Exception {
    //     // Test that form login is not used (should not redirect to login page)
    //     mockMvc.perform(MockMvcRequestBuilders.get("/any/protected/resource"))
    //            .andExpect(status().isOk()); // Should not redirect to login
    // }

    // @Test
    // public void shouldNotRequireBasicAuth() throws Exception {
    //     // Test that basic auth is not required
    //     mockMvc.perform(MockMvcRequestBuilders.get("/any/path")
    //            .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "invalid")))
    //            .andExpect(status().isOk()); // Should still allow access with invalid credentials
    // }
}