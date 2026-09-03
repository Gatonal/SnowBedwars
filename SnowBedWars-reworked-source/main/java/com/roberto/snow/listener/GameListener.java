package com.roberto.snow.listener;

import com.roberto.snow.Snow;
import com.roberto.snow.arena.*;
import com.roberto.snow.shop.ShopService;
import com.roberto.snow.util.Text;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.Collections;

public final class GameListener implements Listener {
    private final Snow plugin; public GameListener(Snow plugin){this.plugin=plugin;}
    @EventHandler public void onQuit(PlayerQuitEvent event){if(plugin.getArenaManager().getArena(event.getPlayer())!=null){plugin.rememberForRejoin(event.getPlayer());plugin.getArenaManager().leave(event.getPlayer(),true);plugin.getScoreboards().clear(event.getPlayer());}}
    @EventHandler public void onJoin(PlayerJoinEvent event){/* rejoin is explicit to avoid accidental teleports */}
    @EventHandler public void onDeath(PlayerDeathEvent event){Player victim=event.getEntity();Arena arena=plugin.getArenaManager().getArena(victim);if(arena==null)return;Player killer=victim.getKiller();if(killer!=null&&plugin.getArenaManager().getArena(killer)==arena){BedWarsTeam victimTeam=arena.getTeam(victim.getUniqueId());arena.getStats(killer.getUniqueId()).kill(victimTeam!=null&&!victimTeam.isBedAlive());}}
    @EventHandler public void onBreak(BlockBreakEvent event){Player player=event.getPlayer();Arena arena=plugin.getArenaManager().getArena(player);if(arena==null||arena.getState()!=ArenaState.PLAYING)return;Block broken=event.getBlock();for(BedWarsTeam team:arena.getTeams())if(matches(team.getBed(),broken.getLocation())){event.setCancelled(true);if(team.contains(player.getUniqueId())){plugin.getMessages().send(player,"cannot-break-own-bed");return;}team.destroyBed();broken.setType(org.bukkit.Material.AIR);Location effect=broken.getLocation();effect.getWorld().playEffect(effect,Effect.EXPLOSION_LARGE,0);effect.getWorld().playSound(effect,Sound.ANVIL_USE,1F,0.7F);effect.getWorld().playSound(effect,Sound.ENDERDRAGON_HIT,1F,1F);arena.getStats(player.getUniqueId()).bed();plugin.getMessages().send(player,"bed-destroyed",Collections.singletonMap("%team%",Text.color(team.getColorCode()+team.getName())));return;}}
    @EventHandler public void onInventory(InventoryClickEvent event){if(!(event.getWhoClicked() instanceof Player))return;String title=event.getView().getTitle();if(!ShopService.SHOP_TITLE.equals(title)&&!ShopService.UPGRADE_TITLE.equals(title))return;event.setCancelled(true);Player player=(Player)event.getWhoClicked();if(ShopService.UPGRADE_TITLE.equals(title))plugin.getShopService().buyUpgrade(player,event.getRawSlot());}
    private boolean matches(Location configured,Location actual){return configured!=null&&configured.getWorld().equals(actual.getWorld())&&configured.getBlockX()==actual.getBlockX()&&configured.getBlockY()==actual.getBlockY()&&configured.getBlockZ()==actual.getBlockZ();}
}
