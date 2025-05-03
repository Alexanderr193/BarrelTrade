package org.alexanderr193.barrelTrade.listeners;

import org.alexanderr193.barrelTrade.data.model.TradeInventoryHolder;
import org.alexanderr193.barrelTrade.data.model.Barrel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;


public class InventoryCloseListener implements Listener {
    private final TradeListener tradeListener;

    public InventoryCloseListener(TradeListener tradeListener) {
        this.tradeListener = tradeListener;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof TradeInventoryHolder tradeInventoryHolder) {
            Barrel barrel = tradeInventoryHolder.getBarrel();
            if (tradeListener.getPlayerByBarrel(barrel).isPresent()) {
                tradeListener.removeBarrel(barrel);
            }
        }
    }
}
