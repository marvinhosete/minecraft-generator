package me.tuskdev.generator.listener;

import me.tuskdev.generator.cache.GeneratorCache;
import me.tuskdev.generator.model.Generator;
import me.tuskdev.generator.util.Coordinates;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final GeneratorCache generatorCache;

    public BlockBreakListener(GeneratorCache generatorCache) {
        this.generatorCache = generatorCache;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Generator generator = generatorCache.select(Coordinates.of(event.getBlock().getLocation()));

        if (generator != null) {
            event.setCancelled(true);
            return;
        }

        if (event.getBlock().getType() != Material.SEA_LANTERN && event.getBlock().getType() != Material.QUARTZ_STAIRS) return;

        Block block = event.getBlock().getRelative(BlockFace.UP);
        if (block != null && block.getType() == Material.OBSIDIAN) {
            event.setCancelled(generatorCache.select(Coordinates.of(block.getLocation())) != null);
            return;
        }

        Generator target = generatorCache.selectIf(coordinates -> coordinates.build().distance(event.getBlock().getLocation().add(0, 1, 0)) <= 2);
        if (target != null) event.setCancelled(true);
    }

}
