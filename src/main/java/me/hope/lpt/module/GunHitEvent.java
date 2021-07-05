package me.hope.lpt.module;

import com.sucy.skill.api.event.PhysicalDamageEvent;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.BaseModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class GunHitEvent extends BaseModule implements Listener {
  public GunHitEvent() {
    super(LittlePluginThings.GetDefaultModuleName(GunHitEvent.class.toString()));
  }
  
  public void reloadModule() {}
  
  @EventHandler
  public void onPlayerAttack(EntityDamageByEntityEvent event) {}
  
  @EventHandler
  public void onPhysicDamage(PhysicalDamageEvent event) {
    System.out.println("Damage: " + event.getDamage());
    System.out.println("Damager: " + event.getDamager());
    System.out.println("Target: " + event.getTarget());
    System.out.println("isProjectile: " + event.isProjectile());
  }
}
