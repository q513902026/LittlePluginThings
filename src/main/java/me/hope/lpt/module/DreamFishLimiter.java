package me.hope.lpt.module;

import com.google.common.collect.Maps;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.BaseModule;
import me.hope.lpt.module.listeners.FishLimiter;

import java.util.Map;

public class DreamFishLimiter extends BaseModule {
    private long cooldown = 300L;
    private Map<String,PlayerInfo> playerCache =Maps.newHashMap();
    private long lastCommand = System.currentTimeMillis();
    private LimiterMode mode = LimiterMode.PLAYER;
    public DreamFishLimiter(){
        super(LittlePluginThings.GetDefaultModuleName(DreamFishLimiter.class.toString()),"fish-cache");
        this.saveDefaultConfig();

    }
    public void onEnable(){
        this.info("加载配置...");
        loadCooldownSetting();
        this.info("确定当前冷却模式...["+mode.name()+"]");
        this.info("确定当前冷却时间..."+cooldown+"秒");
        this.info("加载缓存信息....");
        loadPlayerCaches();
        this.info("尝试对DreamFish进行命令限制...");
        hookDreamFish();
    }
    public void onDisable(){
        savePlayerCaches();
    }
    private void hookDreamFish() {
        if (LittlePluginThings.getInstance().getServer().getPluginManager().getPlugin("DreamFish") !=null){
            LittlePluginThings.getInstance().getServer().getPluginManager().registerEvents(new FishLimiter(this),LittlePluginThings.getInstance());
            this.info("对DreamFish进行命令限制...成功");
        }
    }
    private void loadCooldownSetting() {
        cooldown = this.getConfig().getLong("config.cooldown",300L);
        lastCommand = this.getConfig().getLong("global.lastCommand",System.currentTimeMillis());
        mode = LimiterMode.valueOf(this.getConfig().getString("config.mode","PLAYER").toUpperCase());
    }
    public long getCooldown(){
        return this.cooldown * 1000;
    }
    public PlayerInfo getPlayerInfo(String name){
        if (playerCache.containsKey(name)){
            return playerCache.get(name);
        }
        PlayerInfo playerInfo = new PlayerInfo(name);
        playerCache.put(name,playerInfo);
        return playerInfo;
    }
    public LimiterMode getMode(){
        return mode;
    }
    public void setLastCommand(){
        this.lastCommand = System.currentTimeMillis();
    }
    public long commpareCooldown(){
        long nowTime = System.currentTimeMillis();
        return nowTime-lastCommand;
    }
    public PlayerInfo setPlayerCache(PlayerInfo playerInfo){
        this.getConfig().set("cache."+playerInfo.getName(),playerInfo.getLastCommand());
        return playerCache.put(playerInfo.getName(),playerInfo);
    }
    public void loadPlayerCache(String name){
        long temp = this.getConfig().getLong("cache."+name,0L);
        if (temp !=0L) {
            if (playerCache.containsKey(name)) {
                playerCache.put(name, playerCache.get(name).setLastCommand(temp));
            } else {
                playerCache.put(name, new PlayerInfo(name, temp));
            }
        }
    }
    private void loadPlayerCaches(){
        long temp;
        for(String name:this.getConfig().getStringList("cache")){
            loadPlayerCache(name);
        }
    }
    public void savePlayerCache(String name){
        if(playerCache.containsKey(name)){
            this.getConfig().set("cache."+name,playerCache.get(name).getLastCommand());
        }
        this.getConfig().saveToString();
        this.saveConfig();
    }
    public void savePlayerCaches(){
        this.info("caches准备保存...");
        for(Map.Entry<String,PlayerInfo> entry : playerCache.entrySet()){
            this.getConfig().set("cache."+entry.getKey(),entry.getValue().getLastCommand());
        }
        this.getConfig().saveToString();
        this.saveConfig();
        this.info("caches保存完成...");
    }
    @Override
    public void reloadModule() {
        savePlayerCaches();
        loadPlayerCaches();
    }
     public class PlayerInfo{
        private long lastCommand = 0L;
        private String name = "";
        public PlayerInfo(String name){
            this.name = name;
        }
        public PlayerInfo(String name,long lastCommand){
            this.name = name;
            this.lastCommand = lastCommand;
        }
        public PlayerInfo setLastCommand(long lastCommand){
            this.lastCommand = lastCommand;
            return this;
        }
        public String getName(){
            return this.name;
        }
        public long getLastCommand(){
            return this.lastCommand;
        }
        public long commpareCooldown(){
            long nowTime = System.currentTimeMillis();
            return nowTime-lastCommand;
        }
    }
    public enum LimiterMode{
        PLAYER(),GLOBAL();

    }
}
