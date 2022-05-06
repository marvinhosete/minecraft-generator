package me.tuskdev.generator.hologram;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

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

    public static void removeHolograms(Chunk chunk) {
        Iterator<Hologram> iterator = HOLOGRAMS.iterator();
        while (iterator.hasNext()) {
            Hologram hologram = iterator.next();
            if (hologram.getInitLocation().getChunk().equals(chunk)) {
                hologram.delete();
                iterator.remove();
            }
        }
    }

    public static boolean isHologram(Entity entity) {
        return HOLOGRAMS.stream().anyMatch(hologram -> hologram.getLines().stream().anyMatch(line -> line.getEntity().getArmorStand().equals(entity)));
    }

    public static void clear() {
        HOLOGRAMS.forEach(Hologram::delete);
        HOLOGRAMS.clear();
    }
}
