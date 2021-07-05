package me.hope.lpt.module.listeners;

import com.google.common.collect.Lists;
import java.util.List;
import me.hope.lpt.module.DreamFishLimiter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FishLimiter implements Listener {
  private DreamFishLimiter module;
  
  private List<String> aliases = Lists.newArrayList((Object[])new String[] { "dreamfish", "fish", "fishing" });
  
  public FishLimiter(DreamFishLimiter module) {
    this.module = module;
  }
  
  @EventHandler
  public void onCommand(PlayerCommandPreprocessEvent event) {
    if (isFish(event.getMessage())) {
      boolean onCooldown = !onCommand(event.getPlayer(), this.module.getMode());
      event.setCancelled(onCooldown);
    } 
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    this.module.loadPlayerCache(event.getPlayer().getName());
  }
  
  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent event) {
    this.module.savePlayerCache(event.getPlayer().getName());
  }
  
  private boolean isFish(String message) {
    String[] temps = message.substring(1).split(" ");
    return this.aliases.contains(temps[0]);
  }
  
  public boolean onCommand(Player player, DreamFishLimiter.LimiterMode mode) {
    DreamFishLimiter.PlayerInfo playerInfo;
    boolean result = false;
    switch (mode) {
      case PLAYER:
        playerInfo = this.module.getPlayerInfo(player.getPlayer().getName());
        if (playerInfo != null) {
          if (playerInfo.commpareCooldown() > this.module.getCooldown()) {
            playerInfo.setLastCommand(System.currentTimeMillis());
            this.module.setPlayerCache(playerInfo);
            result = true;
            break;
          } 
          player.sendMessage("[FishLimiter] :当前命令处于冷却，剩余" + Math.floor(((this.module.getCooldown() - playerInfo.commpareCooldown()) / 1000L)) + "秒");
        } 
        break;
      case GLOBAL:
        if (this.module.commpareCooldown() > this.module.getCooldown()) {
          this.module.setLastCommand();
          result = true;
          break;
        } 
        player.sendMessage("[FishLimiter] :当前命令处于冷却，剩余" + Math.floor(((this.module.getCooldown() - this.module.commpareCooldown()) / 1000L)) + "秒");
        break;
    } 
    return result;
  }
}
