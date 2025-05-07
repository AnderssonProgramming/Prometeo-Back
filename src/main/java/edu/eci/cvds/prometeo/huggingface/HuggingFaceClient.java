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
    private final HuggingFaceProperties props;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public HuggingFaceClient(HuggingFaceProperties props) {
        this.props = props;
    }

    public String queryModel(String input) throws Exception {
        String jsonPayload = "{\"inputs\": \"" + input + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(props.getModelUrl()))
                .header("Authorization", "Bearer " + props.getApiToken())
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