package dev.bwmp.discordbridge.events;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.bwmp.discordbridge.Config;
import dev.bwmp.discordbridge.DiscordBridge;

public class PlayerDeathSystem extends RefChangeSystem<EntityStore, DeathComponent> {

  public PlayerDeathSystem() {
    super();
  }

  @Override
  public ComponentType<EntityStore, DeathComponent> componentType() {
    return DeathComponent.getComponentType();
  }

  @Override
  public Query<EntityStore> getQuery() {
    return Query.any();
  }

  @Override
  public void onComponentAdded(Ref<EntityStore> ref,
      DeathComponent component,
      Store<EntityStore> store,
      CommandBuffer<EntityStore> commandBuffer) {
    handlePlayerDeath(ref, component, store);
  }

  @Override
  public void onComponentRemoved(Ref<EntityStore> ref,
      DeathComponent component,
      Store<EntityStore> store,
      CommandBuffer<EntityStore> commandBuffer) {
    Player player = store.getComponent(ref, Player.getComponentType());
    if (player != null) {
      System.out.println("Player " + player.getDisplayName() + " respawned");
    }
  }

  @Override
  public void onComponentSet(@Nonnull Ref<EntityStore> ref,
      @Nullable DeathComponent oldComponent,
      @Nonnull DeathComponent newComponent,
      @Nonnull Store<EntityStore> store,
      @Nonnull CommandBuffer<EntityStore> commandBuffer) {
    if (oldComponent == null) {
      handlePlayerDeath(ref, newComponent, store);
    }
  }

  private void handlePlayerDeath(@Nonnull Ref<EntityStore> ref,
      @Nonnull DeathComponent deathComponent,
      @Nonnull Store<EntityStore> store) {
    Player player = store.getComponent(ref, Player.getComponentType());
    if (player == null) {
      return;
    }

    Config config = DiscordBridge.getInstance().getConfig();

    String deathReason = deathComponent.getDeathMessage().getAnsiMessage();

    String message = config.getMessages().getPlayerDeath()
        .replace("{player}", player.getDisplayName())
        .replace("{reason}", deathReason.replace("You were" , "").trim());
    DiscordBridge.getInstance().getDiscordBot().sendMessage(message);

    System.out.println("Player " + player.getDisplayName() + " died from " + deathReason);
  }
}
