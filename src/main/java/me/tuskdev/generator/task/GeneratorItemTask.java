package me.tuskdev.generator.task;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import me.tuskdev.generator.cache.GeneratorCache;
import me.tuskdev.generator.config.GeneratorManager;
import me.tuskdev.generator.config.data.SimpleGenerator;
import me.tuskdev.generator.controller.GeneratorController;
import me.tuskdev.generator.model.Generator;
import me.tuskdev.generator.util.GeneratorUtil;
import me.tuskdev.generator.util.TimeUtil;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GeneratorItemTask implements Runnable {

    private final GeneratorCache generatorCache;
    private final GeneratorController generatorController;
    private final GeneratorManager generatorManager;

    public GeneratorItemTask(GeneratorCache generatorCache, GeneratorController generatorController, GeneratorManager generatorManager) {
        this.generatorCache = generatorCache;
        this.generatorController = generatorController;
        this.generatorManager = generatorManager;
    }

    @Override
    public void run() {
        Set<Generator> generators = generatorCache.getAll();
        for (Generator generator : generators) {
            SimpleGenerator simpleGenerator = generatorManager.getGenerator(generator.getType());
            long time = generator.getTime();
            Hologram hologram = generator.getHologram();
            if (hologram == null) {
                GeneratorUtil.hologram(generator, simpleGenerator);
                hologram = generator.getHologram();
            }

            if (time > System.currentTimeMillis()) {
                hologram.removeLine(3);
                hologram.appendTextLine(String.format("§f§lNext Generation: §d§l%s", TimeUtil.format(time-System.currentTimeMillis())));
                continue;
            }

            generator.setItems(generator.getItems() + (simpleGenerator.getItemsAmount() * generator.getLevel()));
            generator.setTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(simpleGenerator.getItemsDelay()));
        }
    }
}
