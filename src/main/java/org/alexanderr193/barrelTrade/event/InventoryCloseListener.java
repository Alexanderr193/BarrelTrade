package org.alexanderr193.barrelTrade.event;

import org.alexanderr193.barrelTrade.BarrelHolder;
import org.alexanderr193.barrelTrade.barrel.Barrel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;


public class InventoryCloseListener implements Listener {
    private final BarrelAddedListener barrelAddedListener;

    public InventoryCloseListener(BarrelAddedListener barrelAddedListener) {
        this.barrelAddedListener = barrelAddedListener;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof BarrelHolder barrelHolder) {
            Barrel barrel = barrelHolder.getBarrel();
            if (barrelAddedListener.getPlayerByBarrel(barrel).isPresent()) {
                barrelAddedListener.removeBarrel(barrel);
            }
        }
    }
}
