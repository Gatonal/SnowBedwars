package com.roberto.snow.shop;

import com.roberto.snow.Snow;
import com.roberto.snow.arena.Arena;
import com.roberto.snow.arena.BedWarsTeam;
import com.roberto.snow.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public final class ShopService {
    public static final String SHOP_TITLE=Text.color("&1[&bSnow&1] &fLoja"); public static final String UPGRADE_TITLE=Text.color("&1[&bSnow&1] &fUpgrades");
    private final Snow plugin; public ShopService(Snow plugin){this.plugin=plugin;}
    public void openShop(Player player){Inventory inventory=Bukkit.createInventory(null,27,SHOP_TITLE);item(inventory,10,Material.WOOL,"&bBlocos", "&f16 lã por &b4 ferro");item(inventory,12,Material.STONE_SWORD,"&bCombate", "&fEspada de pedra por &b10 ferro");item(inventory,14,Material.CHAINMAIL_BOOTS,"&bArmadura", "&fBotas permanentes por &b40 ferro");item(inventory,16,Material.TNT,"&bUtilidades", "&fTNT por &b4 ouro");player.openInventory(inventory);}
    public void openUpgrades(Player player){Inventory inventory=Bukkit.createInventory(null,27,UPGRADE_TITLE);item(inventory,10,Material.IRON_CHESTPLATE,"&bProteção", "&fMelhora a armadura do time");item(inventory,12,Material.IRON_SWORD,"&bAfiação", "&fEspadas do time recebem afiação");item(inventory,14,Material.FURNACE,"&bGeradores", "&fAcelera os geradores da ilha");item(inventory,16,Material.BEACON,"&bPoço de cura", "&fRegeneração na base");item(inventory,22,Material.TRIPWIRE_HOOK,"&bArmadilha", "&fAlerta de invasores");player.openInventory(inventory);}
    public void buyUpgrade(Player player,int slot){Arena arena=plugin.getArenaManager().getArena(player);if(arena==null)return;BedWarsTeam team=arena.getTeam(player.getUniqueId());if(team==null)return; if(slot==10)team.increaseProtection();else if(slot==12)team.enableSharpness();else if(slot==14)team.increaseGenerator();else if(slot==16)team.enableHealPool();else if(slot==22)team.addTrap();player.sendMessage(Text.color("&1[&bSnow&1] &fUpgrade adquirido para o time."));}
    private void item(Inventory inv,int slot,Material material,String name,String lore){ItemStack stack=new ItemStack(material);ItemMeta meta=stack.getItemMeta();meta.setDisplayName(Text.color(name));meta.setLore(Arrays.asList(Text.color(lore)));stack.setItemMeta(meta);inv.setItem(slot,stack);}
}
