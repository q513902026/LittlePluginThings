package me.hope.lpt.module;

import com.google.common.collect.Lists;
import java.util.List;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.BaseModule;
import me.hope.lpt.core.ModuleManager;
import me.hope.lpt.module.commands.TeleportCreateCommand;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class TeleportBlackList extends BaseModule implements Listener {
  private List<String> blackList = Lists.newArrayList();
  
  public boolean canCreate = false;
  
  public TeleportBlackList() {
    super(LittlePluginThings.GetDefaultModuleName(TeleportBlackList.class.toString()), "blacklist");
    saveDefaultConfig();
    saveConfig();
  }
  
  public void onEnable() {
    saveDefaultConfig();
    ModuleManager.registerCommand("portal", (CommandExecutor)(new TeleportCreateCommand()).setParent(this));
    loadBlackList();
    info("开启完成");
  }
  
  private void loadBlackList() {
    List<String> temp = getConfig().getStringList("blacklist");
    info("正在验证有效世界....");
    for (World w : LittlePluginThings.getInstance().getServer().getWorlds()) {
      if (temp.contains(w.getName())) {
        this.blackList.add(w.getName());
        info("[" + w.getName() + "] - in BlackList");
        continue;
      } 
      info("[" + w.getName() + "]");
    } 
    info("验证结束....");
  }
  
  public void toggleCreate() {
    this.canCreate = !this.canCreate;
  }
  
  @EventHandler
  public void onPortalCreateEvent(PortalCreateEvent event) {
    if (this.canCreate)
      return; 
    event.setCancelled(true);
  }
  
  @EventHandler
  public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
    if (event.getPlayer().hasPermission("lpt.teleport"))
      return; 
    if (this.blackList.contains(event.getTo().getWorld().getName()))
      event.setCancelled(true); 
  }
  
  public void reloadModule() {}
}
