package edu.eci.cvds.prometeo.huggingface;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class HuggingFaceClient {

    @Value("${huggingface.api.token}")
    private String huggingFaceToken;

    @Value("${huggingface.model.url}")
    private String modelUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String queryModel(String input) throws Exception {
        String jsonPayload = "{\"inputs\": \"" + input + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(modelUrl))
                .header("Authorization", "Bearer " + huggingFaceToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error calling Hugging Face API: " + response.body());
        }

        return response.body();
    }
}