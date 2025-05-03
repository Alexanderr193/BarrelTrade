package org.alexanderr193.barrelTrade.event;

import org.alexanderr193.barrelTrade.barrel.Barrel;
import org.alexanderr193.barrelTrade.database.BarrelRepository;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.Optional;


public class BlockPistonExtendListener implements Listener {
    private final BarrelRepository barrelRepository;
    private final BarrelAddedListener barrelAddedListener;

    public BlockPistonExtendListener(BarrelRepository barrelRepository, BarrelAddedListener barrelAddedListener) {
        this.barrelRepository = barrelRepository;
        this.barrelAddedListener = barrelAddedListener;
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) throws Exception {
        for (Block block : event.getBlocks()) {
            if (block.getType() == Material.BARREL && barrelRepository.findBarrel(
                    block.getX(),
                    block.getY(),
                    block.getZ(),
                    block.getWorld().getName()
            ).isPresent()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) throws Exception {
        if (!event.isSticky()) return;

        // Because Spigot devs love breaking s**t
        BlockFace direction = event.getDirection();
        Block retractBlock = event.getBlock().getRelative(direction.getOppositeFace());

        if (retractBlock.getType() == Material.BARREL) {
            Optional<Barrel> barrelOptional = barrelRepository.findBarrel(
                    retractBlock.getX(),
                    retractBlock.getY(),
                    retractBlock.getZ(),
                    retractBlock.getWorld().getName()
            );
            if (barrelOptional.isEmpty()) return;
            Barrel barrel = barrelOptional.get();
            event.setCancelled(true);

            barrelAddedListener.removeBarrel(barrel);

            event.getBlock().getWorld().playSound(
                    event.getBlock().getLocation(),
                    Sound.ENTITY_ITEM_BREAK,
                    1f,
                    0.5f
            );
        }
    }

}
