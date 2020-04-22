package me.hope.lpt.core;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.module.AchievementListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;


public abstract class BaseModule {
    private FileConfiguration config;
    private File configFile;
    private File dataFolder;
    private String name;
    private String configName;
    protected static Map<String, CommandExecutor> commands =Maps.newHashMap();
    public BaseModule(){
        this.name = LittlePluginThings.GetDefaultModuleName(this.getClass().toString());
        info(name + "正在初始化.");
    }
    public BaseModule(String name){
        this.name = name;
        info(name + "正在初始化.");
    }
    public BaseModule(String name,String configName){
        this.name = name;
        this.configName = configName;
        info(name + "正在初始化.");
        this.dataFolder = new File(LittlePluginThings.getInstance().getDataFolder()+File.separator+this.name);
        this.configFile = new File(dataFolder,configName+".yml");
        info("正在加载配置文件...");
    }
    public void onEnable(){};
    public void onDisable(){};
    public String getName(){
        return name;
    }

    public void info(String msg){
        LittlePluginThings.getInstance().getLogger().info("["+getName()+"]: "+msg);
    }

    public final File getDataFolder() {
        return dataFolder;
    }

    public abstract void reloadModule();

    public FileConfiguration getConfig(){
        if (config == null){
            reloadConfig();
        }
        return config;
    }
    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            info("Could not save config to " + configFile);
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = getResource(configName+".yml");
        if (defConfigStream == null) {
            return;
        }

        config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource(configName+".yml", false);
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        LittlePluginThings.getInstance().saveResource(getName()+ File.separator+resourcePath,replace);
    }

    public InputStream getResource(String filename) {
        return LittlePluginThings.getInstance().getResource(getName()+File.separator+filename);
    }
    public static <T extends CommandExecutor> T getCommandExecutor(String name){return (T) commands.get(name);}

    public static <T extends CommandExecutor> T registerCommand(String name,T command){
        T put = (T) commands.put(name, command);
        LittlePluginThings.getInstance().getLogger().info("[LittlePluginThings - CommandMap] : 命令"+name+"注册完成.");
        return put;
    }

    public static Map<String,CommandExecutor> getCommandMap(){return commands;}
}
