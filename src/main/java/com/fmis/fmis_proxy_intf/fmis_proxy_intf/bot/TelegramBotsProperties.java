package com.fmis.fmis_proxy_intf.fmis_proxy_intf.bot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration properties class to bind Telegram bot settings
 * defined under the prefix "proxy.interface.telegram" in application properties.
 */
@Component
@ConfigurationProperties(prefix = "proxy.interface.telegram")
public class TelegramBotsProperties {

    /**
     * List of Telegram bot configurations.
     */
    private List<Bot> bot;

    /**
     * Returns the list of Telegram bot configurations.
     *
     * @return the list of bots
     */
    public List<Bot> getBot() {
        return bot;
    }

    /**
     * Sets the list of Telegram bot configurations.
     *
     * @param bot the list of bots to set
     */
    public void setBot(List<Bot> bot) {
        this.bot = bot;
    }

    /**
     * Represents the configuration properties for a single Telegram bot.
     */
    public static class Bot {

        /**
         * The Telegram bot token issued by BotFather.
         */
        private String token;

        /**
         * The username of the Telegram bot (without the '@' symbol).
         */
        private String username;

        /**
         * Returns the token issued by BotFather for this bot.
         *
         * @return the bot token
         */
        public String getToken() {
            return token;
        }

        /**
         * Sets the token issued by BotFather for this bot.
         *
         * @param token the bot token to set
         */
        public void setToken(String token) {
            this.token = token;
        }

        /**
         * Returns the username of the bot (without the '@' symbol).
         *
         * @return the bot username
         */
        public String getUsername() {
            return username;
        }

        /**
         * Sets the username of the bot (without the '@' symbol).
         *
         * @param username the bot username to set
         */
        public void setUsername(String username) {
            this.username = username;
        }
    }
}