package org.alexanderr193.barrelTrade.listeners;

import org.alexanderr193.barrelTrade.data.model.Barrel;
import org.alexanderr193.barrelTrade.data.model.Slot;
import org.alexanderr193.barrelTrade.data.repository.BarrelRepository;
import org.alexanderr193.barrelTrade.data.serialization.ItemSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class EntityExplodeListener implements Listener {
    private final BarrelRepository barrelRepository;
    private final TradeListener tradeListener;

    public EntityExplodeListener(BarrelRepository barrelRepository, TradeListener tradeListener) {
        this.barrelRepository = barrelRepository;
        this.tradeListener = tradeListener;
    }

    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent event) throws RuntimeException {
        event.blockList().removeIf(block -> {
            if (block.getType() == Material.BARREL) {
                Optional<Barrel> barrelOptional;
                try {
                    barrelOptional = barrelRepository.findBarrel(
                            block.getX(),
                            block.getY(),
                            block.getZ(),
                            block.getWorld().getName()
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (barrelOptional.isPresent()) {
                    Barrel barrel = barrelOptional.get();
                    // Notify owner
                    String ownerName = barrel.getOwner();
                    Player owner = Bukkit.getPlayer(ownerName);
                    if (owner != null) {
                        owner.sendMessage(ChatColor.RED + "Your shop barrel was destroyed by an explosion at " +
                                block.getX() + ", " + block.getY() + ", " + block.getZ());
                        owner.playSound(owner.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
                        Optional<Player> optionalPlayer = tradeListener.getPlayerByBarrel(barrel);
                        optionalPlayer.ifPresent(HumanEntity::closeInventory);
                    }

                    // Remove from storage
                    try {
                        barrelRepository.removeBarrel(barrel);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    tradeListener.removeBarrel(barrel);

                    for (Slot slot : barrel.getSlots()) {
                        ItemStack itemStack = ItemSerializer.itemStackFromBase64(slot.getBase64Product());
                        block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
                    }
                    return false;
                }
            }
            return false;
        });
    }
}
