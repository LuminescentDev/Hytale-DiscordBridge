package dev.bwmp.discordbridge.discord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Command {

    /**
     * @return The command name (without prefix)
     */
    String getName();

    /**
     * @return Command aliases (optional)
     */
    default String[] getAliases() {
        return new String[0];
    }

    /**
     * @return Command description for help
     */
    String getDescription();

    /**
     * Execute the command
     * @param event The Discord message event
     * @param args Command arguments (split by space)
     */
    void execute(MessageReceivedEvent event, String[] args);
}
