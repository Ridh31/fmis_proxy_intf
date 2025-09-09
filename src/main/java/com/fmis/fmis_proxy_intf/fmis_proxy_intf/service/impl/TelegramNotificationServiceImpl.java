package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.bot.ProxyInterfaceNotificationBot;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TelegramNotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link TelegramNotificationService} that sends messages
 * to Telegram bots using the Telegram Bot API.
 */
@Service
public class TelegramNotificationServiceImpl implements TelegramNotificationService {

    private final Map<String, ProxyInterfaceNotificationBot> botRegistry = new ConcurrentHashMap<>();

    private final String proxyInterfaceBotUsername;
    private final String bankInterfaceBotUsername;
    private final String sarmisInterfaceBotUsername;

    /**
     * Constructs the Telegram notification service implementation with bot usernames
     * injected from application properties. These usernames are used to route messages
     * to the correct bot instance.
     *
     * @param proxyInterfaceBotUsername  the username of the Proxy Interface bot
     * @param bankInterfaceBotUsername   the username of the Bank Interface bot
     * @param sarmisInterfaceBotUsername the username of the SARMIS Interface bot
     */
    public TelegramNotificationServiceImpl(
            @Value("${proxy.interface.telegram.bot[0].username}") String proxyInterfaceBotUsername,
            @Value("${proxy.interface.telegram.bot[1].username}") String bankInterfaceBotUsername,
            @Value("${proxy.interface.telegram.bot[2].username}") String sarmisInterfaceBotUsername) {
        this.proxyInterfaceBotUsername = proxyInterfaceBotUsername;
        this.bankInterfaceBotUsername = bankInterfaceBotUsername;
        this.sarmisInterfaceBotUsername = sarmisInterfaceBotUsername;
    }

    /**
     * Registers a Telegram bot instance with the service.
     *
     * @param botUsername the username of the bot
     * @param bot the bot instance
     */
    @Override
    public void registerBot(String botUsername, ProxyInterfaceNotificationBot bot) {
        botRegistry.put(botUsername, bot);
    }

    /**
     * Sends a message through the Proxy Interface bot.
     *
     * @param message the message text to send
     */
    @Override
    public void sendProxyInterfaceMessage(String message) {
        sendToBot(proxyInterfaceBotUsername, message);
    }

    /**
     * Sends a message through the Bank Interface bot.
     *
     * @param message the message text to send
     */
    @Override
    public void sendBankInterfaceMessage(String message) {
        sendToBot(bankInterfaceBotUsername, message);
    }

    /**
     * Sends a message through the SARMIS Interface bot.
     *
     * @param message the message text to send
     */
    @Override
    public void sendSarmisInterfaceMessage(String message) {
        sendToBot(sarmisInterfaceBotUsername, message);
    }

    /**
     * Sends a message to all registered bots and their subscribers.
     *
     * @param message the message text to send
     */
    @Override
    public void sendToAllBots(String message) {
        for (ProxyInterfaceNotificationBot bot : botRegistry.values()) {
            sendToBotSubscribers(bot, message);
        }
    }

    /**
     * Sends a message to a specific bot identified by username.
     *
     * @param botUsername the username of the target bot
     * @param message the message text to send
     */
    @Override
    public void sendToBot(String botUsername, String message) {
        ProxyInterfaceNotificationBot bot = botRegistry.get(botUsername);
        if (bot != null) {
            sendToBotSubscribers(bot, message);
        } else {
            System.err.printf("⚠️ Bot with username %s not found.%n", botUsername);
        }
    }

    /**
     * Sends the given message to all chat IDs subscribed to the specified bot.
     *
     * @param bot the Telegram bot instance
     * @param message the message text to send
     */
    private void sendToBotSubscribers(ProxyInterfaceNotificationBot bot, String message) {
        Set<String> chatIds = bot.getChatIds();
        for (String chatId : chatIds) {
            bot.sendMessage(chatId, message);
        }
    }
}