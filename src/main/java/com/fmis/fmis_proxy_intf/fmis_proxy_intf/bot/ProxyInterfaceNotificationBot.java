package com.fmis.fmis_proxy_intf.fmis_proxy_intf.bot;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.TelegramUtil;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Telegram bot that handles subscriptions for proxy interface notifications.
 * Listens for the "/start" command to register chat IDs and can send messages to subscribed chats.
 */
public class ProxyInterfaceNotificationBot extends TelegramLongPollingBot {

    @Value("${application.public.base-url}")
    private String baseURL;

    @Value("${application.api.prefix}")
    private String apiPrefix;

    private final String botToken;
    private final String botUsername;
    private final Set<String> chatIds = ConcurrentHashMap.newKeySet();
    private final boolean healthCommand;

    /**
     * Constructs a Telegram bot with the provided token and username.
     *
     * @param botToken    the bot token issued by BotFather
     * @param botUsername the bot username (without the '@' symbol)
     */

    public ProxyInterfaceNotificationBot(String botToken, String botUsername,
                                         boolean healthCommand, String baseURL, String apiPrefix) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.healthCommand = healthCommand;
        this.baseURL = baseURL;
        this.apiPrefix = apiPrefix;
    }

    /**
     * Sends a formatted text message to a specific Telegram chat.
     * This method uses the Telegram Bot API to send a message with optional HTML formatting.
     * It handles API exceptions internally and logs errors if the message cannot be sent.
     *
     * @param chatId  the unique identifier of the Telegram chat to send the message to
     * @param message the content of the message to be sent, supports HTML tags for rich formatting
     */
    public void sendMessage(String chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .parseMode("HTML")
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.err.printf("⚠️ Failed to send message to chat ID %s via %s. Error: %s%n",
                    chatId, getBotUsername(), e.getMessage());
        }
    }

    /**
     * Handles incoming updates from Telegram.
     * Registers chat IDs when receiving the "/start" command.
     *
     * @param update the incoming update from Telegram
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText().trim();

            switch (messageText.toLowerCase()) {
                case "/start":
                    if (chatIds.contains(chatId)) {
                        sendMessage(chatId, "ℹ️ You’re already set to receive interface updates.");
                    } else {
                        chatIds.add(chatId);
                        sendMessage(chatId, "✅ You’re now set to receive interface updates.");
                        System.out.printf("[%s] Subscribed: %s%n", getBotUsername(), chatId);
                    }
                    break;

                case "/stop":
                    if (chatIds.remove(chatId)) {
                        sendMessage(chatId, "🔕 You’ve stopped receiving interface updates.");
                        System.out.printf("[%s] Unsubscribed: %s%n", getBotUsername(), chatId);
                    } else {
                        sendMessage(chatId, "ℹ️ You’re not currently subscribed.");
                    }
                    break;

                case "/health":
                    if (healthCommand) {
                        String health = callHealthApi();
                        sendMessage(chatId, TelegramUtil.buildHealthCheckMessage(health));
                    } else {
                        sendMessage(chatId, "❌ This bot cannot run /health");
                    }
                    break;

                default:
                    sendMessage(chatId, "🤖 Unknown command. Use /start or /stop.");
                    break;
            }
        }
    }

    private String callHealthApi() {
        try {
            var client = java.net.http.HttpClient.newHttpClient();
            String healthUrl = baseURL.replaceAll("/$", "") + apiPrefix + "/system/health";

            var request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(healthUrl))
                    .GET()
                    .build();

            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (Exception e) {
            return "❌ Error checking system health: " + e.getMessage();
        }
    }

    /**
     * Returns the token of this Telegram bot.
     *
     * @return the bot token
     */
    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Returns the username of this Telegram bot.
     *
     * @return the bot username
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    /**
     * Returns the set of chat IDs subscribed to receive updates.
     *
     * @return a thread-safe set of subscribed chat IDs
     */
    public Set<String> getChatIds() {
        return chatIds;
    }
}