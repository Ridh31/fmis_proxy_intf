package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TelegramNotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link TelegramNotificationService} that sends messages
 * to a Telegram bot using the Telegram Bot API.
 */
@Service
public class TelegramNotificationServiceImpl implements TelegramNotificationService {

    @Value("${bank.interface.telegram.bot.token}")
    private String botToken;

    @Value("${bank.interface.telegram.chat.id}")
    private String chatId;

    private final RestTemplate restTemplate;

    public TelegramNotificationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends a message to the configured Telegram chat via the bot.
     *
     * @param message the message text to send
     */
    @Override
    public void sendBankInterfaceMessage(String message) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        Map<String, String> params = new HashMap<>();
        params.put("chat_id", chatId);
        params.put("text", message);

        try {
            restTemplate.postForObject(url, params, String.class);
        } catch (Exception e) {
            System.err.println("Failed to send telegram message: " + e.getMessage());
        }
    }
}