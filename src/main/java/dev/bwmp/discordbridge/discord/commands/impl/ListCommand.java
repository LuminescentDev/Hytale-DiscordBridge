package dev.bwmp.discordbridge.discord.commands.impl;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import dev.bwmp.discordbridge.discord.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.Collection;

public class ListCommand implements Command {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"players", "online"};
    }

    @Override
    public String getDescription() {
        return "List all online players";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        Collection<PlayerRef> players = Universe.get().getPlayers();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Online Players")
                .setColor(Color.GREEN);

        if (players.isEmpty()) {
            embed.setDescription("No players online");
        } else {
            StringBuilder sb = new StringBuilder();
            for (PlayerRef player : players) {
                sb.append("• ").append(player.getUsername()).append("\n");
            }
            embed.setDescription(sb.toString());
            embed.setFooter(players.size() + " player(s) online");
        }

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
