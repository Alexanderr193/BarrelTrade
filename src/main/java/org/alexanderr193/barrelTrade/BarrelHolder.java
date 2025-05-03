package org.alexanderr193.barrelTrade;

import org.alexanderr193.barrelTrade.barrel.Barrel;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BarrelHolder implements InventoryHolder {
    private Barrel barrel;

    public BarrelHolder(Barrel barrel) {
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
