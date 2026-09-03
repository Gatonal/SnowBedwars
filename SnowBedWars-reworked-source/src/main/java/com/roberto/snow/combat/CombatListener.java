package com.roberto.snow.combat;

import com.roberto.snow.Snow;
import com.roberto.snow.arena.Arena;
import com.roberto.snow.arena.BedWarsTeam;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

/** Keeps the hot damage path small; arena-only logic runs only for a lethal hit. */
public final class CombatListener implements Listener {
    private final Snow plugin; public CombatListener(Snow plugin){this.plugin=plugin;}
    @EventHandler(ignoreCancelled=true)
    public void damage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player victim=(Player)event.getEntity(); Arena arena=plugin.getArenaManager().getArena(victim);
        if(arena==null || !arena.isPlayingMember(victim.getUniqueId())) return;
        Player attacker=attacker(event);
        if(attacker!=null) { BedWarsTeam a=arena.getTeam(attacker.getUniqueId()), b=arena.getTeam(victim.getUniqueId()); if(a!=null && a==b) { event.setCancelled(true); return; } }
        if(event.getFinalDamage() < victim.getHealth()) return;
        event.setCancelled(true); plugin.defeat(victim,attacker);
    }
    private Player attacker(EntityDamageEvent event) {
        if(!(event instanceof EntityDamageByEntityEvent)) return null;
        Entity damager=((EntityDamageByEntityEvent)event).getDamager();
        if(damager instanceof Player) return (Player)damager;
        if(damager instanceof Projectile) { ProjectileSource source=((Projectile)damager).getShooter(); return source instanceof Player?(Player)source:null; }
        return null;
    }
    @EventHandler(ignoreCancelled=true)
    public void fireball(PlayerInteractEvent event) {
        ItemStack item=event.getItem(); if(item==null || item.getType()!=Material.FIREBALL) return;
        Player player=event.getPlayer(); if(plugin.getArenaManager().getArena(player)==null) return;
        event.setCancelled(true); item.setAmount(item.getAmount()-1);
        Fireball fireball=player.launchProjectile(Fireball.class);
        fireball.setVelocity(player.getLocation().getDirection().normalize().multiply(1.45D)); fireball.setYield(0F); fireball.setIsIncendiary(false);
    }
}
