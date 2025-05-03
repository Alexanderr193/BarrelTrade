package org.alexanderr193.barrelTrade.view.gui;

import org.alexanderr193.barrelTrade.data.model.TradeInventoryHolder;
import org.alexanderr193.barrelTrade.data.model.Barrel;
import org.alexanderr193.barrelTrade.data.model.Slot;
import org.alexanderr193.barrelTrade.data.serialization.ItemSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BarrelInventoryView {
    public static void openBarrelShop(Barrel barrel, Player player) {
        Inventory inv = Bukkit.createInventory(new TradeInventoryHolder(barrel), InventoryType.BARREL, ChatColor.DARK_GREEN + barrel.getOwner() + "'s store");
        for (Slot slot :  barrel.getSlots()) {
            if (slot.getBase64Product() == null) continue;

            ItemStack item = ItemSerializer.itemStackFromBase64(slot.getBase64Product());
            if (item == null) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                lore.add("Price: " + slot.getAmount() + " " + slot.getCurrency());
                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            inv.setItem(slot.getSlotId(), item);
        }
        player.openInventory(inv);
    }
}
