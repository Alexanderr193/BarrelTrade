package org.alexanderr193.barrelTrade.listeners;

import org.alexanderr193.barrelTrade.data.model.Barrel;
import org.alexanderr193.barrelTrade.data.model.Slot;
import org.alexanderr193.barrelTrade.data.repository.BarrelRepository;
import org.alexanderr193.barrelTrade.data.serialization.ItemSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BlockBreakListener implements Listener {
    private final BarrelRepository barrelRepository;
    private final TradeListener tradeListener;

    public BlockBreakListener(BarrelRepository barrelRepository, TradeListener tradeListener) {
        this.barrelRepository = barrelRepository;
        this.tradeListener = tradeListener;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) throws Exception {
        Block block = event.getBlock();

        // Check if broken block is a barrel
        if (block.getType() == Material.BARREL) {
            // Get your barrel data
            Optional<Barrel> barrelOpt = barrelRepository.findBarrel(
                    block.getX(),
                    block.getY(),
                    block.getZ(),
                    block.getWorld().getName()
            );

            if (barrelOpt.isPresent()) {
                Barrel barrel = barrelOpt.get();
                String ownerName = barrel.getOwner();

                Optional<Player> optionalPlayer = tradeListener.getPlayerByBarrel(barrel);
                tradeListener.removeBarrel(barrel);
                barrelRepository.removeBarrel(barrel);

                // Notify owner if online
                Player owner = Bukkit.getPlayer(ownerName);
                if (owner != null) {
                    owner.sendMessage(ChatColor.YELLOW + "Your shop barrel at " +
                            block.getX() + ", " + block.getY() + ", " + block.getZ() +
                            " has been removed");
                    owner.playSound(owner.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 1, 2);
                    optionalPlayer.ifPresent(HumanEntity::closeInventory);
                }

                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.BARREL));
                block.setType(Material.AIR);

                for (Slot slot : barrel.getSlots()) {
                    ItemStack itemStack = ItemSerializer.itemStackFromBase64(slot.getBase64Product());
                    block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
                }

            }
        }
    }
}
