package org.alexanderr193.barrelTrade.data.model;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class TradeInventoryHolder implements InventoryHolder {
    private Barrel barrel;

    public TradeInventoryHolder(Barrel barrel) {
        this.barrel = barrel;
    }

    public Barrel getBarrel() {
        return barrel;
    }

    public void setBarrel(Barrel barrel) {
        this.barrel = barrel;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
