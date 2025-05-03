package org.alexanderr193.barrelTrade.listeners;

import org.alexanderr193.barrelTrade.api.events.BarrelTradeEvent;
import org.alexanderr193.barrelTrade.data.model.Barrel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TradeListener implements Listener {
    private final Map<Barrel, Player> activeBarrels = new ConcurrentHashMap<>();

    @EventHandler
    public void onBarrelAdded(BarrelTradeEvent event) {
        this.addBarrel(event.getBarrel(), event.getPlayer());
    }

    public void addBarrel(Barrel barrel, Player player) {
        activeBarrels.put(barrel, player);
    }

    public void removeBarrel(Barrel barrel) {
        activeBarrels.remove(barrel);
    }

    public Optional<Player> getPlayerByBarrel(Barrel barrel) {
        return Optional.ofNullable(activeBarrels.get(barrel));
    }

}
