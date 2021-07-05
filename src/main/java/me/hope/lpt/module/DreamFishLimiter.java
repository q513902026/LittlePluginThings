package me.hope.lpt.module;

import com.google.common.collect.Maps;
import java.util.Map;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.BaseModule;
import me.hope.lpt.module.listeners.FishLimiter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class DreamFishLimiter extends BaseModule {
  private long cooldown = 300L;
  
  private Map<String, PlayerInfo> playerCache = Maps.newHashMap();
  
  private long lastCommand = System.currentTimeMillis();
  
  private LimiterMode mode = LimiterMode.PLAYER;
  
  public DreamFishLimiter() {
    super(LittlePluginThings.GetDefaultModuleName(DreamFishLimiter.class.toString()), "fish-cache");
    saveDefaultConfig();
    saveConfig();
  }
  
  public void onEnable() {
    info("加载配置...");
    if (LittlePluginThings.getInstance().getServer().getPluginManager().getPlugin("DreamFish") == null) {
      info("未找到DreamFish插件");
      return;
    } 
    loadCooldownSetting();
    info("确定当前冷却模式...[" + this.mode.name() + "]");
    info("确定当前冷却时间..." + this.cooldown + "秒");
    info("加载缓存信息....");
    loadPlayerCaches();
    info("尝试对DreamFish进行命令限制...");
    hookDreamFish();
  }
  
  public void onDisable() {
    savePlayerCaches();
  }
  
  private void hookDreamFish() {
    if (LittlePluginThings.getInstance().getServer().getPluginManager().getPlugin("DreamFish") != null) {
      LittlePluginThings.getInstance().getServer().getPluginManager().registerEvents((Listener)new FishLimiter(this), (Plugin)LittlePluginThings.getInstance());
      info("对DreamFish进行命令限制...成功");
    } 
  }
  
  private void loadCooldownSetting() {
    this.cooldown = getConfig().getLong("config.cooldown", 300L);
    this.lastCommand = getConfig().getLong("global.lastCommand", System.currentTimeMillis());
    this.mode = LimiterMode.valueOf(getConfig().getString("config.mode", "PLAYER").toUpperCase());
  }
  
  public long getCooldown() {
    return this.cooldown * 1000L;
  }
  
  public PlayerInfo getPlayerInfo(String name) {
    if (this.playerCache.containsKey(name))
      return this.playerCache.get(name); 
    PlayerInfo playerInfo = new PlayerInfo(name);
    this.playerCache.put(name, playerInfo);
    return playerInfo;
  }
  
  public LimiterMode getMode() {
    return this.mode;
  }
  
  public void setLastCommand() {
    this.lastCommand = System.currentTimeMillis();
  }
  
  public long commpareCooldown() {
    long nowTime = System.currentTimeMillis();
    return nowTime - this.lastCommand;
  }
  
  public PlayerInfo setPlayerCache(PlayerInfo playerInfo) {
    getConfig().set("cache." + playerInfo.getName(), Long.valueOf(playerInfo.getLastCommand()));
    return this.playerCache.put(playerInfo.getName(), playerInfo);
  }
  
  public void loadPlayerCache(String name) {
    long temp = getConfig().getLong("cache." + name, 0L);
    if (temp != 0L)
      if (this.playerCache.containsKey(name)) {
        this.playerCache.put(name, ((PlayerInfo)this.playerCache.get(name)).setLastCommand(temp));
      } else {
        this.playerCache.put(name, new PlayerInfo(name, temp));
      }  
  }
  
  private void loadPlayerCaches() {
    for (String name : getConfig().getStringList("cache"))
      loadPlayerCache(name); 
    info("加载缓存信息...." + this.playerCache.size());
  }
  
  public void savePlayerCache(String name) {
    if (this.playerCache.containsKey(name))
      getConfig().set("cache." + name, Long.valueOf(((PlayerInfo)this.playerCache.get(name)).getLastCommand())); 
    getConfig().saveToString();
    saveConfig();
  }
  
  public void savePlayerCaches() {
    info("caches准备保存...");
    for (Map.Entry<String, PlayerInfo> entry : this.playerCache.entrySet())
      getConfig().set("cache." + (String)entry.getKey(), Long.valueOf(((PlayerInfo)entry.getValue()).getLastCommand())); 
    getConfig().saveToString();
    saveConfig();
    info("caches保存完成...");
  }
  
  public void reloadModule() {
    savePlayerCaches();
    loadPlayerCaches();
  }
  
  public class PlayerInfo {
    private long lastCommand = 0L;
    
    private String name = "";
    
    public PlayerInfo(String name) {
      this.name = name;
    }
    
    public PlayerInfo(String name, long lastCommand) {
      this.name = name;
      this.lastCommand = lastCommand;
    }
    
    public PlayerInfo setLastCommand(long lastCommand) {
      this.lastCommand = lastCommand;
      return this;
    }
    
    public String getName() {
      return this.name;
    }
    
    public long getLastCommand() {
      return this.lastCommand;
    }
    
    public long commpareCooldown() {
      long nowTime = System.currentTimeMillis();
      return nowTime - this.lastCommand;
    }
  }
  
  public enum LimiterMode {
    PLAYER, GLOBAL;
  }
}
