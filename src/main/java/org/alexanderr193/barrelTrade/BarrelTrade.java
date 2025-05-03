package org.alexanderr193.barrelTrade;

import org.alexanderr193.barrelTrade.command.BarrelCommand;
import org.alexanderr193.barrelTrade.database.BarrelRepository;

import org.alexanderr193.barrelTrade.event.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;

public final class BarrelTrade extends JavaPlugin {
    private BarrelRepository barrelRepository;
    private BarrelAddedListener barrelAddedListener;

    private void registerListeners() {
        Arrays.asList(
                new BlockInteractListener(barrelRepository, barrelAddedListener),
                new InventoryClickListener(barrelRepository),
                new InventoryDragListener(),
                new InventoryMoveItemEventListener(),
                new InventoryCloseListener(barrelAddedListener),
                new EntityExplodeListener(barrelRepository, barrelAddedListener),
                new BlockBreakListener(barrelRepository, barrelAddedListener),
                new BlockPistonExtendListener(barrelRepository, barrelAddedListener),
                this.barrelAddedListener
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onEnable() {
        try {
            barrelRepository = new BarrelRepository(Path.of(getDataFolder().getPath()));
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to init BarrelDB", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
        barrelAddedListener = new BarrelAddedListener();


        getCommand("load").setExecutor(new BarrelCommand(barrelRepository, barrelAddedListener));
        getCommand("load").setTabCompleter(new BarrelCommand(barrelRepository, barrelAddedListener));

        registerListeners();
    }



    @Override
    public void onDisable() {

    }
}
