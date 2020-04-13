package me.hope.lpt;

import com.google.common.collect.Maps;
import me.hope.lpt.core.BaseModule;
import nmstag.NMSUtil;
import nmstag.NMSUtil_1_12;
import org.bukkit.command.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LittlePluginThings extends JavaPlugin {
    private static LittlePluginThings INSTANCE;
    private Map<String,BaseModule> modules = Maps.newHashMap();
    private NMSUtil.PlayerNMS playerNMS;
    private PluginCommand command;
    @Override
    public void onEnable() {
        INSTANCE = this;
        this.getLogger().info("LittlePluginThings 正在查找模块");
        this.saveDefaultConfig();
        this.getLogger().info("LittlePluginThigs 命令初始化...");
        command = getCommand("lpt");
        this.getLogger().info("LittlePluginThings 模块开启中....");
        registerModule(this.getConfig().getStringList("modules"));
        playerNMS =  new NMSUtil_1_12.PlayerNMS();
        onModulesEnable();
        this.getLogger().info("LittlePluginThings 模块开启完毕!");

    }
    private void onModulesEnable(){
        for(BaseModule module : modules.values()){
            module.onEnable();
        }
    }
    @Override
    public void onDisable() {
        INSTANCE = null;
    }
    private void registerListener(Listener ls){
        this.getServer().getPluginManager().registerEvents(ls,this);
    }
    private void registerModule(List<String> modules){
        if (modules !=null){
            for(String s : modules){
                try {
                    Object instance = Class.forName(s).newInstance();
                    if (instance instanceof Listener){
                        registerListener((Listener) instance);
                    }
                    if (instance instanceof BaseModule) {
                        this.modules.put(((BaseModule) instance).getName(), (BaseModule) instance);
                    }
                } catch (InstantiationException e) {
                    this.getLogger().warning("Module<"+ GetDefaultModuleName(s)+"> 在尝试实例化时出现了错误!");
                    continue;
                } catch (IllegalAccessException e) {
                    this.getLogger().warning("Module<"+ GetDefaultModuleName(s)+"> 在获取时权限出现了错误!");
                    continue;
                } catch (ClassNotFoundException e) {
                    this.getLogger().warning("Module<"+ GetDefaultModuleName(s)+"> 未找到对应类!");
                    continue;
                }
            }
        }
    }
    public static String GetDefaultModuleName(String className){
        String[] names = className.split("\\.");
        return names[names.length-1];
    }
    public static LittlePluginThings getInstance(){
        return INSTANCE;
    }
    public NMSUtil.PlayerNMS getPlayerNMS(){return this.playerNMS;}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean success = false;
        if (!this.isEnabled()) {
            throw new CommandException("Cannot execute command '" + label + "' in plugin " + this.getDescription().getFullName() + " - plugin is disabled.");
        } else if (!command.testPermission(sender)) {
            return true;
        } else {
            try {
                if (args ==null | args.length==0){ return true;}
                for(Map.Entry<String,CommandExecutor> entry : BaseModule.getCommandMap().entrySet()){
                    if (args[0].equalsIgnoreCase(entry.getKey())){
                        label = entry.getKey();
                        if (args.length >1){
                            System.arraycopy(args,1,args,0, (args.length - 1));
                        }
                        success = entry.getValue().onCommand(sender,command,args[0],args);
                    }
                }
            } catch (Throwable var9) {
                throw new CommandException("Unhandled exception executing command '" + label + "' in plugin " + getDescription().getFullName(), var9);
            }
            return success;
        }

    }
}
