package org.alexanderr193.barrelTrade.barrel;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Barrel {
    private final int x, y, z;
    private final String world;
    private final String owner;
    private final List<Slot> slots;

    public Barrel(int x, int y, int z, String world, String owner, List<Slot> slots) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.owner = owner;
        this.slots = slots;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public String getOwner() { return owner; }
    public List<Slot> getSlots() { return slots; }

    public void addSlot(Slot slot) {
        slots.add(slot);
    }

    public void removeSlot(Slot slot) {
        slots.remove(slot);
    }

    public void setSlot(Slot slot) {
        this.slots.removeIf(s -> s.slotId == slot.slotId);
        this.slots.add(slot);
    }

    public Optional<Slot> getSlotBySlotId(int sloId) {
        return getSlots().stream().filter(s -> s.slotId == sloId).findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Barrel barrel = (Barrel) o;
        return x == barrel.x && y == barrel.y && z == barrel.z && Objects.equals(world, barrel.world);
    }

    public boolean equals(int x, int y, int z, String worldName) {
        return x == this.x && y == this.y && z == this.z && worldName.equals(this.world);
    }

    @Override
    public int hashCode() {
        return  31 * (31 * (31 * x + y) + z) + world.hashCode();
    }
}