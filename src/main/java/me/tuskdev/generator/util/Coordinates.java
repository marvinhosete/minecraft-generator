package me.tuskdev.generator.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

public class Coordinates {

    private final String worldName;
    private final int x, y, z;

    public Coordinates(String worldName, int x, int y, int z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorldName() {
        return worldName;
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

    public Location build() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public static Coordinates of(Location location) {
        return new Coordinates(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /* Override */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y && z == that.z && Objects.equals(worldName, that.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, y, z);
    }
}
