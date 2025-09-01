package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

/**
 * Service interface for sending notifications via Telegram.
 *
 * Provides a method to send messages to the proxy interface Telegram bot.
 */
public interface TelegramNotificationService {

    /**
     * Sends a message to the proxy interface Telegram bot.
     *
     * @param message the text message to send
     */
    void sendProxyInterfaceMessage(String message);
}
