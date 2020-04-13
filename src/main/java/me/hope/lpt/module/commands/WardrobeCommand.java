package me.hope.lpt.module.commands;

import com.google.common.collect.Lists;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.BaseModule;
import me.hope.lpt.module.WardrobeMender;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class WardrobeCommand implements CommandExecutor {
    private static Map<String,Integer> configList = new HashMap<String,Integer>();
    private PluginCommand command;
    private BaseModule parent;
    public WardrobeCommand(){
        configList.put("armourers:feet",1);
        configList.put("armourers:wings",1);
        configList.put("armourers:head",1);
        configList.put("armourers:chest",1);
        configList.put("armourers:legs",1);
    }
    public WardrobeCommand setParent(BaseModule parent){
        this.parent = parent;
        return this;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof ConsoleCommandSender){
            try{
                if (args[0].equalsIgnoreCase("SetPlayerSlot")){
                    final Player p  = LittlePluginThings.getInstance().getServer().getPlayer(args[1]);
                    String key = args[2];
                    LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: " + p.getName() +"的"+key+"设置中...");
                    int value = Integer.parseInt(args[3]);
                    NBTTagCompound nbt = (NBTTagCompound)LittlePluginThings.getInstance().getPlayerNMS().getTag(p);
                    Object changedNBT = changedSlotLimit(nbt,key,value);
                    LittlePluginThings.getInstance().getPlayerNMS().saveData(changedNBT,p);
                    LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: " + p.getName() +"设置完成...");
                }else if (args[0].equalsIgnoreCase("SetPlayer")){
                    final Player p = LittlePluginThings.getInstance().getServer().getPlayer(args[1]);
                    LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: " + p.getName() +"设置中...");
                    String value = args[2];
                    NBTTagCompound nbt = (NBTTagCompound)LittlePluginThings.getInstance().getPlayerNMS().getTag(p);
                    Object changedNBT = changedSlotLimit(nbt,value);
                    LittlePluginThings.getInstance().getPlayerNMS().saveData(changedNBT,p);
                    LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: " + p.getName() +"设置完成...");
                }else if (args[0].equalsIgnoreCase("SetAll")){
                    String value = args[1];
                    for(OfflinePlayer player : getAllPlayers()){
                        LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: " + player.getName() +"设置中...");
                        NBTTagCompound nbt = (NBTTagCompound)LittlePluginThings.getInstance().getPlayerNMS().getTag(player.getPlayer());
                        Object changedNBT = changedSlotLimit(nbt,value);
                        LittlePluginThings.getInstance().getPlayerNMS().saveData(changedNBT,player.getPlayer());
                        LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: " + player.getName() +"设置完成...");
                    }
                }else if (args[0].equalsIgnoreCase("clearList")){
                    parent.getConfig().set("players",new ArrayList<String>());
                    parent.saveConfig();
                    parent.getConfig().saveToString();
                    parent.reloadModule();

                    LittlePluginThings.getInstance().getLogger().info("[WardrobeMender-Command]: 列表清理完成...");
                }
                }catch(Exception e){
                    e.printStackTrace();
                    return true;
                }
            }
        return true;
    }

    public static Object changedSlotLimit(NBTTagCompound nbt,int type,String key,int value){
        NBTTagCompound tempNbt;
        if (nbt.hasKey("ForgeCaps")){
            tempNbt = nbt.getCompound("ForgeCaps");
            if (tempNbt.hasKey("armourers_workshop:player-wardrobe-provider")){
                NBTTagCompound wardrobe = tempNbt.getCompound("armourers_workshop:player-wardrobe-provider");
                String prefix = "slots-unlocked-";
                switch(type){
                    case 1:
                        try{
                            wardrobe.setInt(prefix+key,value);
                        }catch (NullPointerException e) {
                            e.printStackTrace();
                            return nbt;
                        }
                    break;
                    case 2:
                        value = Integer.parseInt(key);
                        for(String ks : configList.keySet()){
                            if (wardrobe.hasKey(prefix+ks)){
                                wardrobe.setInt(prefix+ks,value);
                            }
                        }
                        break;
                    case 0:
                        for(Map.Entry<String,Integer> entry : configList.entrySet()){
                            if (wardrobe.hasKey(prefix+entry.getKey())){
                                wardrobe.setInt(prefix+entry.getKey(),entry.getValue());
                            }
                        }
                        break;
                }
                tempNbt.set("armourers_workshop:player-wardrobe-provider",wardrobe);
            }
            nbt.set("ForgeCaps",tempNbt);
        }
        return nbt;
    }
    public static Object changedSlotLimit(NBTTagCompound nbt) {
        return changedSlotLimit(nbt,0,null,0);
    }
    public static Object  changedSlotLimit(NBTTagCompound nbt,String key,int value) {
        return changedSlotLimit(nbt,1,key,0);
    }
    public static Object changedSlotLimit(NBTTagCompound nbt,String key) {
        return changedSlotLimit(nbt,2,key,0);
    }

    public static List<OfflinePlayer> getAllPlayers(){
        return Lists.newArrayList(LittlePluginThings.getInstance().getServer().getOnlinePlayers());
    }
    public static Map<String,Integer> getConfigList(){return configList;}

}
