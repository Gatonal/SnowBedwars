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

/**
 * Serviço responsável pela loja e pelos upgrades.
 * A renderização dos NPCs permanece separada.
 */
public final class ShopService {

    public static final String SHOP_TITLE =
            Text.color("&1[&bSnow&1] &fLoja Rápida");

    public static final String UPGRADE_TITLE =
            Text.color("&1[&bSnow&1] &fUpgrades");

    private final Snow plugin;

    public ShopService(Snow plugin) {
        this.plugin = plugin;
    }

    /**
     * Abre a loja principal.
     */
    public void openShop(Player p) {

        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                SHOP_TITLE
        );

        // Blocos
        offer(
                inventory,
                10,
                Material.WOOL,
                16,
                Material.IRON_INGOT,
                4,
                "&fLã"
        );

        offer(
                inventory,
                11,
                Material.ENDER_STONE,
                12,
                Material.IRON_INGOT,
                24,
                "&fPedra do Fim"
        );

        offer(
                inventory,
                12,
                Material.LADDER,
                16,
                Material.IRON_INGOT,
                4,
                "&fEscadas"
        );

        // Armas
        offer(
                inventory,
                19,
                Material.STONE_SWORD,
                1,
                Material.IRON_INGOT,
                10,
                "&fEspada de pedra"
        );

        offer(
                inventory,
                20,
                Material.IRON_SWORD,
                1,
                Material.GOLD_INGOT,
                7,
                "&fEspada de ferro"
        );

        offer(
                inventory,
                21,
                Material.BOW,
                1,
                Material.GOLD_INGOT,
                12,
                "&fArco"
        );

        // Armaduras
        offer(
                inventory,
                28,
                Material.CHAINMAIL_BOOTS,
                1,
                Material.IRON_INGOT,
                40,
                "&fBotas de cota"
        );

        offer(
                inventory,
                29,
                Material.IRON_BOOTS,
                1,
                Material.GOLD_INGOT,
                12,
                "&fBotas de ferro"
        );

        // Utilidades
        offer(
                inventory,
                37,
                Material.TNT,
                1,
                Material.GOLD_INGOT,
                4,
                "&fTNT"
        );

        offer(
                inventory,
                38,
                Material.FIREBALL,
                1,
                Material.IRON_INGOT,
                40,
                "&fBola de fogo"
        );

        offer(
                inventory,
                39,
                Material.ENDER_PEARL,
                1,
                Material.EMERALD,
                4,
                "&fPérola do fim"
        );

        offer(
                inventory,
                40,
                Material.WATER_BUCKET,
                1,
                Material.GOLD_INGOT,
                3,
                "&fBalde d'água"
        );

