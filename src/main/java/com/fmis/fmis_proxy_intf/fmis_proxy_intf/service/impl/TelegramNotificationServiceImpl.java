package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.bot.BankInterfaceNotification;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TelegramNotificationService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Implementation of {@link TelegramNotificationService} that sends messages
 * to a Telegram bot using the Telegram Bot API.
 */
@Service
public class TelegramNotificationServiceImpl implements TelegramNotificationService {

    private final BankInterfaceNotification bankTelegramBot;

    /**
     * Constructs a {@code TelegramNotificationServiceImpl} with the required bot dependency.
     * Initializes the service for sending messages via the configured Telegram bot.
     *
     * @param bankTelegramBot the Telegram bot used to send messages
     */
    public TelegramNotificationServiceImpl(BankInterfaceNotification bankTelegramBot) {
        this.bankTelegramBot = bankTelegramBot;
    }

    /**
     * Sends a message to the configured Telegram chat(s) via the bot.
     *
     * @param telegramMessage the message text to send
     */
    @Override
    public void sendBankInterfaceMessage(String telegramMessage) {
        for (String chatId : bankTelegramBot.getChatIds()) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(telegramMessage);
            message.setParseMode("Markdown");

            try {
                bankTelegramBot.execute(message);
            } catch (TelegramApiException e) {
                System.err.println("Failed to send Telegram message to chat ID " + chatId + ": " + e.getMessage());
            }
        }
    }
}