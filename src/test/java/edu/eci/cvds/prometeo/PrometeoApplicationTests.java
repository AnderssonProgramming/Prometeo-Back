package edu.eci.cvds.prometeo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = PrometeoApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.main.banner-mode=off",
    "logging.level.org.springframework=ERROR"
})
class PrometeoApplicationTests {

    @Test
    void contextLoads() {
        // Test vacío que sólo verifica que se cargue el contexto
    }
}