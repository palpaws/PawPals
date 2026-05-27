package com.he186674.mvc.petshop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> body) {

        String message = body.get("message");

        String url = "https://openrouter.ai/api/v1/chat/completions";

        RestTemplate restTemplate = new RestTemplate();

        // HEADERS
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(apiKey);

        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("HTTP-Referer", "http://localhost:8080");

        headers.set("X-Title", "PawPals");

        // SYSTEM MESSAGE
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put(
                "content",
                "Bạn là chatbot của website PawPals. " +
                        "Website chuyên về thú cưng, spa, hotel, chăm sóc chó mèo. " +
                        "Trả lời ngắn gọn, thân thiện bằng tiếng Việt."
        );

        // USER MESSAGE
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        // REQUEST BODY
        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("model", "deepseek/deepseek-chat-v3-0324");

        requestBody.put("messages", messages);

        // ENTITY
        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(requestBody, headers);

        // API CALL
        ResponseEntity<Map> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        Map.class
                );

        // PARSE RESPONSE
        Map responseBody = response.getBody();

        List choices =
                (List) responseBody.get("choices");

        Map choice =
                (Map) choices.get(0);

        Map messageMap =
                (Map) choice.get("message");

        String reply =
                messageMap.get("content").toString();

        return Map.of("reply", reply);
    }
}