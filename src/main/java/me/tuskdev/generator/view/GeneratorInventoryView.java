package me.tuskdev.generator.view;

import com.google.common.collect.ImmutableMap;
import me.tuskdev.generator.cache.GeneratorCache;
import me.tuskdev.generator.config.GeneratorManager;
import me.tuskdev.generator.config.data.SimpleGenerator;
import me.tuskdev.generator.controller.GeneratorController;
import me.tuskdev.generator.hologram.Hologram;
import me.tuskdev.generator.hook.VaultHook;
import me.tuskdev.generator.inventory.View;
import me.tuskdev.generator.inventory.ViewContext;
import me.tuskdev.generator.model.Generator;
import me.tuskdev.generator.util.Coordinates;
import me.tuskdev.generator.util.ItemBuilder;
import me.tuskdev.generator.util.ItemNBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GeneratorInventoryView extends View {

    private final GeneratorCache generatorCache;
    private final GeneratorController generatorController;
    private final GeneratorManager generatorManager;
    private final ItemStack itemGenerator;
    private final ConfigurationSection messages;
    private final ConfigurationSection itemViewer;
    private final VaultHook vaultHook = new VaultHook();

    public GeneratorInventoryView(GeneratorCache generatorCache, GeneratorController generatorController, GeneratorManager generatorManager, ItemStack itemGenerator, ConfigurationSection messages, ConfigurationSection inventory) {
        super(5, "§b§l*§f§l*§b§l* §f§l§nGENERATOR MANAGER§b§l *§f§l*§b§l*");

        setCancelOnClick(true);

        for (int i = 0; i < 45; i++)
            if (i != 11 && i != 15 && i != 29 && i != 33) slot(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).toItemStack());

        slot(11, ItemBuilder.load(inventory.getConfigurationSection("collection-item"))).closeOnClick().onClick(handle -> handleCollection(handle.getPlayer(), handle.get("generator")));
        slot(15, ItemBuilder.load(inventory.getConfigurationSection("remove-item"))).closeOnClick().onClick(handle -> handleRemove(handle.getPlayer(), handle.get("generator")));
        slot(33, ItemBuilder.load(inventory.getConfigurationSection("information-item")));

        this.generatorCache = generatorCache;
        this.generatorController = generatorController;
        this.generatorManager = generatorManager;
        this.itemGenerator = itemGenerator;
        this.itemViewer = inventory.getConfigurationSection("viewer-item");
        this.messages = messages;
    }

    @Override
    protected void onRender(ViewContext context) {
        Generator generator = context.get("generator");

        context.slot(29, ItemBuilder.load(itemViewer, ImmutableMap.of("player", context.getPlayer().getName(), "level", generator.getLevel(), "next-level", generator.getLevel()+1))).closeOnClick().onClick(handle -> handleUpgrade(handle.getPlayer(), generator));
    }

    void handleCollection(Player player, Generator generator) {
        int items = generator.getItems();
        if (items <= 0) {
            player.sendMessage(message("no-items"));
            return;
        }

        SimpleGenerator simpleGenerator = generatorManager.getGenerator(generator.getType());

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), simpleGenerator.getItemsCommand().replace("{player}", player.getName()).replace("{amount}", "" + items));

        generator.setItems(0);
        generatorController.updateItems(0, generator.getCoordinates());

        player.sendMessage(message("collection-success").replace("{amount}", "" + items));
    }

    void handleRemove(Player player, Generator generator) {
        generator.getHologram().delete();

        Coordinates coordinates = generator.getCoordinates();
        generatorCache.delete(coordinates);
        generatorController.delete(coordinates);

        Block block = coordinates.build().getBlock();
        block.setType(Material.AIR);
        for (int x = -1; x < 2; x++) for (int z = -1; z < 2; z++) block.getRelative(x, -1, z).setType(Material.AIR);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), generatorManager.getGenerator(generator.getType()).getItemsCommand().replace("{player}", player.getName()).replace("{amount}", "" + generator.getItems()));
        player.getInventory().addItem(ItemNBT.set(generatorManager.getGenerator(generator.getType()).build(itemGenerator.clone(), generator.getAmount()), "type", generator.getType().toUpperCase(), "amount", "" + generator.getAmount(), "level", "" + generator.getLevel()));
        player.sendMessage(message("remove-success"));
    }

    void handleUpgrade(Player player, Generator generator) {
        SimpleGenerator simpleGenerator = generatorManager.getGenerator(generator.getType());

        if (generator.getLevel() >= simpleGenerator.getMaxUpgrade()) {
            player.sendMessage(message("max-upgrade"));
            return;
        }

        int next = generator.getLevel()+1;
        double balance = vaultHook.getBalance(player);
        double value = simpleGenerator.getValueUpgrade()*next;
        if (balance < value) {
            player.sendMessage(message("not-enough-money").replace("{value}", "" + value));
            return;
        }

        Hologram hologram = generator.getHologram();
        hologram.updateLine(2, String.format("§f§lTier: §b§l%s", next));

        generator.setLevel(next);
        generatorController.updateLevel(next, generator.getCoordinates());

        vaultHook.withdrawBalance(player, value);
        player.sendMessage(message("upgrade-success"));
    }

    String message(String key) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(key, "&cAn error occurred while trying to send the message."));
    }
}
