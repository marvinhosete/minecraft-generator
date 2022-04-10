package me.tuskdev.generator.util;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.tuskdev.generator.GeneratorPlugin;
import me.tuskdev.generator.config.data.SimpleGenerator;
import me.tuskdev.generator.model.Generator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class GeneratorUtil {

    private static String HOLOGRAM_TITLE;

    public static void setHologramTitle(String title) {
        if (HOLOGRAM_TITLE == null) HOLOGRAM_TITLE = title;
    }

    public static void hologram(Generator generator, SimpleGenerator simpleGenerator) {
        Hologram hologram = HologramsAPI.createHologram(JavaPlugin.getPlugin(GeneratorPlugin.class), generator.getCoordinates().build().add(0.5, 2.4, 0.5));
        hologram.appendTextLine(HOLOGRAM_TITLE.replace("{type}", simpleGenerator.getType().toUpperCase()).replace("{amount}", "" + generator.getAmount()));
        hologram.appendTextLine("");
        hologram.appendTextLine(String.format("§f§lTier: §b§l%s", generator.getLevel()));
        hologram.appendTextLine(String.format("§f§lNext Generation: §d§l%s", TimeUtil.format(TimeUnit.MINUTES.toMillis(simpleGenerator.getItemsDelay()))));

        generator.setHologram(hologram);
    }

    public static void buildStruct(Location location) {
        Block block = location.getBlock();
        block.setType(Material.OBSIDIAN);

        for (int i = 0; i < 3; i++) {
            for (int x = -1; x < 2; x++) {
                for (int z = -1; z < 2; z++) {
                    Block blockAt = block.getRelative(x, -1, z);
                    if ((x == 0 || z == 0) && !(x == 0 && z == 0)) stair(blockAt, x, z);
                    else blockAt.setType(Material.SEA_LANTERN);
                }
            }
        }
    }

    static void stair(Block block, int x, int z) {
        block.setType(Material.QUARTZ_STAIRS);
        if (x == 0 && z == 1) return;

        byte face = (byte) (z == 0 ? (x == 1 ? 0x1 : 0x0) : 0x2);
        block.setData(face);
    }

}
