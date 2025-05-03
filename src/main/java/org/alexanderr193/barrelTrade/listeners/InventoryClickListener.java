package org.alexanderr193.barrelTrade.listeners;

import org.alexanderr193.barrelTrade.api.events.BarrelTradeEvent;
import org.alexanderr193.barrelTrade.data.model.Barrel;
import org.alexanderr193.barrelTrade.data.model.Slot;
import org.alexanderr193.barrelTrade.data.model.TradeInventoryHolder;
import org.alexanderr193.barrelTrade.data.repository.BarrelRepository;
import org.alexanderr193.barrelTrade.data.serialization.ItemSerializer;
import org.alexanderr193.barrelTrade.economy.PlayerEconomyManager;
import org.alexanderr193.barrelTrade.view.gui.BarrelInventoryView;
import org.bukkit.Bukkit;
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
import java.util.logging.Level;


public class InventoryClickListener implements Listener {
    private final BarrelRepository barrelRepository;

    public InventoryClickListener(BarrelRepository barrelRepository) {
        this.barrelRepository = barrelRepository;
    }

    private void handleOwnerClick(Player player, Barrel barrel, Slot slot) throws Exception {
        ItemStack item = ItemSerializer.itemStackFromBase64(slot.getBase64Product());
        player.getInventory().addItem(item);

        barrel.removeSlot(slot);
        barrelRepository.removeSlotInBarrel(barrel, slot.getSlotId());

        BarrelInventoryView.openBarrelShop(barrel, player);
        BarrelTradeEvent.callEvent(barrel, player);

        player.sendMessage(ChatColor.GREEN + "The product has been turned into your inventory");
    }

    private void handleBuyerClick(Player player, Barrel barrel, Slot slot) throws Exception {
        if (PlayerEconomyManager.withdrawCurrencyFromPlayer(player, slot.getCurrency(), slot.getAmount())) {
            barrel.removeSlot(slot);
            barrelRepository.removeSlotInBarrel(barrel, slot.getSlotId());

            BarrelInventoryView.openBarrelShop(barrel, player);
            BarrelTradeEvent.callEvent(barrel, player);

            player.getInventory().addItem(ItemSerializer.itemStackFromBase64(slot.getBase64Product()));
            player.updateInventory();
            player.sendMessage(ChatColor.GREEN + "Purchase successful!");
        }
        else {
            player.sendMessage(ChatColor.RED + "You need " + slot.getAmount() + " " +
                    slot.getCurrency().toString().toLowerCase() + "s to buy this!");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof TradeInventoryHolder holder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getClick().isShiftClick() || event.getClickedInventory() == null) {
            return;
        }

        Barrel barrel = holder.getBarrel();
        int slotId = event.getSlot();

        Optional<Slot> slotOpt = barrel.getSlotBySlotId(slotId);
        if (slotOpt.isEmpty()) {
            return;
        }

        Slot slot = slotOpt.get();

        if (barrel.getOwner().equals(player.getName())) {
            try {
                handleOwnerClick(player, barrel, slot);
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Something went wrong, lol");
                Bukkit.getLogger().log(Level.SEVERE, "Error handleOwnerClick(): " + e);
            }

            return;
        }

        try {
            handleBuyerClick(player, barrel, slot);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Something went wrong, lol");
            Bukkit.getLogger().log(Level.SEVERE, "Error handleBuyerClick(): " + e);
        }
    }


    public void onInventoryClickEvent(InventoryClickEvent event) throws Exception {
        if (event.getInventory().getHolder() instanceof TradeInventoryHolder) {
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

            if (clickedInventory.getHolder() instanceof TradeInventoryHolder tradeInventoryHolder) {
                // Clicked in the BarrelHolder GUI
                Barrel barrel = tradeInventoryHolder.getBarrel();
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

                        BarrelInventoryView.openBarrelShop(barrel, player);
                        BarrelTradeEvent.callEvent(barrel, player);

                        player.getInventory().addItem(ItemSerializer.itemStackFromBase64(slotOptional.get().getBase64Product()));
                        player.updateInventory();
                        return;
                    }

                    if (PlayerEconomyManager.withdrawCurrencyFromPlayer(player, slotOptional.get().getCurrency(), slotOptional.get().getAmount())) {
                        barrel.removeSlot(slotOptional.get());
                        barrelRepository.removeSlotInBarrel(barrel, slotId);

                        BarrelInventoryView.openBarrelShop(barrel, player);
                        BarrelTradeEvent.callEvent(barrel, player);

                        player.getInventory().addItem(ItemSerializer.itemStackFromBase64(slotOptional.get().getBase64Product()));
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
