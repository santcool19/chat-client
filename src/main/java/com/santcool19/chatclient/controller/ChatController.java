package com.santcool19.chatclient.controller;

import com.santcool19.chatclient.model.UserPrompt;
import com.santcool19.chatclient.repo.UserPromptRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class ChatController {

    private final UserPromptRepository promptRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public ChatController(UserPromptRepository promptRepository) {
        this.promptRepository = promptRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<?> send(@RequestParam String username, @RequestParam String prompt) {
        // store locally
        promptRepository.save(new UserPrompt(username, prompt));

        // post to MCP for investment-analyzer
        try {
            var msg = new java.util.HashMap<String, Object>();
            msg.put("sender", username);
            msg.put("recipient", "investment-analyzer");
            msg.put("payload", prompt);
            restTemplate.postForEntity("http://localhost:8082/api/mcp/send", msg, String.class);
            return ResponseEntity.ok("sent");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/poll")
    @ResponseBody
    public ResponseEntity<?> poll() {
        try {
            var resp = restTemplate.getForEntity("http://localhost:8082/api/mcp/poll?recipient=chat-client", Object[].class);
            return ResponseEntity.ok(resp.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/history")
    @ResponseBody
    public List<UserPrompt> history() {
        return promptRepository.findAll();
    }
}

