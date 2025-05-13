package edu.eci.cvds.prometeo.huggingface;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;






public class HuggingFaceClientTest {

    @Mock
    private HuggingFaceProperties mockProperties;
    
    private HuggingFaceClient client;
    
    // Test implementation of HuggingFaceClient that allows testing with mocked HttpClient
    private static class TestableHuggingFaceClient extends HuggingFaceClient {
        private final HttpClient mockHttpClient;
        private final HttpResponse<String> mockResponse;
        
        public TestableHuggingFaceClient(HuggingFaceProperties props, 
                                        HttpClient mockHttpClient, 
                                        HttpResponse<String> mockResponse) {
            super(props);
            this.mockHttpClient = mockHttpClient;
            this.mockResponse = mockResponse;
        }
        
        @Override
        public String queryModel(String input) throws Exception {
            String jsonPayload = "{\"inputs\": \"" + input + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            // Use mockResponse instead of calling the real HTTP client
            HttpResponse<String> response = mockResponse;

            if (response.statusCode() != 200) {
                throw new RuntimeException("Error calling Hugging Face API: " + response.body());
            }

            return response.body();
        }
    }
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockProperties.getModelUrl()).thenReturn("http://test-url");
        when(mockProperties.getApiToken()).thenReturn("test-token");
        client = new HuggingFaceClient(mockProperties);
    }
    
    @Test
    void testConstructor() {
        assertNotNull(client);
    }
    
    // @Test
    // void testQueryModel_Success() throws Exception {
    //     // Arrange
    //     HttpClient mockHttpClient = mock(HttpClient.class);
    //     HttpResponse<String> mockResponse = mock(HttpResponse.class);
    //     when(mockResponse.statusCode()).thenReturn(200);
    //     when(mockResponse.body()).thenReturn("Success response");
        
    //     TestableHuggingFaceClient testClient = new TestableHuggingFaceClient(
    //         mockProperties, mockHttpClient, mockResponse);
        
    //     // Act
    //     String result = testClient.queryModel("Test input");
        
    //     // Assert
    //     assertNotEquals("Success response", result);
    //     verify(mockProperties).getModelUrl();
    //     verify(mockProperties).getApiToken();
    // }
    
    
}