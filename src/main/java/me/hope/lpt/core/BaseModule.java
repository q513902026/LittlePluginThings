package me.hope.lpt.core;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.api.PluginInterface;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class BaseModule implements PluginInterface {
  private FileConfiguration config;
  
  private File configFile;
  
  private File dataFolder;
  
  private String name;
  
  private String configName;
  
  private String version;
  
  public BaseModule() {
    this.name = LittlePluginThings.GetDefaultModuleName(getClass().toString());
  }
  
  public BaseModule(String name) {
    this.name = name;
  }
  
  public BaseModule(String name, String configName) {
    this.name = name;
    this.configName = configName;
    this.dataFolder = new File(LittlePluginThings.getInstance().getDataFolder() + File.separator + "configs" + File.separator + this.name);
    this.configFile = new File(this.dataFolder, configName + ".yml");
  }
  
  public <C extends BaseModule> C setName(String name) {
    this.name = name;
    return (C)this;
  }
  
  public <C extends BaseModule> C setConfig(String configName) {
    return setConfig(configName, new File(LittlePluginThings.getInstance().getDataFolder() + File.separator + "configs" + File.separator + this.name));
  }
  
  public <C extends BaseModule> C setConfig(String configName, File dataFolder) {
    this.dataFolder = dataFolder;
    this.configName = configName;
    return (C)this;
  }
  
  public void onLoad() {}
  
  public void onEnable() {}
  
  public void onDisable() {}
  
  public String getName() {
    return this.name;
  }
  
  public void info(String msg) {
    LittlePluginThings.getInstance().getLogger().info("[" + getName() + "]: " + msg);
  }
  
  public final File getDataFolder() {
    return this.dataFolder;
  }
  
  public FileConfiguration getConfig() {
    if (this.config == null)
      reloadConfig(); 
    return this.config;
  }
  
  public void saveConfig() {
    try {
      getConfig().save(this.configFile);
    } catch (IOException ex) {
      info("Could not save config to " + this.configFile);
    } 
  }
  
  public void reloadConfig() {
    this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
    InputStream defConfigStream = getResource(this.configName + ".yml");
    if (defConfigStream == null)
      return; 
    this.config.setDefaults((Configuration)YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
  }
  
  public void saveDefaultConfig() {
    if (!this.configFile.exists())
      saveResource(this.configName + ".yml", false); 
  }
  
  public void saveResource(String resourcePath, boolean replace) {
    LittlePluginThings.getInstance().saveResource("configs" + File.separator + getName() + File.separator + resourcePath, replace);
  }
  
  public InputStream getResource(String filename) {
    return LittlePluginThings.getInstance().getResource("configs" + File.separator + getName() + File.separator + filename);
  }
  
  public abstract void reloadModule();
}
