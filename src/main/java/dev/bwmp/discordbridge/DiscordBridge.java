package dev.bwmp.discordbridge;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;

import dev.bwmp.discordbridge.discord.DiscordBot;
import dev.bwmp.discordbridge.discord.commands.impl.ConsoleCommand;
import dev.bwmp.discordbridge.discord.commands.impl.HelpCommand;
import dev.bwmp.discordbridge.discord.commands.impl.ListCommand;
import dev.bwmp.discordbridge.events.ChatListener;
import dev.bwmp.discordbridge.events.ConnectionListener;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public class DiscordBridge extends JavaPlugin {

    private static DiscordBridge instance;
    private DiscordBot discordBot;
    private Config config;
    private Path dataFolder;

    public DiscordBridge(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        this.dataFolder = Path.of("mods", "DiscordBridge");
    }

    @Override
    protected void setup() {
        // Load configuration
        try {
            loadConfig();
        } catch (IOException e) {
            getLogger().at(Level.SEVERE).withCause(e).log("Failed to load config.json");
            return;
        }

        // Initialize Discord bot
        discordBot = new DiscordBot(config);
        if (!discordBot.connect()) {
            getLogger().at(Level.SEVERE).log("Failed to connect to Discord. Check your bot token.");
            return;
        }

        discordBot.sendMessage(config.getMessages().getServerStart());

        // Register Discord commands
        registerCommands();

        // Register event listeners
        this.getEventRegistry().registerGlobal(PlayerChatEvent.class, ChatListener::onPlayerChat);
        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, ConnectionListener::onPlayerJoin);
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, ConnectionListener::onPlayerLeave);

        getLogger().at(Level.INFO).log("DiscordBridge enabled successfully!");
    }

    private void registerCommands() {
        var manager = discordBot.getCommandManager();
        manager.register(new HelpCommand());
        manager.register(new ListCommand());
        manager.register(new ConsoleCommand());
    }

    @Override
    protected void shutdown() {
        if (discordBot != null){
            discordBot.sendMessage(config.getMessages().getServerStop());
            discordBot.disconnect();
        }
        getLogger().at(Level.INFO).log("DiscordBridge disabled.");
    }

    private void loadConfig() throws IOException {
        Path configPath = dataFolder.resolve("config.json");

        // Copy default config if it doesn't exist
        if (!Files.exists(configPath)) {
            Files.createDirectories(configPath.getParent());
            try (InputStream in = getClass().getResourceAsStream("/config.json")) {
                if (in != null) {
                    Files.copy(in, configPath);
                }
            }
        }

        // Parse config
        String content = Files.readString(configPath);
        config = Config.parse(content);
    }

    public static DiscordBridge getInstance() {
        return instance;
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    public Config getConfig() {
        return config;
    }

    public Path getDataFolder() {
        return dataFolder;
    }

    public void broadcastMessage(String message) {
        for (PlayerRef playerRef : Universe.get().getPlayers()) {
            playerRef.sendMessage(Message.raw(message));
        }
    }
}
