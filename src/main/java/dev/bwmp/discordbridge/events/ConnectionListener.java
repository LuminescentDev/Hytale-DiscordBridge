package dev.bwmp.discordbridge.events;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.bwmp.discordbridge.Config;
import dev.bwmp.discordbridge.DiscordBridge;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionListener {
  private static final Map<String, Long> lastLeaveSent = new ConcurrentHashMap<>();
  private static final long LEAVE_DEDUP_WINDOW_MS = 1500L; // prevent duplicate leave messages within 1.5s

  public static void onPlayerJoin(PlayerConnectEvent event) {
    PlayerRef player = event.getPlayerRef();
    Config config = DiscordBridge.getInstance().getConfig();
    String message = config.getMessages().getPlayerJoin()
        .replace("{player}", player.getUsername());
    DiscordBridge.getInstance().getDiscordBot().sendMessage(message);

    lastLeaveSent.remove(player.getUsername());
  }

  public static void onPlayerLeave(PlayerDisconnectEvent event) {
    PlayerRef player = event.getPlayerRef();
    Config config = DiscordBridge.getInstance().getConfig();

    long now = System.currentTimeMillis();
    Long last = lastLeaveSent.get(player.getUsername());
    if (last != null && (now - last) < LEAVE_DEDUP_WINDOW_MS) {
      return;
    }

    String message = config.getMessages().getPlayerLeave()
        .replace("{player}", player.getUsername());

    DiscordBridge.getInstance().getDiscordBot().sendMessage(message);

    lastLeaveSent.put(player.getUsername(), now);
  }
}
