package com.roberto.snow.listener;

import com.roberto.snow.Snow;
import com.roberto.snow.arena.*;
import com.roberto.snow.shop.ShopService;
import com.roberto.snow.util.Text;
import com.roberto.snow.api.BedWarsBedBreakEvent;
import com.roberto.snow.api.BedWarsKillEvent;
import com.roberto.snow.quest.QuestType;
import com.roberto.snow.cosmetic.CosmeticManager;
import com.roberto.snow.quest.QuestManager;
import com.roberto.snow.hotbar.HotbarManager;
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
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Collections;

public final class GameListener implements Listener {
    private final Snow plugin; public GameListener(Snow plugin){this.plugin=plugin;}
    @EventHandler public void onQuit(PlayerQuitEvent event){if(plugin.getArenaManager().getArena(event.getPlayer())!=null){plugin.rememberForRejoin(event.getPlayer());plugin.getArenaManager().leave(event.getPlayer(),true);plugin.getScoreboards().clear(event.getPlayer());}}
    @EventHandler public void onJoin(PlayerJoinEvent event){plugin.getHotbar().giveLobbyItems(event.getPlayer());}
    @EventHandler public void onBreak(BlockBreakEvent event){Player player=event.getPlayer();Arena arena=plugin.getArenaManager().getArena(player);if(arena==null||arena.getState()!=ArenaState.PLAYING)return;Block broken=event.getBlock();for(BedWarsTeam team:arena.getTeams())if(matches(team.getBed(),broken.getLocation())){event.setCancelled(true);if(team.contains(player.getUniqueId())){plugin.getMessages().send(player,"cannot-break-own-bed");return;}team.destroyBed();broken.setType(org.bukkit.Material.AIR);Location effect=broken.getLocation();plugin.getCosmetics().bedBreak(player);arena.getStats(player.getUniqueId()).bed();plugin.getQuests().progress(player,QuestType.BED_BREAK,1);plugin.getPrestiges().addXp(player,30);plugin.getServer().getPluginManager().callEvent(new BedWarsBedBreakEvent(arena,player));plugin.getMessages().send(player,"bed-destroyed",Collections.singletonMap("%team%",Text.color(team.getColorCode()+team.getName())));return;}}
    @EventHandler public void onInventory(InventoryClickEvent event){if(!(event.getWhoClicked() instanceof Player))return;String title=event.getView().getTitle();if(!ShopService.SHOP_TITLE.equals(title)&&!ShopService.UPGRADE_TITLE.equals(title))return;event.setCancelled(true);if(event.getRawSlot()<0||event.getRawSlot()>=event.getInventory().getSize())return;Player player=(Player)event.getWhoClicked();if(ShopService.UPGRADE_TITLE.equals(title))plugin.getShopService().buyUpgrade(player,event.getRawSlot());else plugin.getShopService().buy(player,event.getRawSlot());}
    @EventHandler public void onMenuClick(InventoryClickEvent event){if(!(event.getWhoClicked() instanceof Player))return;String title=event.getView().getTitle();if(!QuestManager.TITLE.equals(title)&&!CosmeticManager.TITLE.equals(title))return;event.setCancelled(true);Player p=(Player)event.getWhoClicked();if(QuestManager.TITLE.equals(title))plugin.getQuests().claim(p,event.getRawSlot());else plugin.getCosmetics().select(p,event.getRawSlot());}
    @EventHandler public void onLobbyItem(PlayerInteractEvent event){if(event.getItem()==null||!event.getItem().hasItemMeta()||!event.getItem().getItemMeta().hasDisplayName())return;Player p=event.getPlayer();String name=event.getItem().getItemMeta().getDisplayName();if("§eDesafios".equals(name)){event.setCancelled(true);plugin.getQuests().open(p);}else if("§dCosméticos".equals(name)){event.setCancelled(true);plugin.getCosmetics().open(p);}}
    private boolean matches(Location configured,Location actual){return configured!=null&&configured.getWorld().equals(actual.getWorld())&&configured.getBlockX()==actual.getBlockX()&&configured.getBlockY()==actual.getBlockY()&&configured.getBlockZ()==actual.getBlockZ();}
}
