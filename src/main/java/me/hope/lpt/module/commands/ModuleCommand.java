package me.hope.lpt.module.commands;

import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.BaseModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ModuleCommand implements CommandExecutor {
  public LittlePluginThings parent;
  
  public ModuleCommand setParent(LittlePluginThings parent) {
    this.parent = parent;
    return this;
  }
  
  public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
    if (commandSender.hasPermission("lpt.module")) {
      if (label.equalsIgnoreCase("enable"))
        try {
          BaseModule module = this.parent.registerModule("me.hope.lpt.module." + args[1]);
          module.onEnable();
          this.parent.getConfig().getStringList("modules").add("me.hope.lpt.module." + args[1]);
        } catch (ClassNotFoundException e) {
          commandSender.sendMessage("[Lpt]: 不存在的模块");
        } catch (IllegalAccessException e) {
          commandSender.sendMessage("[Lpt]: 不允许的模块");
        } catch (InstantiationException e) {
          commandSender.sendMessage("[Lpt]: 不允许的模块");
        }  
      return true;
    } 
    return false;
  }
}
