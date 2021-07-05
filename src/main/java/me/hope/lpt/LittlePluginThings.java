package me.hope.lpt;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import me.hope.lpt.core.BaseModule;
import me.hope.lpt.core.ModuleManager;
import me.hope.lpt.util.ClassUtil;
import nmstag.NMSUtil;
import nmstag.NMSUtil_1_12;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class LittlePluginThings extends JavaPlugin {
  private static LittlePluginThings INSTANCE;
  
  private Map<String, BaseModule> modules = Maps.newHashMap();
  
  private NMSUtil.PlayerNMS playerNMS;
  
  private NMSUtil.ItemStackNMS itemStackNMS;
  
  private PluginCommand command;
  
  public void onEnable() {
    INSTANCE = this;
    getLogger().info("LittlePluginThings 正在查找模块");
    saveDefaultConfig();
    getLogger().info("LittlePluginThigs 命令初始化...");
    this.command = getCommand("lpt");
    getLogger().info("LittlePluginThings 模块开启中....");
    (ModuleManager.getInstance()).modulesList.addAll(getConfig().getStringList("modules"));
    registerModule((ModuleManager.getInstance()).modulesList);
    this.playerNMS = (NMSUtil.PlayerNMS)new NMSUtil_1_12.PlayerNMS();
    this.itemStackNMS = (NMSUtil.ItemStackNMS)new NMSUtil_1_12.ItemStackNMS();
    ClassUtil.clearCache();
    onModulesEnable();
    getLogger().info("LittlePluginThings 模块开启完毕!");
  }
  
  private void onModulesEnable() {
    for (BaseModule module : this.modules.values())
      module.onEnable(); 
  }
  
  private void onModulesDisable() {
    for (BaseModule module : this.modules.values())
      module.onDisable(); 
  }
  
  public void onDisable() {
    onModulesDisable();
    getLogger().info("LittlePluginThings 模块关闭完毕!");
    INSTANCE = null;
  }
  
  private void registerListener(Listener ls) {
    getServer().getPluginManager().registerEvents(ls, (Plugin)this);
  }
  
  public BaseModule registerModule(String path) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    Object instance = Class.forName(path).newInstance();
    if (instance instanceof Listener)
      registerListener((Listener)instance); 
    if (instance instanceof BaseModule)
      this.modules.put(((BaseModule)instance).getName(), (BaseModule)instance); 
    return (BaseModule)instance;
  }
  
  private void registerModule(Collection<String> modules) {
    if (modules != null)
      for (String s : modules) {
        try {
          registerModule(s);
        } catch (InstantiationException e) {
          getLogger().warning("Module<" + GetDefaultModuleName(s) + "> 在尝试实例化时出现了错误!");
        } catch (IllegalAccessException e) {
          getLogger().warning("Module<" + GetDefaultModuleName(s) + "> 在获取时权限出现了错误!");
        } catch (ClassNotFoundException e) {
          getLogger().warning("Module<" + GetDefaultModuleName(s) + "> 未找到对应类!");
        } 
      }  
  }
  
  public static String GetDefaultModuleName(String className) {
    String[] names = className.split("\\.");
    return names[names.length - 1];
  }
  
  public static LittlePluginThings getInstance() {
    return INSTANCE;
  }
  
  public NMSUtil.ItemStackNMS getItemStackNMS() {
    return this.itemStackNMS;
  }
  
  public NMSUtil.PlayerNMS getPlayerNMS() {
    return this.playerNMS;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    boolean success = false;
    if (!isEnabled())
      throw new CommandException("Cannot execute command '" + label + "' in plugin " + getDescription().getFullName() + " - plugin is disabled."); 
    if (!command.testPermission(sender))
      return true; 
    try {
      if ((((args == null) ? 1 : 0) | ((args.length == 0) ? 1 : 0)) != 0)
        return true; 
      for (Map.Entry<String, CommandExecutor> entry : (Iterable<Map.Entry<String, CommandExecutor>>)ModuleManager.getCommandMap().entrySet()) {
        if (args[0].equalsIgnoreCase(entry.getKey())) {
          label = entry.getKey();
          String[] new_args = {};
          if (args.length > 1){
              new_args = new String[args.length - 1];
              System.arraycopy(args, 1, args, 0, args.length - 1); 
            }
          success = ((CommandExecutor)entry.getValue()).onCommand(sender, command, args[0], new_args);
        } 
      } 
    } catch (Throwable var9) {
      throw new CommandException("Unhandled exception executing command '" + label + "' in plugin " + getDescription().getFullName(), var9);
    } 
    return success;
  }
}
