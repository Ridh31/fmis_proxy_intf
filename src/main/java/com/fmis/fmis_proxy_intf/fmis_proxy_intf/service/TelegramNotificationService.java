package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.bot.ProxyInterfaceNotificationBot;

/**
 * Service interface for sending notifications via Telegram bots.
 */
public interface TelegramNotificationService {

    /**
     * Sends a message via the Proxy Interface Telegram bot.
     *
     * @param message the message text to send
     */
    void sendProxyInterfaceMessage(String message);

    /**
     * Sends a message via the Bank Interface Telegram bot.
     *
     * @param message the message text to send
     */
    void sendBankInterfaceMessage(String message);

    /**
     * Sends a message via the SARMIS Interface Telegram bot.
     *
     * @param message the message text to send
     */
    void sendSarmisInterfaceMessage(String message);

    /**
     * Sends a message to all registered Telegram bots.
     *
     * @param message the message text to send
     */
    void sendToAllBots(String message);

    /**
     * Sends a message to a specific Telegram bot identified by username.
     *
     * @param botUsername the username of the target bot
     * @param message the message text to send
     */
    void sendToBot(String botUsername, String message);

    /**
     * Registers a Telegram bot instance by its username.
     *
     * @param botUsername the username of the bot
     * @param bot the bot instance
     */
    void registerBot(String botUsername, ProxyInterfaceNotificationBot bot);
}