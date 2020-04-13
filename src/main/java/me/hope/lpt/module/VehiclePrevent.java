package me.hope.lpt.module;

import com.google.common.collect.Lists;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.BaseModule;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.List;


public class VehiclePrevent extends BaseModule implements Listener {
    private static List<String> onlyEnterLists;

    public VehiclePrevent(){
        super(LittlePluginThings.GetDefaultModuleName(VehiclePrevent.class.toString()),"prevent-list");
        this.saveDefaultConfig();
        loadPreventList();
    }

    public void loadPreventList(){
        onlyEnterLists = this.getConfig().getStringList("prevent");
    }

    @EventHandler
    public void onEntityEnterVehicle(VehicleEnterEvent event){
        if (!onlyEnterLists.contains(event.getEntered().getType().name())){
            event.setCancelled(true);
        }
    }

    @Override
    public void reloadModule() {
        loadPreventList();
    }
}
