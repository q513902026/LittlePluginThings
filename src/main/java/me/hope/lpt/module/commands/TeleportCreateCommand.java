package me.hope.lpt.module.commands;

import me.hope.lpt.module.TeleportBlackList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TeleportCreateCommand implements CommandExecutor {
  private TeleportBlackList parent;
  
  public TeleportCreateCommand setParent(TeleportBlackList parent) {
    this.parent = parent;
    return this;
  }
  
  public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
    if (commandSender.hasPermission("lpt.portal.create")) {
      this.parent.toggleCreate();
      commandSender.sendMessage("[TeleportBlackList]: 现在" + (this.parent.canCreate ? "可以" : "不可以") + "建造传送门!");
      return true;
    } 
    return false;
  }
}
