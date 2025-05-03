package org.alexanderr193.barrelTrade.utils;

import org.alexanderr193.barrelTrade.BarrelHolder;
import org.alexanderr193.barrelTrade.barrel.Barrel;
import org.alexanderr193.barrelTrade.barrel.Currency;
import org.alexanderr193.barrelTrade.barrel.Slot;
import org.alexanderr193.barrelTrade.event.BarrelAddedListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Util {
    public static void updateInventoryWithBarrel(Barrel barrel, Player player) {
        Inventory inv = Bukkit.createInventory(new BarrelHolder(barrel), InventoryType.BARREL, ChatColor.DARK_GREEN + barrel.getOwner() + "'s store");
        List<Slot> slots = barrel.getSlots();
        for (Slot slot : slots) {
            ItemStack itemStack = Serialization.itemStackFromBase64(slot.getBase64Product());
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(List.of("Price: " + slot.getAmount() + " " + slot.getCurrency()));
            itemStack.setItemMeta(itemMeta);
            inv.setItem(slot.getSlotId(), itemStack);
        }

        player.openInventory(inv);
    }

    public static boolean takeItemsFromPlayer(Player player, Currency currency, int amount) {
        Material material = null;

        switch (currency) {
            case COAL -> material = Material.COAL;
            case EMERALD -> material = Material.EMERALD;
            case DIAMOND -> material = Material.DIAMOND;
            case IRON -> material = Material.IRON_INGOT;
            case NETHERITE -> material = Material.NETHERITE_INGOT;
        }

        if (!player.getInventory().contains(material, amount)) {
            return false;
        }

        Inventory inventory = player.getInventory();
        int remaining = amount;

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material) {
                int remove = Math.min(item.getAmount(), remaining);
                item.setAmount(item.getAmount() - remove);
                remaining -= remove;

                if (remaining <= 0) break;
            }
        }

        player.updateInventory();
        return true;
    }

}
