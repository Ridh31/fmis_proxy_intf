package com.fmis.fmis_proxy_intf.fmis_proxy_intf.bot;

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

    private final String botToken;
    private final String botUsername;
    private final Set<String> chatIds = ConcurrentHashMap.newKeySet();

    /**
     * Constructs a Telegram bot with the provided token and username.
     *
     * @param botToken    the bot token issued by BotFather
     * @param botUsername the bot username (without the '@' symbol)
     */
    public ProxyInterfaceNotificationBot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
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

            if ("/start".equalsIgnoreCase(messageText)) {
                SendMessage startMessage = SendMessage.builder()
                        .chatId(chatId)
                        .text("✅ You’re now set to receive interface updates.")
                        .build();
                try {
                    execute(startMessage);
                    chatIds.add(chatId);
                } catch (TelegramApiException e) {
                    System.err.printf("⚠️ Unable to send start message to chat ID %s. Error: %s%n",
                            chatId, e.getMessage());
                }
            }
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