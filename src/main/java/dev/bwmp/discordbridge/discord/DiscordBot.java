package dev.bwmp.discordbridge.discord;

import dev.bwmp.discordbridge.Config;
import dev.bwmp.discordbridge.DiscordBridge;
import dev.bwmp.discordbridge.discord.commands.CommandManager;
import net.dv8tion.jda.api.JDA;

import java.util.logging.Level;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot extends ListenerAdapter {

    private final Config config;
    private final CommandManager commandManager;
    private JDA jda;

    public DiscordBot(Config config) {
        this.config = config;
        this.commandManager = new CommandManager("!");
    }

    public boolean connect() {
        try {
            jda = JDABuilder.createDefault(config.getDiscord().getBotToken())
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(this)
                    .build()
                    .awaitReady();
            return true;
        } catch (Exception e) {
            DiscordBridge.getInstance().getLogger().at(Level.SEVERE).withCause(e).log("Failed to connect to Discord");
            return false;
        }
    }

    public void disconnect() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public void sendMessage(String message) {
        if (jda == null) return;

        TextChannel channel = jda.getTextChannelById(config.getDiscord().getChannelId());
        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public JDA getJda() {
        return jda;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore bot messages
        if (event.getAuthor().isBot()) return;

        // Only listen to the configured channel
        if (!event.getChannel().getId().equals(config.getDiscord().getChannelId())) return;

        // Try to handle as command
        if (commandManager.handleMessage(event)) {
            return;
        }

        // Otherwise, relay to game
        String username = event.getMember() != null
                ? event.getMember().getEffectiveName()
                : event.getAuthor().getName();
        String content = event.getMessage().getContentDisplay();

        String gameMessage = config.getMessages().getDiscordToGame()
                .replace("{username}", username)
                .replace("{message}", content);

        DiscordBridge.getInstance().broadcastMessage(gameMessage);
    }
}
