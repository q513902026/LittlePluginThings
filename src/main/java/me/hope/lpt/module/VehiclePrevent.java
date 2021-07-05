package me.hope.lpt.module;

import java.util.List;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.api.ConfigInstance;
import me.hope.lpt.api.Plugin;
import me.hope.lpt.core.BaseModule;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

@Plugin(name = "VehiclePrevent", version = "1.0")
public class VehiclePrevent extends BaseModule implements Listener {
  private static List<String> onlyEnterLists;
  
  @ConfigInstance
  public Configuration config;
  
  public VehiclePrevent() {
    super(LittlePluginThings.GetDefaultModuleName(VehiclePrevent.class.toString()), "prevent-list");
    saveDefaultConfig();
  }
  
  public void onEnable() {
    loadPreventList();
    info("已成功载入限制目标...." + onlyEnterLists.size());
    info("开启完成");
  }
  
  public void loadPreventList() {
    onlyEnterLists = getConfig().getStringList("prevent");
  }
  
  @EventHandler
  public void onEntityEnterVehicle(VehicleEnterEvent event) {
    if (!onlyEnterLists.contains(event.getEntered().getType().name()))
      event.setCancelled(true); 
  }
  
  public void reloadModule() {
    loadPreventList();
  }
}
