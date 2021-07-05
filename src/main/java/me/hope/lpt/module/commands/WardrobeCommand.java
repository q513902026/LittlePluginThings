package me.hope.lpt.module.commands;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.module.WardrobeMender;
import me.hope.lpt.module.sub.CustomNpcSubMod;
import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class WardrobeCommand implements CommandExecutor {
  private static final Map<String, Integer> configList = new HashMap<>();
  
  private PluginCommand command;
  
  private WardrobeMender parent;
  
  public WardrobeCommand() {
    configList.put("armourers:feet", Integer.valueOf(1));
    configList.put("armourers:wings", Integer.valueOf(1));
    configList.put("armourers:head", Integer.valueOf(1));
    configList.put("armourers:chest", Integer.valueOf(1));
    configList.put("armourers:legs", Integer.valueOf(1));
    configList.put("armourers:outfit", Integer.valueOf(1));
  }
  
  public static Object changedSlotLimit(NBTTagCompound nbt, int type, String key, int value) {
    if (nbt.hasKey("ForgeCaps")) {
      NBTTagCompound tempNbt = nbt.getCompound("ForgeCaps");
      if (tempNbt.hasKey("armourers_workshop:player-wardrobe-provider")) {
        NBTTagCompound wardrobe = tempNbt.getCompound("armourers_workshop:player-wardrobe-provider");
        String prefix = "slots-unlocked-";
        switch (type) {
          case 1:
            try {
              wardrobe.setInt(prefix + key, value);
            } catch (NullPointerException e) {
              e.printStackTrace();
              return nbt;
            } 
            break;
          case 2:
            value = Integer.parseInt(key);
            for (String ks : configList.keySet()) {
              if (wardrobe.hasKey(prefix + ks))
                wardrobe.setInt(prefix + ks, value); 
            } 
            break;
          case 0:
            for (Map.Entry<String, Integer> entry : configList.entrySet()) {
              if (wardrobe.hasKey(prefix + (String)entry.getKey()))
                wardrobe.setInt(prefix + (String)entry.getKey(), ((Integer)entry.getValue()).intValue()); 
            } 
            break;
        } 
        tempNbt.set("armourers_workshop:player-wardrobe-provider", (NBTBase)wardrobe);
      } 
      nbt.set("ForgeCaps", (NBTBase)tempNbt);
    } 
    return nbt;
  }
  
  public static Object changedSlotLimit(NBTTagCompound nbt) {
    return changedSlotLimit(nbt, 0, null, 0);
  }
  
  public static Object changedSlotLimit(NBTTagCompound nbt, String key, int value) {
    return changedSlotLimit(nbt, 1, key, 0);
  }
  
  public static Object changedSlotLimit(NBTTagCompound nbt, String key) {
    return changedSlotLimit(nbt, 2, key, 0);
  }
  
  public static List<Player> getOnlinePlayers() {
    return Lists.newArrayList(LittlePluginThings.getInstance().getServer().getOnlinePlayers());
  }
  
  public static Map<String, Integer> getConfigList() {
    return configList;
  }
  
  public WardrobeCommand setParent(WardrobeMender parent) {
    this.parent = parent;
    return this;
  }
  
  public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
    if (commandSender instanceof org.bukkit.command.ConsoleCommandSender) {
      try {
        if (args[0].equalsIgnoreCase("clearList")) {
          this.parent.getConfig().set("players", new ArrayList());
          this.parent.saveConfig();
          this.parent.getConfig().saveToString();
          this.parent.reloadModule();
          LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: 列表清理完成...");
        } else if (args[0].equalsIgnoreCase("sendMail")) {
          Player p = LittlePluginThings.getInstance().getServer().getPlayer(args[1]);
          String senderName = args[2];
          String title = args[3];
          StringBuilder sb = new StringBuilder("\n");
          for (int i = 4; i < args.length; i++)
            sb.append(args[i]); 
          CustomNpcSubMod.INSTANCE.sendMail(senderName, p, title, new String[] { sb.toString() });
          LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: 邮件尝试发送成功...");
        } else if (args[0].equalsIgnoreCase("sendAllPlayerMail")) {
          String senderName = args[1];
          String title = args[2];
          StringBuilder sb = new StringBuilder();
          for (int i = 3; i < args.length; i++)
            sb.append(args[i] + "\n"); 
          for (Player p : getOnlinePlayers()) {
            CustomNpcSubMod.INSTANCE.sendMail(senderName, p, title, new String[] { sb.toString() });
          } 
          LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: 给所有在线玩家尝试发送邮件...成功");
        } 
      } catch (Exception e) {
        e.printStackTrace();
        return true;
      } 
    } else if (commandSender instanceof Player && 
      commandSender.isOp()) {
      Player senderPlayer = (Player)commandSender;
      if (args[0].equalsIgnoreCase("sendMailWithBook")) {
        Player destPlayer = LittlePluginThings.getInstance().getServer().getPlayer(args[1]);
        String senderName = args[2];
        ItemStack itemStack = senderPlayer.getInventory().getItemInOffHand();
        if (itemStack.getType() == Material.WRITTEN_BOOK) {
          BookMeta meta = (BookMeta)itemStack.getItemMeta();
          CustomNpcSubMod.INSTANCE.sendMail(senderName, destPlayer, meta.getTitle(), (String[])meta.getPages().toArray((Object[])new String[0]));
        } else {
          return true;
        } 
      } else if (args[0].equalsIgnoreCase("sendAllMailWithBook")) {
        String senderName = args[1];
        ItemStack itemStack = senderPlayer.getInventory().getItemInOffHand();
        if (itemStack.getType() == Material.WRITTEN_BOOK) {
          BookMeta meta = (BookMeta)itemStack.getItemMeta();
          for (Player destPlayer : getOnlinePlayers())
            CustomNpcSubMod.INSTANCE.sendMail(senderName, destPlayer, meta.getTitle(), (String[])meta.getPages().toArray((Object[])new String[0])); 
          senderPlayer.sendMessage("[WardrobeCommand]:正在尝试给所有在线玩家发送邮件....");
          senderPlayer.sendMessage(String.format("[WardrobeCommand]: 发送者: %s , 标题: %s, 内容: %s", new Object[] { senderName, meta.getTitle(), Arrays.toString(meta.getPages().toArray()) }));
        } else {
          senderPlayer.sendMessage("[WardrobeCommand]: 未在副手检测到正确的成书,请确认后再试");
          return true;
        } 
      } 
    } 
    return true;
  }
}
