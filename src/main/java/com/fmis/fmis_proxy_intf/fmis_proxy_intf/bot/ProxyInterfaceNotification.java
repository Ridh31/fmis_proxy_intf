package com.fmis.fmis_proxy_intf.fmis_proxy_intf.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Telegram bot that handles subscriptions for proxy interface notifications.
 */
@Component
public class ProxyInterfaceNotification extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final Set<String> chatIds = ConcurrentHashMap.newKeySet();

    /**
     * Constructs a Telegram bot with the provided token and username.
     *
     * @param botToken    the bot token from BotFather
     * @param botUsername the bot username (without '@')
     */
    public ProxyInterfaceNotification(
            @Value("${proxy.interface.telegram.bot.token}") String botToken,
            @Value("${proxy.interface.telegram.bot.username}") String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    /**
     * Processes incoming Telegram updates.
     * Registers users who send the "/start" command and confirms subscription.
     *
     * @param update the incoming update from Telegram
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText().trim();

            if ("/start".equalsIgnoreCase(messageText)) {
                SendMessage startMessage = new SendMessage();
                startMessage.setChatId(chatId);
                startMessage.setText("✅ You’re now set to receive interface updates.");

                try {
                    execute(startMessage);
                    chatIds.add(chatId);
                } catch (TelegramApiException e) {
                    System.err.printf("⚠️ Unable to send start message to chat ID %s. Error: %s%n", chatId, e.getMessage());
                }
            }
        }
    }

    /**
     * Returns the bot token configured for this bot.
     *
     * @return the bot token
     */
    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Returns the bot username configured for this bot.
     *
     * @return the bot username
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    /**
     * Returns the set of chat IDs that have subscribed by sending "/start".
     *
     * @return a set of subscribed chat IDs
     */
    public Set<String> getChatIds() {
        return chatIds;
    }
}