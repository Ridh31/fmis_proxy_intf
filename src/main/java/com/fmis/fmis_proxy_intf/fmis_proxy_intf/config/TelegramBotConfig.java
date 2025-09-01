package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.bot.ProxyInterfaceNotification;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Configuration class for setting up the Telegram bot integration.
 */
@Configuration
public class TelegramBotConfig {

    @Value("${proxy.interface.telegram.bot.token}")
    private String botToken;

    /**
     * Unregisters any existing Telegram webhook to avoid 409 conflict errors
     * when using {@code TelegramLongPollingBot}.
     * This ensures that polling works without webhook interference.
     */
    @PostConstruct
    public void unregisterWebhook() {
        String url = "https://api.telegram.org/bot" + botToken + "/deleteWebhook";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Webhook delete response: " + response.body());
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to delete Telegram webhook: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }

    /**
     * Registers the Telegram bot with {@link TelegramBotsApi} for long polling.
     *
     * @param interfaceBot the custom Telegram bot implementation
     * @return a configured instance of {@code TelegramBotsApi}
     * @throws TelegramApiException if bot registration fails
     */
    @Bean
    public TelegramBotsApi telegramBotsApi(ProxyInterfaceNotification interfaceBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(interfaceBot);
        return botsApi;
    }
}