        p.openInventory(inventory);
    }

    /**
     * Realiza a compra de um item da loja.
     */
    public void buy(Player p, int slot) {

        Inventory openInventory =
                p.getOpenInventory().getTopInventory();

        ItemStack wanted = openInventory.getItem(slot);

        if (wanted == null || !wanted.hasItemMeta()) {
            return;
        }

        ItemMeta meta = wanted.getItemMeta();

        if (meta == null || meta.getLore() == null
                || meta.getLore().isEmpty()) {
            return;
        }

        String lore = meta.getLore().get(0);

        if (lore == null) {
            return;
        }

        lore = lore.replace("§0", "");

        String[] data = lore.split(":");

        if (data.length != 2) {
            return;
        }

        Material currency = Material.matchMaterial(data[0]);

        if (currency == null) {
            return;
        }

        int price;

        try {
            price = Integer.parseInt(data[1]);
        } catch (NumberFormatException ex) {
            return;
        }

        if (price <= 0) {
            return;
        }

        if (count(p, currency) < price) {
            p.sendMessage("§cRecursos insuficientes.");
            return;
        }

        take(p, currency, price);

        ItemStack grant = wanted.clone();

        ItemMeta grantMeta = grant.getItemMeta();

        if (grantMeta != null) {
            grantMeta.setLore(null);
            grant.setItemMeta(grantMeta);
        }

        p.getInventory().addItem(grant);

        p.sendMessage("§aCompra realizada!");
    }

    /**
     * Abre a loja de upgrades.
     */
    public void openUpgrades(Player p) {

        Inventory inventory = Bukkit.createInventory(
                null,
                27,
                UPGRADE_TITLE
        );

        upgrade(
                inventory,
                10,
                Material.IRON_CHESTPLATE,
                "&bProteção",
                4
        );

        upgrade(
                inventory,
                12,
                Material.IRON_SWORD,
                "&bAfiação",
                4
        );

        upgrade(
                inventory,
                14,
                Material.FURNACE,
                "&bGeradores",
                4
        );

        upgrade(
                inventory,
                16,
                Material.BEACON,
                "&bPoço de cura",
                3
        );

        upgrade(
                inventory,
                22,
                Material.TRIPWIRE_HOOK,
                "&bArmadilha",
                2
        );

        p.openInventory(inventory);
    }

    /**
     * Compra um upgrade para o time.
     */
    public void buyUpgrade(Player p, int slot) {

        Arena arena = plugin.getArenaManager().getArena(p);

        if (arena == null) {
            return;
        }

        BedWarsTeam team =
                arena.getTeam(p.getUniqueId());

        if (team == null) {
            return;
        }

        ItemStack item =
                p.getOpenInventory()
                        .getTopInventory()
                        .getItem(slot);

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null
                || meta.getLore() == null
                || meta.getLore().isEmpty()) {
            return;
        }

        String lore = meta.getLore().get(0);

        int cost;

        try {
            cost = Integer.parseInt(
                    lore.replaceAll("[^0-9]", "")
            );
        } catch (NumberFormatException ex) {
            return;
        }

        if (cost <= 0) {
            return;
        }

        if (count(p, Material.DIAMOND) < cost) {
            p.sendMessage("§cVocê precisa de diamantes.");
            return;
        }

        take(
                p,
                Material.DIAMOND,
                cost
        );

        if (slot == 10) {

            team.increaseProtection();

        } else if (slot == 12) {

            team.enableSharpness();

        } else if (slot == 14) {

            team.increaseGenerator();

        } else if (slot == 16) {

            team.enableHealPool();

        } else if (slot == 22) {

            team.addTrap();

        } else {

            return;
        }

        p.sendMessage(
                "§aUpgrade adquirido para o time."
        );
    }

    /**
     * Cria um item da loja.
     */
    private void offer(
            Inventory inventory,
            int slot,
            Material product,
            int amount,
            Material money,
            int price,
            String name
    ) {

        ItemStack item =
                new ItemStack(product, amount);

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        meta.setDisplayName(
                Text.color(name)
        );

        meta.setLore(
                Arrays.asList(
                        "§0" + money.name() + ":" + price,
                        "§7Custo: §f"
                                + price
                                + " "
                                + money.name()
                )
        );

        item.setItemMeta(meta);

        inventory.setItem(
                slot,
                item
        );
    }

    /**
     * Cria um item de upgrade.
     */
    private void upgrade(
            Inventory inventory,
            int slot,
            Material material,
            String name,
            int price
    ) {

        ItemStack item =
                new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        meta.setDisplayName(
                Text.color(name)
        );

        meta.setLore(
                Arrays.asList(
                        "§7" + price + " diamantes"
                )
        );

        item.setItemMeta(meta);

        inventory.setItem(
                slot,
                item
        );
    }

    /**
     * Conta quantos itens de determinado material o jogador possui.
     */
    private int count(Player p, Material material) {

        int amount = 0;

        for (ItemStack item :
                p.getInventory().getContents()) {

            if (item != null
                    && item.getType() == material) {

                amount += item.getAmount();
            }
        }

        return amount;
    }

    /**
     * Remove uma quantidade de determinado material.
     */
    private void take(
            Player p,
            Material material,
            int amount
    ) {

        for (ItemStack item :
                p.getInventory().getContents()) {

            if (item == null
                    || item.getType() != material) {
                continue;
            }

            int used = Math.min(
                    amount,
                    item.getAmount()
            );

            item.setAmount(
                    item.getAmount() - used
            );

            amount -= used;

            if (amount <= 0) {
                return;
            }
        }
    }
}