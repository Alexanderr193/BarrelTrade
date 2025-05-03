package org.alexanderr193.barrelTrade.event;

import org.alexanderr193.barrelTrade.BarrelHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Set;

public class InventoryDragListener implements Listener {
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof BarrelHolder) {
            event.setCancelled(true); // Always cancel drags in our GUI

            // Get which slots are being dragged over lol
            Set<Integer> rawSlots = event.getRawSlots();

            for (int slot : rawSlots) {
                // Top inventory slots range from 0 to size-1
                // Player inventory slots start from size of top inventory

                if (slot < event.getView().getTopInventory().getSize()) {
                    // Dragging in BarrelHolder (top inventory)
                    // player.sendMessage("Dragging in BarrelHolder at slot " + slot);
                } else {
                    // Dragging in player inventory (bottom inventory)
                    // player.sendMessage("Dragging in player inventory");
                    event.setCancelled(false);
                }
            }
        }
    }
}
