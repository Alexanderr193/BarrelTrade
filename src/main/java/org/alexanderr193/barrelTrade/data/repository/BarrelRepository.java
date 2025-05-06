package org.alexanderr193.barrelTrade.data.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.alexanderr193.barrelTrade.data.model.Barrel;
import org.alexanderr193.barrelTrade.data.model.Slot;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BarrelRepository {
    private final Path dataFile;
    private final Gson gson;
    private final List<Barrel> barrelCache;  // Основной кеш
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public BarrelRepository(Path dataFolder) throws IOException {
        this.dataFile = dataFolder.resolve("barrels.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.initDataFile();
        this.barrelCache = Collections.synchronizedList(new ArrayList<>());
        this.loadBarrelsIntoCache();
    }

    public Path getDataFilePath() {
        return this.dataFile;
    }

    private void initDataFile() throws IOException {
        if (!Files.exists(dataFile)) {
            Files.createDirectories(dataFile.getParent());
            saveBarrels(Collections.emptyList());
        }
    }

    private void loadBarrelsIntoCache() throws IOException {
        lock.writeLock().lock();
        try (Reader reader = Files.newBufferedReader(dataFile)) {
            Type type = new TypeToken<List<Barrel>>() {}.getType();
            List<Barrel> barrels = gson.fromJson(reader, type);
            barrelCache.clear();
            if (barrels != null) {
                barrelCache.addAll(barrels);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void close() throws IOException {
        lock.writeLock().lock();
        try {
            saveBarrelsInternal(barrelCache);

            createBackupInternal();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void saveBarrelsInternal(List<Barrel> barrels) throws IOException {
        try (Writer writer = Files.newBufferedWriter(dataFile)) {
            gson.toJson(barrels, writer);
        }
    }

    private void createBackupInternal() throws IOException {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path backupPath = dataFile.getParent().resolve("barrels_" + timestamp + ".json");
        Files.copy(dataFile, backupPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private void saveBarrels(List<Barrel> barrels) throws IOException {
        lock.writeLock().lock();
        try (Writer writer = Files.newBufferedWriter(dataFile)) {
            gson.toJson(barrels, writer);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Barrel> findBarrel(int x, int y, int z, String worldName) {
        Barrel targetBarrel = new Barrel(x, y, z, worldName, null, null);
        lock.readLock().lock();
        try {
            return barrelCache.stream()
                    .filter(b -> b.equals(targetBarrel))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addBarrel(Barrel barrel) throws IOException {
        lock.writeLock().lock();
        try {
            barrelCache.add(barrel);
            saveBarrels(barrelCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeBarrel(Barrel barrel) throws IOException {
        lock.writeLock().lock();
        try {
            barrelCache.removeIf(b -> b.equals(barrel));
            saveBarrels(barrelCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateSlotInBarrel(Barrel barrel, Slot slot) throws IOException {
        lock.writeLock().lock();
        try {
            barrelCache.stream()
                    .filter(b -> b.equals(barrel))
                    .findFirst()
                    .ifPresent(b -> b.setSlot(slot));
            saveBarrels(barrelCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeSlotInBarrel(Barrel barrel, int slotId) throws IOException {
        lock.writeLock().lock();
        try {
            barrelCache.stream()
                    .filter(b -> b.equals(barrel))
                    .findFirst()
                    .ifPresent(b -> b.getSlots().removeIf(s -> s.getSlotId() == slotId));
            saveBarrels(barrelCache);
        } finally {
            lock.writeLock().unlock();
        }
    }
}