package org.alexanderr193.barrelTrade.commands;

import org.alexanderr193.barrelTrade.data.model.Barrel;
import org.alexanderr193.barrelTrade.data.model.enums.Currency;
import org.alexanderr193.barrelTrade.data.model.Slot;
import org.alexanderr193.barrelTrade.data.repository.BarrelRepository;
import org.alexanderr193.barrelTrade.listeners.TradeListener;
import org.alexanderr193.barrelTrade.data.serialization.ItemSerializer;
import org.bukkit.Bukkit;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BarrelCommand implements CommandExecutor, TabCompleter {
    private final BarrelRepository barrelRepository;
    private final TradeListener tradeListener;

    public BarrelCommand(BarrelRepository barrelRepository, TradeListener tradeListener) {
        this.barrelRepository = barrelRepository;
        this.tradeListener = tradeListener;
    }

    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.RED + "Usage: /load <slot> <currency> <price>");
        player.sendMessage(ChatColor.GRAY + "Currencies: " + ChatColor.WHITE + String.join(ChatColor.GRAY + ", " + ChatColor.WHITE, Currency.names()));
        player.sendMessage(ChatColor.GRAY + "Slot range: " + ChatColor.WHITE + "1-27");
        player.sendMessage(ChatColor.GRAY + "Price range: "+ ChatColor.WHITE + "1-64");
    }

    private Optional<Barrel> validateBarrel(Player player, Block block) throws Exception {
        if (block.getType() != Material.BARREL) {
            player.sendMessage("You must be looking at a barrel");
            return Optional.empty();
        }

        Optional<Barrel> barrelOpt = barrelRepository.findBarrel(
                block.getX(),
                block.getY(),
                block.getZ(),
                block.getWorld().getName()
        );

        if (barrelOpt.isEmpty()) {
            player.sendMessage(ChatColor.RED + "This barrel is not registered as a store");
            return Optional.empty();
        }
        Barrel barrel = barrelOpt.get();

        if (!barrel.getOwner().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "You don't own this store");
            return Optional.empty();
        }

        if (tradeListener.getPlayerByBarrel(barrel).isPresent()) {
            Player occupyingPlayer = tradeListener.getPlayerByBarrel(barrel).get();
            player.sendMessage(ChatColor.RED + "Store in use by " +
                    ChatColor.YELLOW + occupyingPlayer.getName() +
                    ChatColor.RED + ". Please wait");
            return Optional.empty();
        }

        return Optional.of(barrel);
    }

    private Optional<Integer> parseSlotId(Player player, String arg) {
        try {
            int slotId = Integer.parseInt(arg) - 1;
            if (slotId < 0 || slotId >= 27) {
                player.sendMessage(ChatColor.RED + "slotId must be in the range [1; 27]");
                return Optional.empty();
            }
            return Optional.of(slotId);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Wrong slotId format");
            return Optional.empty();
        }
    }

    private Optional<Currency> parseCurrency(Player player, String arg) {
        try {
            return Optional.of(Currency.valueOf(arg.toUpperCase()));
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Only: " + String.join(", ", Currency.names()));
            return Optional.empty();
        }
    }

    private Optional<Integer> parsePrice(Player player, String arg) {
        try {
            int price = Integer.parseInt(arg);
            if (price < 1 || price > 64) {
                player.sendMessage(ChatColor.RED + "amount_price must be in the range [1; 64]");
                return Optional.empty();
            }
            return Optional.of(price);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Wrong slotId format");
            return Optional.empty();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is for the player only");
            return true;
        }

        if (args.length != 3) {
            sendUsage(player);
            return true;
        }

        try {
            Block targetBlock = player.getTargetBlock(null, 15);
            Optional<Barrel> barrelOpt = validateBarrel(player, targetBlock);
            if (barrelOpt.isEmpty()) return true;
            Barrel barrel = barrelOpt.get();

            Optional<Integer> slotId = parseSlotId(player, args[0]);
            Optional<Currency> currency = parseCurrency(player, args[1]);
            Optional<Integer> price = parsePrice(player, args[2]);

            if (slotId.isEmpty() || currency.isEmpty() || price.isEmpty()) {
                return true;
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                player.sendMessage( ChatColor.RED + "Well sorry, but you can't cell air");
                return true;
            }

            if (barrel.getSlotBySlotId(slotId.get()).isPresent()) {
                player.sendMessage(ChatColor.RED + "Cannot place item: Slot " +
                        ChatColor.WHITE + slotId.get() + ChatColor.RED + " contains another item");
                return true;
            }

            barrelRepository.updateSlotInBarrel(
                    barrel,
                    new Slot(
                            slotId.get(),
                            price.get(),
                            currency.get(),
                            ItemSerializer.itemStackToBase64(item)
                    )
            );

            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            player.playSound(player.getLocation(), Sound.BLOCK_BARREL_OPEN, 1, 1);
            player.sendMessage(ChatColor.GREEN + "The product has been added. Great");

            // player.updateInventory();

        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Something went wrong, lol");
            Bukkit.getLogger().log(Level.SEVERE, "Error when processing the command: " + e);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return List.of();

        if (args.length == 1) return IntStream.rangeClosed(1, 27)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());

        if (args.length == 2) return Arrays.stream(Currency.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        if (args.length == 3) return List.of("1", "10", "64");

        return List.of();
    }
}
