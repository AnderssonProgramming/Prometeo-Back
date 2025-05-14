package edu.eci.cvds.prometeo.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;  

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OpenAiClientTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Dotenv dotenv;

    private OpenAiClient openAiClient;    @BeforeEach
    void setUp() {
        try (MockedStatic<Dotenv> dotenvMockedStatic = mockStatic(Dotenv.class)) {
            // Mock the static method chain
            DotenvBuilder dotenvBuilder = mock(DotenvBuilder.class);
            dotenvMockedStatic.when(Dotenv::configure).thenReturn(dotenvBuilder);
            when(dotenvBuilder.ignoreIfMissing()).thenReturn(dotenvBuilder);
            when(dotenvBuilder.load()).thenReturn(dotenv);
            
            // Mock WebClient.Builder
            when(webClientBuilder.build()).thenReturn(webClient);

            // Default behavior for dotenv
            when(dotenv.get("OPEN_AI_TOKEN")).thenReturn(null);
            when(dotenv.get("OPEN_AI_MODEL")).thenReturn(null);
            
            openAiClient = new OpenAiClient(webClientBuilder, objectMapper);
        }
    }    @Test
    void testQueryModelWithDummyKey() {
        // The API key should be "dummy-key" by default in our setup
        String result = openAiClient.queryModel("Test prompt");
        
        assertEquals("{\"choices\":[{\"message\":{\"content\":\"Esta es una respuesta simulada. Configura OPEN_AI_TOKEN para usar OpenAI.\"}}]}", result);
    }@Test
    void testQueryModelWithValidKey() throws JsonProcessingException {
        // Create a new instance with mocked environment variables
        try (MockedStatic<Dotenv> dotenvMockedStatic = mockStatic(Dotenv.class)) {
            // Setup the dotenv mock chain
            DotenvBuilder dotenvBuilder = mock(DotenvBuilder.class);
            dotenvMockedStatic.when(Dotenv::configure).thenReturn(dotenvBuilder);
            when(dotenvBuilder.ignoreIfMissing()).thenReturn(dotenvBuilder);
            when(dotenvBuilder.load()).thenReturn(dotenv);
            
            when(dotenv.get("OPEN_AI_TOKEN")).thenReturn("real-api-key");
            when(dotenv.get("OPEN_AI_MODEL")).thenReturn("https://api.openai.com/v1/chat/completions");
            when(webClientBuilder.build()).thenReturn(webClient);

            // Set up the WebClient mock chain
            when(webClient.post()).thenReturn(requestBodyUriSpec);
            when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
            when(requestBodySpec.header(eq("Authorization"), anyString())).thenReturn(requestBodySpec);
            when(requestBodySpec.header(eq("Content-Type"), anyString())).thenReturn(requestBodySpec);
            when(requestBodySpec.bodyValue(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("{\"choices\":[{\"message\":{\"content\":\"API response\"}}]}"));

            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            OpenAiClient client = new OpenAiClient(webClientBuilder, objectMapper);
            String result = client.queryModel("Test prompt");

            assertEquals("{\"choices\":[{\"message\":{\"content\":\"API response\"}}]}", result);
            verify(requestBodySpec).header(eq("Authorization"), eq("Bearer real-api-key"));
        }
    }    @Test
    void testQueryModelWithException() throws JsonProcessingException {
        // Create a new instance with mocked environment variables
        try (MockedStatic<Dotenv> dotenvMockedStatic = mockStatic(Dotenv.class)) {
            // Setup the dotenv mock chain
            DotenvBuilder dotenvBuilder = mock(DotenvBuilder.class);
            dotenvMockedStatic.when(Dotenv::configure).thenReturn(dotenvBuilder);
            when(dotenvBuilder.ignoreIfMissing()).thenReturn(dotenvBuilder);
            when(dotenvBuilder.load()).thenReturn(dotenv);
            
            when(dotenv.get("OPEN_AI_TOKEN")).thenReturn("real-api-key");
            when(webClientBuilder.build()).thenReturn(webClient);

            // Set up to throw exception
            when(webClient.post()).thenReturn(requestBodyUriSpec);
            when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
            when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
            when(requestBodySpec.bodyValue(anyString())).thenThrow(new RuntimeException("Test exception"));

            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            OpenAiClient client = new OpenAiClient(webClientBuilder, objectMapper);
            String result = client.queryModel("Test prompt");

            assertTrue(result.contains("Error: Test exception"));
        }
    }    @Test
    void testEnvironmentVariablesFallback() {
        // Use a more direct approach to test the behavior
        
        try (MockedStatic<Dotenv> dotenvMockedStatic = mockStatic(Dotenv.class)) {
            // Setup the dotenv mock chain
            DotenvBuilder dotenvBuilder = mock(DotenvBuilder.class);
            dotenvMockedStatic.when(Dotenv::configure).thenReturn(dotenvBuilder);
            when(dotenvBuilder.ignoreIfMissing()).thenReturn(dotenvBuilder);
            when(dotenvBuilder.load()).thenReturn(dotenv);
            
            // Make dotenv return null to force fallback to dummy key
            when(dotenv.get("OPEN_AI_TOKEN")).thenReturn(null);
            when(dotenv.get("OPEN_AI_MODEL")).thenReturn(null);
            
            // Create a real client but with mocked builder
            when(webClientBuilder.build()).thenReturn(webClient);
            OpenAiClient client = new OpenAiClient(webClientBuilder, objectMapper);
            
            // Since the OpenAiClient will use a dummy key in this case, test that a mock response is returned
            String result = client.queryModel("Test prompt");
            
            // Verify it returns the expected dummy response
            assertEquals("{\"choices\":[{\"message\":{\"content\":\"Esta es una respuesta simulada. Configura OPEN_AI_TOKEN para usar OpenAI.\"}}]}", result);
            
            // No real web client calls should be made since it's using the dummy key
            verifyNoInteractions(webClient);
        }
    }
}