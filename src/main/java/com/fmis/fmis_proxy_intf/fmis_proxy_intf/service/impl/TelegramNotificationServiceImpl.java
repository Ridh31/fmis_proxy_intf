package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.bot.ProxyInterfaceNotification;
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

    private final ProxyInterfaceNotification interfaceTelegramBot;

    /**
     * Constructs a {@code TelegramNotificationServiceImpl} with the required bot dependency.
     * Initializes the service for sending messages via the configured Telegram bot.
     *
     * @param interfaceTelegramBot the Telegram bot used to send messages
     */
    public TelegramNotificationServiceImpl(ProxyInterfaceNotification interfaceTelegramBot) {
        this.interfaceTelegramBot = interfaceTelegramBot;
    }

    /**
     * Sends a message to the configured Telegram chat(s) via the bot.
     *
     * @param telegramMessage the message text to send
     */
    @Override
    public void sendProxyInterfaceMessage(String telegramMessage) {
        for (String chatId : interfaceTelegramBot.getChatIds()) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(telegramMessage);
            message.setParseMode("HTML");

            try {
                interfaceTelegramBot.execute(message);
            } catch (TelegramApiException e) {
                System.err.println("Failed to send Telegram message to chat ID " + chatId + ": " + e.getMessage());
            }
        }
    }
}