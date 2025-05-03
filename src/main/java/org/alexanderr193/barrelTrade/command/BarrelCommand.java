package org.alexanderr193.barrelTrade.command;

import org.alexanderr193.barrelTrade.barrel.Barrel;
import org.alexanderr193.barrelTrade.barrel.Currency;
import org.alexanderr193.barrelTrade.barrel.Slot;
import org.alexanderr193.barrelTrade.database.BarrelRepository;
import org.alexanderr193.barrelTrade.event.BarrelAddedListener;
import org.alexanderr193.barrelTrade.utils.Serialization;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class BarrelCommand implements CommandExecutor, TabCompleter {
    private final BarrelRepository barrelRepository;
    private final BarrelAddedListener barrelAddedListener;

    public BarrelCommand(BarrelRepository barrelRepository, BarrelAddedListener barrelAddedListener) {
        this.barrelRepository = barrelRepository;
        this.barrelAddedListener = barrelAddedListener;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("This command is for the player only");
            return true;
        }

        if (strings.length < 3) {
            player.sendMessage("There are too few arguments for the \"\\load\" command");
            player.sendMessage("/load <slot_id in range [1; 27]> <emerald/iron/coal/netherite/diamons> <amount_price in range [1, 64]>");
            return true;
        }

        Block block = player.getTargetBlock(null, 15);
        if (block.getType() != Material.BARREL) {
            player.sendMessage("You must be looking at a barrel");
            return true;
        }

        try {
            Optional<Barrel> barrelOptional = barrelRepository.findBarrel(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
            if (barrelOptional.isEmpty()) {
                player.sendMessage(ChatColor.RED + "This barrel is not registered as a store");
                return true;
            }
            Barrel barrel = barrelOptional.get();

            if (!barrel.getOwner().equals(player.getName())) {
                player.sendMessage(ChatColor.RED + "You don't own this store");
                return true;
            }

            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.sendMessage( ChatColor.RED + "Well sorry, but you can't cell air");
                return true;
            }

            int slotId = -1;
            try {
                slotId = Integer.parseInt(strings[0]) - 1;
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Wrong slotId format");
                return true;
            }
            if (!((slotId+1) >= 1 && (slotId+1) <= 27)) {
                player.sendMessage(ChatColor.RED + "slotId must be in [1; 27] range");
                return true;
            }

            Currency barterType = null;
            try {
                barterType = Currency.valueOf(strings[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + "Only EMERALD, DIAMOND, IRON, COAL, NETHERITE");
                return true;
            }

            int amountPrice = -1;
            try {
                amountPrice = Integer.parseInt(strings[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Wrong amountPrice format");
                return true;
            }
            if (!(amountPrice >= 1 && amountPrice <= 64)) {
                player.sendMessage(ChatColor.RED + "amount_price must be in [1; 64] range");
                return true;
            }

            if (barrelAddedListener.getPlayerByBarrel(barrel).isPresent()) {
                Player occupyingPlayer = barrelAddedListener.getPlayerByBarrel(barrel).get();
                player.sendMessage(ChatColor.RED + "Store in use by " +
                        ChatColor.YELLOW + occupyingPlayer.getName() +
                        ChatColor.RED + ". Please wait");
                return true;
            }


            if (barrel.getSlotBySlotId(slotId).isPresent()) {
                player.sendMessage(ChatColor.RED + "Cannot place item: Slot " +
                        ChatColor.WHITE + slotId + ChatColor.RED + " contains another item");
                return true;
            }

            barrelRepository.updateSlotInBarrel(barrel, new Slot(slotId, amountPrice, barterType, Serialization.itemStackToBase64(player.getInventory().getItemInMainHand())));

            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            player.updateInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_BARREL_OPEN, 2, 2);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0 || strings.length == 1) return List.of("slotid");
        if (strings.length == 2) return List.of("EMERALD", "DIAMOND", "IRON", "COAL", "NETHERITE");
        if (strings.length == 3) return List.of("amount");
        return List.of();
    }
}
