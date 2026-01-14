package dev.bwmp.discordbridge.discord.commands.impl;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import dev.bwmp.discordbridge.DiscordBridge;
import dev.bwmp.discordbridge.discord.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;

public class ConsoleCommand implements Command {

    @Override
    public String getName() {
        return "console";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cmd", "execute", "run"};
    }

    @Override
    public String getDescription() {
        return "Execute a server console command (Admin only)";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        Member member = event.getMember();
        if (member == null) {
            sendError(event, "Could not verify your roles.");
            return;
        }

        // Check for admin role
        String adminRoleId = DiscordBridge.getInstance().getConfig().getDiscord().getAdminRoleId();
        boolean hasAdminRole = member.getRoles().stream()
                .map(Role::getId)
                .anyMatch(id -> id.equals(adminRoleId));

        if (!hasAdminRole) {
            sendError(event, "You don't have permission to use this command.");
            return;
        }

        if (args.length == 0) {
            sendError(event, "Usage: `!console <command>`");
            return;
        }

        // Build the command string
        String command = String.join(" ", args);

        try {
            // Execute the command on the server
            HytaleServer.get().getCommandManager().handleCommand(ConsoleSender.INSTANCE, command);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Command Executed")
                    .setDescription("```" + command + "```")
                    .setColor(Color.GREEN)
                    .setFooter("Executed by " + member.getEffectiveName());

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            sendError(event, "Failed to execute command: " + e.getMessage());
        }
    }

    private void sendError(MessageReceivedEvent event, String message) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Error")
                .setDescription(message)
                .setColor(Color.RED);

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
