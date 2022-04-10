package me.tuskdev.generator.listener;

import me.tuskdev.generator.cache.GeneratorCache;
import me.tuskdev.generator.controller.GeneratorController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class WorldChunkListener implements Listener {

    private final GeneratorCache generatorCache;
    private final GeneratorController generatorController;

    public WorldChunkListener(GeneratorCache generatorCache, GeneratorController generatorController) {
        this.generatorCache = generatorCache;
        this.generatorController = generatorController;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        generatorController.select(event.getWorld().getName(), event.getChunk().getX(), event.getChunk().getZ()).forEach(generatorCache::insert);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        generatorCache.deleteIfAnd(coordinates -> coordinates.getX() >> 4 == event.getChunk().getX() && coordinates.getZ() >> 4 == event.getChunk().getZ(), generator -> generatorController.updateItems(generator.getItems(), generator.getCoordinates()));
    }

}
