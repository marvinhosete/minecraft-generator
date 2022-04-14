package me.tuskdev.generator;

import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.tuskdev.generator.cache.GeneratorCache;
import me.tuskdev.generator.command.GeneratorCommand;
import me.tuskdev.generator.config.GeneratorManager;
import me.tuskdev.generator.controller.GeneratorController;
import me.tuskdev.generator.hologram.HologramLibrary;
import me.tuskdev.generator.inventory.ViewFrame;
import me.tuskdev.generator.listener.BlockBreakListener;
import me.tuskdev.generator.listener.BlockPlaceListener;
import me.tuskdev.generator.listener.PlayerInteractListener;
import me.tuskdev.generator.listener.WorldChunkListener;
import me.tuskdev.generator.task.GeneratorItemTask;
import me.tuskdev.generator.util.GeneratorUtil;
import me.tuskdev.generator.util.ItemBuilder;
import me.tuskdev.generator.view.GeneratorInventoryView;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class GeneratorPlugin extends JavaPlugin {

    private GeneratorCache generatorCache;
    private GeneratorController generatorController;
    private ItemStack GENERATOR_ITEM;

    @Override
    public void onLoad() {
        saveDefaultConfig();

        GENERATOR_ITEM = ItemBuilder.load(getConfig().getConfigurationSection("generator-item"));
        GeneratorUtil.setHologramTitle(GENERATOR_ITEM.getItemMeta().getDisplayName());
    }

    @Override
    public void onEnable() {
        generatorCache = new GeneratorCache();
        generatorController = new GeneratorController(getConfig().getConfigurationSection("database"));
        GeneratorManager generatorManager = new GeneratorManager(getConfig().getConfigurationSection("generators"));

        BukkitFrame bukkitFrame = new BukkitFrame(this);
        bukkitFrame.registerCommands(new GeneratorCommand(GENERATOR_ITEM, generatorManager, getConfig().getConfigurationSection("messages.command")));

        ViewFrame viewFrame = new ViewFrame(this);
        viewFrame.register(new GeneratorInventoryView(generatorCache, generatorController, generatorManager, GENERATOR_ITEM, getConfig().getConfigurationSection("messages.inventory"), getConfig().getConfigurationSection("inventory")));

        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(generatorCache), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(generatorCache, generatorController, generatorManager, getConfig().getString("messages.cant-place-generator", "&cAn error occurred while trying to send the message.")), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(generatorCache, viewFrame), this);
        Bukkit.getPluginManager().registerEvents(new WorldChunkListener(generatorCache, generatorController), this);

        Bukkit.getScheduler().runTaskTimer(this, new GeneratorItemTask(generatorCache, generatorManager), 0L, 20L);

        load(generatorCache, generatorController);
    }

    @Override
    public void onDisable() {
        HologramLibrary.clear();
        generatorCache.deleteAnd(generator -> generatorController.updateItems(generator.getItems(), generator.getCoordinates()));
    }

    void load(GeneratorCache generatorCache, GeneratorController generatorController) {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                generatorController.select(chunk.getWorld().getName(), chunk.getX(), chunk.getZ()).forEach(generatorCache::insert);
            }
        }
    }
}
