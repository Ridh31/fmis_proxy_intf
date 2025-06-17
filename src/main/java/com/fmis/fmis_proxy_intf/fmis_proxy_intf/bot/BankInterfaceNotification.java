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
 * Telegram bot that handles subscriptions for bank statement notifications.
 */
@Component
public class BankInterfaceNotification extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final Set<String> chatIds = ConcurrentHashMap.newKeySet();

    /**
     * Constructs a Telegram bot with the provided token and username.
     *
     * @param botToken    the bot token from BotFather
     * @param botUsername the bot username (without '@')
     */
    public BankInterfaceNotification(
            @Value("${bank.interface.telegram.bot.token}") String botToken,
            @Value("${bank.interface.telegram.bot.username}") String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    /**
     * Handles incoming Telegram updates.
     * Registers a user if they send the "/start" command.
     *
     * @param update the incoming update from Telegram
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText().trim();

            if ("/start".equalsIgnoreCase(text)) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("✅ You’re now set to receive bank interface updates.");

                try {
                    execute(message);
                    chatIds.add(chatId);
                } catch (TelegramApiException e) {
                    System.err.println("Failed to send welcome message to chat ID " + chatId + ": " + e.getMessage());
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