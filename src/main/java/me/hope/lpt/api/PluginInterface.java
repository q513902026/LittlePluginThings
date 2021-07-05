package me.hope.lpt.api;

import org.bukkit.configuration.Configuration;

public interface PluginInterface {
  void onLoad();
  
  void onEnable();
  
  void onDisable();
  
  Configuration getConfig();
  
  void saveDefaultConfig();
  
  void reloadConfig();
  
  void saveResource(String paramString, boolean paramBoolean);
}
