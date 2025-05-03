package org.alexanderr193.barrelTrade.listeners;

import org.alexanderr193.barrelTrade.api.events.BarrelTradeEvent;
import org.alexanderr193.barrelTrade.data.model.Barrel;
import org.alexanderr193.barrelTrade.data.repository.BarrelRepository;
import org.alexanderr193.barrelTrade.view.gui.BarrelInventoryView;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class BlockInteractListener implements Listener {
    private final BarrelRepository barrelRepository;
    private final TradeListener tradeListener;
    private static final String SHOP_ITEM_NAME = "shop";

    public BlockInteractListener(BarrelRepository barrelRepository, TradeListener tradeListener) {
        this.barrelRepository = Objects.requireNonNull(barrelRepository, "barrelRepository cannot be null");
        this.tradeListener = tradeListener;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) throws Exception {
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.BARREL) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Optional<Barrel> barrelOptional = barrelRepository.findBarrel(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());

            if (event.getItem() != null && event.getItem().getType() == Material.PAPER) {
                if (event.getItem().getItemMeta() != null && event.getItem().getItemMeta().getDisplayName().equals(SHOP_ITEM_NAME)) {
                    if (barrelOptional.isPresent()) {
                        event.getPlayer().sendMessage("This barrel is already a store. Owner: " + ChatColor.GREEN + barrelOptional.get().getOwner());
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                    } else {
                        if (event.getItem().getItemMeta()!= null && !event.getItem().getItemMeta().getDisplayName().equals(SHOP_ITEM_NAME)) return;
                        barrelRepository.addBarrel(new Barrel(
                                block.getX(),
                                block.getY(),
                                block.getZ(),
                                block.getWorld().getName(),
                                event.getPlayer().getName(),
                                new ArrayList<>()
                        ));
                        event.getPlayer().sendMessage("This barrel is your store now!");
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                    }
                    event.setCancelled(true);
                    return;
                }
            }
            if (barrelOptional.isPresent()) {
                Barrel barrel = barrelOptional.get();
                if (tradeListener.getPlayerByBarrel(barrel).isEmpty()) {
                    BarrelInventoryView.openBarrelShop(barrel, event.getPlayer());
                    BarrelTradeEvent.callEvent(barrel, event.getPlayer());
                } else {
                    event.getPlayer().sendMessage("The " + ChatColor.YELLOW + tradeListener.getPlayerByBarrel(barrel).get().getName() + ChatColor.RESET + " player is currently using the store");
                }

                event.setCancelled(true);
            }
        }


    }

}
