package org.alexanderr193.barrelTrade.listeners;

import org.alexanderr193.barrelTrade.data.model.TradeInventoryHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class InventoryMoveItemEventListener implements Listener {
    @EventHandler
    public void onHotbarSwap(InventoryMoveItemEvent event) {
        if (event.getDestination().getHolder() instanceof TradeInventoryHolder ||
                event.getSource().getHolder() instanceof TradeInventoryHolder) {
            event.setCancelled(true);
        }
    }
}
