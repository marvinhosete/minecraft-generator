package me.tuskdev.generator.hologram;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;

public class HologramLibrary {

    private static final Set<Hologram> HOLOGRAMS = new HashSet<>();

    public static Hologram createHologram(Location location, String... lines) {
        Hologram hologram = new Hologram(location, lines);
        HOLOGRAMS.add(hologram);
        return hologram;
    }

    public static void removeHologram(Hologram hologram) {
        HOLOGRAMS.remove(hologram);
        hologram.delete();
    }

    public static void clear() {
        HOLOGRAMS.forEach(Hologram::delete);
        HOLOGRAMS.clear();
    }
}
