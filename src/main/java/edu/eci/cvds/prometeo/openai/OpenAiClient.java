package edu.eci.cvds.prometeo.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Component
public class OpenAiClient {
    private static final Logger logger = LoggerFactory.getLogger(OpenAiClient.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String apiUrl;

    public OpenAiClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
        
        // Cargar variables desde .env, similar a DatabaseConfig
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.apiKey = getValue(dotenv, "OPEN_AI_TOKEN", "dummy-key");
        this.apiUrl = getValue(dotenv, "OPEN_AI_MODEL", "https://api.openai.com/v1/chat/completions");
        
        logger.info("OpenAI client initialized with URL: {}", this.apiUrl);
    }

    private String getValue(Dotenv dotenv, String key, String defaultValue) {
        String value = dotenv.get(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    public String queryModel(String prompt) {
        try {
            // Si apiKey es dummy-key, retornar respuesta simulada
            if ("dummy-key".equals(apiKey)) {
                logger.warn("Using dummy API key - this is for development only");
                return "{\"choices\":[{\"message\":{\"content\":\"Esta es una respuesta simulada. Configura OPEN_AI_TOKEN para usar OpenAI.\"}}]}";
            }

            Map<String, Object> payload = Map.of(
                    "model", "gpt-4o",
                    "messages", List.of(Map.of("role", "user", "content", prompt)),
                    "max_tokens", 1000,
                    "temperature", 0.7
            );

            return webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(objectMapper.writeValueAsString(payload))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {
            logger.error("Error querying OpenAI", e);
            return "{\"choices\":[{\"message\":{\"content\":\"Error: " + e.getMessage() + "\"}}]}";
        }
    }
}