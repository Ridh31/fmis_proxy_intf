package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.AIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for AI interactions.
 * Provides endpoints to chat with the AI using user questions.
 */
@RestController
@RequestMapping("/fmis-proxy-ai")
public class AIController {

    private final AIService aiService;

    /**
     * Constructor for AIController.
     *
     * @param aiService The service that handles AI chat operations.
     */
    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * Chat with AI using a user-provided question.
     *
     * @param question The question to ask the AI.
     * @return AI-generated answer as a String.
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String question) {
        return aiService.chat(question);
    }
}