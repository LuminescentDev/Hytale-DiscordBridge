package dev.bwmp.discordbridge.events;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.bwmp.discordbridge.Config;
import dev.bwmp.discordbridge.DiscordBridge;

public class ChatListener {

  public static void onPlayerChat(PlayerChatEvent event) {
    PlayerRef player = event.getSender();
    String message = event.getContent();

    Config config = DiscordBridge.getInstance().getConfig();

    // Format message for Discord
    String discordMessage = config.getMessages().getGameToDiscord()
        .replace("{player}", player.getUsername())
        .replace("{message}", message);

    // Send to Discord
    DiscordBridge.getInstance().getDiscordBot().sendMessage(discordMessage);
  }
}
