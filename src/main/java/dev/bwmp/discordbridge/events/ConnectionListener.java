package dev.bwmp.discordbridge.events;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.bwmp.discordbridge.Config;
import dev.bwmp.discordbridge.DiscordBridge;

public class ConnectionListener {

  public static void onPlayerJoin(PlayerConnectEvent event) {
    PlayerRef player = event.getPlayerRef();
    Config config = DiscordBridge.getInstance().getConfig();
    String message = config.getMessages().getPlayerJoin()
        .replace("{player}", player.getUsername());
    DiscordBridge.getInstance().getDiscordBot().sendMessage(message);
  }

  public static void onPlayerLeave(PlayerDisconnectEvent event) {
    PlayerRef player = event.getPlayerRef();
    Config config = DiscordBridge.getInstance().getConfig();

    String message = config.getMessages().getPlayerLeave()
        .replace("{player}", player.getUsername());

    DiscordBridge.getInstance().getDiscordBot().sendMessage(message);
  }
}
