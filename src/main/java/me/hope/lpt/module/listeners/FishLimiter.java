package me.hope.lpt.module.listeners;

import com.google.common.collect.Lists;
import me.hope.lpt.core.BaseModule;
import me.hope.lpt.module.DreamFishLimiter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class FishLimiter implements Listener {
    private DreamFishLimiter module;
    private List<String> aliases = Lists.newArrayList("dreamfish","fish","fishing");
    public FishLimiter(DreamFishLimiter module){
        this.module = module;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        if(isFish(event.getMessage())){
            boolean onCooldown = !onCommand(event.getPlayer(),module.getMode());
            event.setCancelled(onCooldown);
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        module.loadPlayerCache(event.getPlayer().getName());
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        module.savePlayerCache(event.getPlayer().getName());
    }
    private boolean isFish(String message){
        String[] temps = message.substring(1).split(" ");
        return aliases.contains(temps[0]);
    }
    public boolean onCommand(Player player, DreamFishLimiter.LimiterMode mode) {
        boolean result = false;
        switch(mode) {
            case PLAYER:
            DreamFishLimiter.PlayerInfo playerInfo = module.getPlayerInfo(player.getPlayer().getName());
            if (playerInfo != null) {
                if (playerInfo.commpareCooldown() > module.getCooldown()) {
                    playerInfo.setLastCommand(System.currentTimeMillis());
                    module.setPlayerCache(playerInfo);
                    result = true;
                    break;
                } else {
                    player.sendMessage("[FishLimiter] :当前命令处于冷却，剩余" + Math.floor((module.getCooldown() - playerInfo.commpareCooldown()) / 1000) + "秒");
                }
            }
            break;
            case GLOBAL:
                if (module.commpareCooldown() > module.getCooldown()) {
                    module.setLastCommand();
                    result = true;
                    break;
                } else {
                    player.sendMessage("[FishLimiter] :当前命令处于冷却，剩余" + Math.floor((module.getCooldown() - module.commpareCooldown()) / 1000) + "秒");
                }
                break;
        }
        return result;
    }
}
