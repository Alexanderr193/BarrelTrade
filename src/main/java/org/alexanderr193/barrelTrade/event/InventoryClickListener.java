package org.alexanderr193.barrelTrade.event;

import org.alexanderr193.barrelTrade.BarrelHolder;
import org.alexanderr193.barrelTrade.barrel.Barrel;
import org.alexanderr193.barrelTrade.barrel.Slot;
import org.alexanderr193.barrelTrade.database.BarrelRepository;
import org.alexanderr193.barrelTrade.utils.Serialization;
import org.alexanderr193.barrelTrade.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;


public class InventoryClickListener implements Listener {
    private final BarrelRepository barrelRepository;

    public InventoryClickListener(BarrelRepository barrelRepository) {
        this.barrelRepository = barrelRepository;
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) throws Exception {
        if (event.getInventory().getHolder() instanceof BarrelHolder) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            Inventory clickedInventory = event.getClickedInventory();

            // Determine which part was clicked
            if (clickedInventory == null) return;
            if (event.getClick().isShiftClick()) {
                event.setCancelled(true);
                return;
            }

            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                return;
            }

            if (clickedInventory.getHolder() instanceof BarrelHolder barrelHolder) {
                // Clicked in the BarrelHolder GUI
                Barrel barrel = barrelHolder.getBarrel();
                int slotId = event.getSlot();
                Optional<Slot> slotOptional = barrel.getSlotBySlotId(slotId);
                if (slotOptional.isEmpty()) {
                    // Air

                    return;
                } else {
                    // Buy
                    if (barrel.getOwner().equals(player.getName())) {
                        // Current player is the owner of the barrel
                        barrel.removeSlot(slotOptional.get());
                        barrelRepository.removeSlotInBarrel(barrel, slotId);

                        Util.updateInventoryWithBarrel(barrel, player);
                        BarrelAddedEvent.callEvent(barrel, player);

                        player.getInventory().addItem(new ItemStack(Serialization.itemStackFromBase64(slotOptional.get().getBase64Product())));
                        player.updateInventory();
                        return;
                    }

                    if (Util.takeItemsFromPlayer(player, slotOptional.get().getCurrency(), slotOptional.get().getAmount())) {
                        barrel.removeSlot(slotOptional.get());
                        barrelRepository.removeSlotInBarrel(barrel, slotId);

                        Util.updateInventoryWithBarrel(barrel, player);
                        BarrelAddedEvent.callEvent(barrel, player);

                        player.getInventory().addItem(new ItemStack(Serialization.itemStackFromBase64(slotOptional.get().getBase64Product())));
                        player.updateInventory();
                        player.sendMessage(ChatColor.GREEN + "Purchase successful!");
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "You need " + slotOptional.get().getAmount() + " " +
                                slotOptional.get().getCurrency().toString().toLowerCase() + "s to buy this!");
                    }

                    return;
                }

            } else if (clickedInventory.getType() == InventoryType.PLAYER) {
                // Clicked in player's personal inventory
                event.setCancelled(false);
            }
        }
    }
}
