package org.alexanderr193.barrelTrade;

import org.alexanderr193.barrelTrade.commands.BarrelCommand;
import org.alexanderr193.barrelTrade.data.repository.BarrelRepository;
import org.alexanderr193.barrelTrade.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.logging.Level;

public final class BarrelTrade extends JavaPlugin {
    private BarrelRepository barrelRepository;
    private TradeListener tradeListener;

    private void registerListeners() {
        Arrays.asList(
                new BlockInteractListener(barrelRepository, tradeListener),
                new InventoryClickListener(barrelRepository),
                new InventoryDragListener(),
                new InventoryMoveItemEventListener(),
                new InventoryCloseListener(tradeListener),
                new EntityExplodeListener(barrelRepository, tradeListener),
                new BlockBreakListener(barrelRepository, tradeListener),
                new BlockPistonExtendListener(barrelRepository, tradeListener),
                this.tradeListener
        ).forEach(listener -> {
            try {
                getServer().getPluginManager().registerEvents(listener, this);
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Failed to register listener: " + listener.getClass().getSimpleName(), e);
            }
        });
    }

    @Override
    public void onEnable() {
        try {
            barrelRepository = new BarrelRepository(Path.of(getDataFolder().getPath()));
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to init barrelRepository", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
        tradeListener = new TradeListener();


        getCommand("load").setExecutor(new BarrelCommand(barrelRepository, tradeListener));
        getCommand("load").setTabCompleter(new BarrelCommand(barrelRepository, tradeListener));

        registerListeners();
    }



    @Override
    public void onDisable() {
        try {
            barrelRepository.close();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to save barrel data", e);

            // Emergency backup
            try {
                Path emergencyFile = getDataFolder().toPath().resolve("barrels_EMERGENCY.json");
                Files.copy(
                        barrelRepository.getDataFilePath(),
                        emergencyFile,
                        StandardCopyOption.REPLACE_EXISTING
                );
                getLogger().warning("Emergency save created in plugin folder");
            } catch (IOException ex) {
                getLogger().log(Level.SEVERE, "CRITICAL: Failed to create emergency backup", ex);
            }
        }
    }
}
