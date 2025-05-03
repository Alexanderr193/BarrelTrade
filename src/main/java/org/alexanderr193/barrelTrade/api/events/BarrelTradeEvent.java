package org.alexanderr193.barrelTrade.api.events;

import org.alexanderr193.barrelTrade.data.model.Barrel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BarrelTradeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Barrel barrel;
    private final Player player;

    public BarrelTradeEvent(Barrel barrel, Player player) {
        this.barrel = barrel;
        this.player = player;
    }

    public Barrel getBarrel() {
        return barrel;
    }

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }


    public static void callEvent(Barrel barrel, Player player) {
        Bukkit.getPluginManager().callEvent(new BarrelTradeEvent(barrel, player));
    }

}
