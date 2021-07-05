package me.hope.lpt.module;

import java.util.List;
import java.util.Map;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.BaseModule;
import me.hope.lpt.core.ModuleManager;
import me.hope.lpt.module.commands.WardrobeCommand;
import me.hope.lpt.module.sub.CustomNpcSubMod;
import me.hope.lpt.module.sub.ForgeSubMod;
import me.hope.lpt.module.sub.WardrobeSubModMender;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WardrobeMender extends BaseModule implements Listener {
  private boolean useNBT;
  
  private List<String> playerList;
  
  public WardrobeMender() {
    super(LittlePluginThings.GetDefaultModuleName(WardrobeMender.class.toString()), "playerlist");
    saveDefaultConfig();
    this.useNBT = getConfig().getBoolean("useNBT", false);
    this.playerList = getConfig().getStringList("players");
  }
  
  public void onEnable() {
    info("正在注册命令...");
    ModuleManager.registerCommand("sar", (CommandExecutor)(new WardrobeCommand()).setParent(this));
    info("开始对MOD进行兼容");
    ForgeSubMod.INSTANCE.init();
    CustomNpcSubMod.INSTANCE.init();
    WardrobeSubModMender.INSTANCE.init();
    info("开启完成");
  }
  
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    if (!event.getPlayer().hasPermission("lpt.wardrobe.ignore") && 
      !this.playerList.contains(event.getPlayer().getName())) {
      final Player p = event.getPlayer();
      final List<ItemStack> skinItemStackList = WardrobeSubModMender.INSTANCE.getSkinInventoryContainerItemStack((HumanEntity)p);
      (new BukkitRunnable() {
          public void run() {
            WardrobeSubModMender.INSTANCE.sendToPlayer(p, skinItemStackList);
          }
        }).runTaskLater((Plugin)LittlePluginThings.getInstance(), 20L);
      LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: " + p.getName() + "设置中...");
      if (this.useNBT) {
        NBTTagCompound nbt = (NBTTagCompound)LittlePluginThings.getInstance().getPlayerNMS().getTag(p);
        Object changedNBT = WardrobeCommand.changedSlotLimit(nbt);
        LittlePluginThings.getInstance().getPlayerNMS().saveData(changedNBT, p);
        if (checkNBT((NBTTagCompound)LittlePluginThings.getInstance().getPlayerNMS().getTag(p))) {
          savePlayerList(p);
        } else {
          LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: " + p.getName() + "设置失败...");
        } 
      } else {
        WardrobeSubModMender.INSTANCE.setAllUnlockCount(p, 1);
        savePlayerList(p);
      } 
    } 
  }
  
  private void savePlayerList(Player p) {
    this.playerList.add(p.getName());
    LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: " + p.getName() + "设置完成...");
    getConfig().set("players", this.playerList);
    saveConfig();
    getConfig().saveToString();
  }
  
  private boolean checkNBT(NBTTagCompound nbt) {
    if (nbt.hasKey("ForgeCaps")) {
      NBTTagCompound tempNbt = nbt.getCompound("ForgeCaps");
      if (tempNbt.hasKey("armourers_workshop:player-wardrobe-provider")) {
        NBTTagCompound wardrobe = tempNbt.getCompound("armourers_workshop:player-wardrobe-provider");
        String prefix = "slots-unlocked-";
        for (Map.Entry<String, Integer> entry : (Iterable<Map.Entry<String, Integer>>)WardrobeCommand.getConfigList().entrySet()) {
          if (wardrobe.hasKey(prefix + (String)entry.getKey()) && 
            wardrobe.getInt(prefix + (String)entry.getKey()) != ((Integer)entry.getValue()).intValue())
            return false; 
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public void reloadModule() {
    this.useNBT = getConfig().getBoolean("useNBT");
    this.playerList = getConfig().getStringList("players");
  }
}
