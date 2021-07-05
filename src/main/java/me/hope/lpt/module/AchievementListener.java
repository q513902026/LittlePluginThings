package me.hope.lpt.module;

import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.BaseModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AchievementListener extends BaseModule implements Listener {
  public AchievementListener() {
    super(LittlePluginThings.GetDefaultModuleName(AchievementListener.class.toString()), "achievement");
    saveDefaultConfig();
  }
  
  @EventHandler
  public void onAchievement(PlayerAdvancementDoneEvent event) {}
  
  public void reloadModule() {}
}
