package me.tuskdev.generator.config;

import me.tuskdev.generator.config.data.SimpleGenerator;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class GeneratorManager {

    private final Map<String, SimpleGenerator> MAP = new HashMap<>();

    public GeneratorManager(ConfigurationSection configurationSection) {
        configurationSection.getKeys(false).forEach(key -> MAP.put(key.toUpperCase(), new SimpleGenerator(configurationSection.getConfigurationSection(key))));
    }

    public SimpleGenerator getGenerator(String id) {
        return MAP.get(id.toUpperCase());
    }

}
