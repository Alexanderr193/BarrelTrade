package org.alexanderr193.barrelTrade.event;

import org.alexanderr193.barrelTrade.BarrelHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class InventoryMoveItemEventListener implements Listener {
    @EventHandler
    public void onHotbarSwap(InventoryMoveItemEvent event) {
        if (event.getDestination().getHolder() instanceof BarrelHolder ||
                event.getSource().getHolder() instanceof BarrelHolder) {
            event.setCancelled(true);
        }
    }
}
