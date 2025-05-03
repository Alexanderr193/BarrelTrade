package org.alexanderr193.barrelTrade.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.alexanderr193.barrelTrade.barrel.Barrel;
import org.alexanderr193.barrelTrade.barrel.Slot;
import org.bukkit.Location;

import java.io.*;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BarrelRepository {
    private final Path dataFile;
    private final Gson gson;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public BarrelRepository(Path dataFolder) throws Exception {
        this.dataFile = dataFolder.resolve("barrels.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        initDataFile();
    }

    private void initDataFile() throws IOException {
        if (!Files.exists(dataFile)) {
            try {
                Files.createDirectories(dataFile.getParent());
                saveBarrels(Collections.emptyList());
            } catch (IOException e) {
                throw new IOException("Failed to init barrels file", e);
            }
        }
    }

    public List<Barrel> getAllBarrels() throws Exception {
        lock.readLock().lock();
        try (Reader reader = Files.newBufferedReader(dataFile)) {
            Type type = new TypeToken<List<Barrel>>() {}.getType();
            List<Barrel> barrels = gson.fromJson(reader, type);
            return barrels != null ? barrels : Collections.emptyList();
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException("Invalid JSON data", e);
        } catch (IOException e) {
            throw new IOException("Failed to read barrels", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void saveBarrels(List<Barrel> barrels) throws IOException {
        lock.writeLock().lock();
        try (Writer writer = Files.newBufferedWriter(dataFile)) {
            gson.toJson(barrels, writer);
        } catch (IOException e) {
            throw new IOException("Failed to save barrels", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Barrel> findBarrel(int x,  int y, int z, String worldName) throws Exception {
        return getAllBarrels().stream()
                .filter(b -> b.equals(x, y, z, worldName))
                .findFirst();
    }

    public void addBarrel(Barrel barrel) throws Exception {
        List<Barrel> barrels = new ArrayList<>(getAllBarrels());
        barrels.add(barrel);
        saveBarrels(barrels);
    }

    public void removeBarrel(Barrel barrel) throws Exception {
        List<Barrel> barrels = new ArrayList<>(getAllBarrels());
        barrels.removeIf(b -> b.equals(barrel.getX(), barrel.getY(),barrel.getZ(), barrel.getWorld()));
        saveBarrels(barrels);
    }

    public void updateSlotInBarrel(Barrel barrel, Slot slot) throws Exception {
        List<Barrel> barrels = new ArrayList<>(getAllBarrels());
        for (Barrel iterBarrel : barrels) {
            if (iterBarrel.equals(barrel)) {
                iterBarrel.setSlot(slot);
                break;
            }
        }
        saveBarrels(barrels);
    }

    public void removeSlotInBarrel(Barrel barrel, int slotId) throws Exception {
        List<Barrel> barrels = new ArrayList<>(getAllBarrels());
        for (Barrel iterBarrel : barrels) {
            if (iterBarrel.equals(barrel)) {
                iterBarrel.getSlots().removeIf(slot -> slot.getSlotId() == slotId);
                break;
            }
        }
        saveBarrels(barrels);
    }
}