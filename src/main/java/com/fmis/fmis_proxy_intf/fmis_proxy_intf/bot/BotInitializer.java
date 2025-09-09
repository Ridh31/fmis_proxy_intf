package com.fmis.fmis_proxy_intf.fmis_proxy_intf.bot;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TelegramNotificationService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Initializes and registers Telegram bots defined in configuration.
 */
@Component
public class BotInitializer {

    private final TelegramBotsProperties telegramBotsProperties;
    private final TelegramNotificationService notificationService;

    /**
     * Creates a BotInitializer with the necessary dependencies.
     *
     * @param telegramBotsProperties configuration properties containing bot details
     * @param notificationService service to manage and send notifications via bots
     */
    public BotInitializer(TelegramBotsProperties telegramBotsProperties,
                          TelegramNotificationService notificationService) {
        this.telegramBotsProperties = telegramBotsProperties;
        this.notificationService = notificationService;
    }

    /**
     * Called after bean construction to register all configured bots.
     */
    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            for (TelegramBotsProperties.Bot botConfig : telegramBotsProperties.getBot()) {
                unregisterWebhook(botConfig.getToken());

                ProxyInterfaceNotificationBot bot = new ProxyInterfaceNotificationBot(
                        botConfig.getToken(), botConfig.getUsername());

                botsApi.registerBot(bot);
                notificationService.registerBot(botConfig.getUsername(), bot);
                System.out.printf("✅ Telegram bot activated: %s%n", botConfig.getUsername());
            }

        } catch (TelegramApiException e) {
            System.err.printf("❌ Telegram bot initialization failed: %s%n", e.getMessage());
        }
    }

    /**
     * Removes webhook for a bot to avoid conflicts with long polling.
     *
     * @param botToken Telegram bot token
     */
    private void unregisterWebhook(String botToken) {
        String url = "https://api.telegram.org/bot" + botToken + "/deleteWebhook";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        String tokenPreview = botToken.length() > 8 ? botToken.substring(0, 8) : botToken;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.printf("ℹ️ Webhook delete response for bot [%s...]: %s%n", tokenPreview, response.body());
        } catch (IOException | InterruptedException e) {
            System.err.printf("⚠️ Failed to delete Telegram webhook for bot [%s...]: %s%n", tokenPreview, e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}