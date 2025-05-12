package edu.eci.cvds.prometeo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import jakarta.activation.DataSource;

@SpringBootTest
@MockBean(DataSource.class)
class PrometeoApplicationTests {

	@Test
	void contextLoads() {
	}

}
