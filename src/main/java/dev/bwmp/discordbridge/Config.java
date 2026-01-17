package dev.bwmp.discordbridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @SerializedName("discord")
    private DiscordConfig discord;

    @SerializedName("messages")
    private MessagesConfig messages;

    public static Config parse(String json) {
        Config config = GSON.fromJson(json, Config.class);
        if (config == null) {
            config = new Config();
        }
        return config.applyDefaults();
    }

    public static Config createDefault() {
        return new Config().applyDefaults();
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    /**
     * Apply default values for any missing config fields.
     * Returns true if any defaults were applied.
     */
    public Config applyDefaults() {
        if (discord == null) {
            discord = new DiscordConfig();
        }
        discord.applyDefaults();

        if (messages == null) {
            messages = new MessagesConfig();
        }
        messages.applyDefaults();

        return this;
    }

    public DiscordConfig getDiscord() {
        return discord;
    }

    public MessagesConfig getMessages() {
        return messages;
    }

    public static class DiscordConfig {
        @SerializedName("botToken")
        private String botToken;

        @SerializedName("channelId")
        private String channelId;

        @SerializedName("adminRoleId")
        private String adminRoleId;

        void applyDefaults() {
            if (botToken == null) botToken = "YOUR_BOT_TOKEN_HERE";
            if (channelId == null) channelId = "YOUR_CHANNEL_ID_HERE";
            if (adminRoleId == null) adminRoleId = "YOUR_ADMIN_ROLE_ID_HERE";
        }

        public String getBotToken() {
            return botToken;
        }

        public String getChannelId() {
            return channelId;
        }

        public String getAdminRoleId() {
            return adminRoleId;
        }
    }

    public static class MessagesConfig {
        @SerializedName("discordToGame")
        private String discordToGame;

        @SerializedName("gameToDiscord")
        private String gameToDiscord;

        @SerializedName("playerJoin")
        private String playerJoin;

        @SerializedName("playerLeave")
        private String playerLeave;

        @SerializedName("serverStart")
        private String serverStart;

        @SerializedName("serverStop")
        private String serverStop;

        @SerializedName("playerDeath")
        private String playerDeath;

        void applyDefaults() {
            if (discordToGame == null) discordToGame = "[Discord] {username}: {message}";
            if (gameToDiscord == null) gameToDiscord = "**{player}**: {message}";
            if (playerJoin == null) playerJoin = ":green_circle: **{player}** joined the server";
            if (playerLeave == null) playerLeave = ":red_circle: **{player}** left the server";
            if (serverStart == null) serverStart = ":white_check_mark: Server has started";
            if (serverStop == null) serverStop = ":stop_sign: Server is stopping";
            if (playerDeath == null) playerDeath = ":skull: **{player}** has died: {reason}";
        }

        public String getDiscordToGame() {
            return discordToGame;
        }

        public String getGameToDiscord() {
            return gameToDiscord;
        }

        public String getPlayerJoin() {
            return playerJoin;
        }

        public String getPlayerLeave() {
            return playerLeave;
        }

        public String getServerStart() {
            return serverStart;
        }

        public String getServerStop() {
            return serverStop;
        }

        public String getPlayerDeath() {
            return playerDeath;
        }
    }
}
