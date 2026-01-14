package dev.bwmp.discordbridge.discord.commands.impl;

import dev.bwmp.discordbridge.DiscordBridge;
import dev.bwmp.discordbridge.discord.commands.Command;
import dev.bwmp.discordbridge.discord.commands.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;

public class HelpCommand implements Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"commands", "?"};
    }

    @Override
    public String getDescription() {
        return "Show all available commands";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        CommandManager manager = DiscordBridge.getInstance().getDiscordBot().getCommandManager();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Available Commands")
                .setColor(Color.BLUE);

        StringBuilder sb = new StringBuilder();
        for (Command cmd : manager.getCommands()) {
            sb.append("`").append(manager.getPrefix()).append(cmd.getName()).append("`");

            String[] aliases = cmd.getAliases();
            if (aliases.length > 0) {
                sb.append(" (");
                for (int i = 0; i < aliases.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(aliases[i]);
                }
                sb.append(")");
            }

            sb.append(" - ").append(cmd.getDescription()).append("\n");
        }

        embed.setDescription(sb.toString());
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
