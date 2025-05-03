package org.alexanderr193.barrelTrade.economy;

import org.alexanderr193.barrelTrade.data.model.enums.Currency;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerEconomyManager {
    public static boolean withdrawCurrencyFromPlayer(Player player, Currency currency, int amount) {

        Material material =  switch (currency) {
            case COAL -> material = Material.COAL;
            case EMERALD -> material = Material.EMERALD;
            case DIAMOND -> material = Material.DIAMOND;
            case IRON -> material = Material.IRON_INGOT;
            case NETHERITE -> material = Material.NETHERITE_INGOT;
            default -> throw new IllegalArgumentException("Unknown currency: " + currency);
        };

        if (!player.getInventory().contains(material, amount)) {
            return false;
        }

        int remaining = amount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != material) continue;

            int remove = Math.min(item.getAmount(), remaining);
            item.setAmount(item.getAmount() - remove);
            remaining -= remove;

            if (remaining <= 0) break;
        }

        player.updateInventory();
        return true;
    }
}
