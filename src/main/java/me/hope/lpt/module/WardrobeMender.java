package me.hope.lpt.module;


import com.google.common.collect.Lists;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.BaseModule;
import me.hope.lpt.module.commands.WardrobeCommand;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;


import java.util.*;

public class WardrobeMender extends BaseModule implements Listener {
    private List<String> playerList = Lists.newArrayList();

    public WardrobeMender() {
        super(LittlePluginThings.GetDefaultModuleName(WardrobeMender.class.toString()),"playerlist");
        this.saveDefaultConfig();
        playerList = this.getConfig().getStringList("players");

    }

    @Override
    public void onEnable() {
        this.info("正在注册命令...");
        registerCommand("sar",new WardrobeCommand().setParent(this));

    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (!event.getPlayer().hasPermission("lpt.wardrobe.ignore")){
            if(!playerList.contains(event.getPlayer().getName())){
                final Player p = event.getPlayer();
                LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: " + p.getName() +"设置中...");
                    NBTTagCompound nbt = (NBTTagCompound)LittlePluginThings.getInstance().getPlayerNMS().getTag(p);
                    Object changedNBT = WardrobeCommand.changedSlotLimit(nbt);
                    LittlePluginThings.getInstance().getPlayerNMS().saveData(changedNBT,p);
                if (checkNBT((NBTTagCompound) LittlePluginThings.getInstance().getPlayerNMS().getTag(p))){
                    playerList.add(p.getName());
                    LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: " + p.getName() +"设置完成...");
                    this.getConfig().set("players", playerList);
                    this.saveConfig();
                    this.getConfig().saveToString();
                }else{
                    LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: " + p.getName() +"设置失败...");
                }
            }
        }
    }
    private boolean checkNBT(NBTTagCompound nbt){
        NBTTagCompound tempNbt;
        if (nbt.hasKey("ForgeCaps")) {
            tempNbt = nbt.getCompound("ForgeCaps");
            if (tempNbt.hasKey("armourers_workshop:player-wardrobe-provider")) {
                NBTTagCompound wardrobe = tempNbt.getCompound("armourers_workshop:player-wardrobe-provider");
                String prefix = "slots-unlocked-";
                int error = 0;
                for(Map.Entry<String,Integer> entry : WardrobeCommand.getConfigList().entrySet()){
                    if (wardrobe.hasKey(prefix+entry.getKey())){
                        if(wardrobe.getInt(prefix+entry.getKey()) != entry.getValue()){
                            error +=1;
                        }
                    }
                }
                if (error == 0 ){
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void reloadModule() {
        playerList = this.getConfig().getStringList("players");
    }
}
