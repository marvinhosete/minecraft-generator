package me.tuskdev.generator.listener;

import me.tuskdev.generator.cache.GeneratorCache;
import me.tuskdev.generator.config.GeneratorManager;
import me.tuskdev.generator.config.data.SimpleGenerator;
import me.tuskdev.generator.controller.GeneratorController;
import me.tuskdev.generator.model.Generator;
import me.tuskdev.generator.util.Coordinates;
import me.tuskdev.generator.util.GeneratorUtil;
import me.tuskdev.generator.util.ItemNBT;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BlockPlaceListener implements Listener {

    private final GeneratorCache generatorCache;
    private final GeneratorController generatorController;
    private final GeneratorManager generatorManager;

    public BlockPlaceListener(GeneratorCache generatorCache, GeneratorController generatorController, GeneratorManager generatorManager) {
        this.generatorCache = generatorCache;
        this.generatorController = generatorController;
        this.generatorManager = generatorManager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        if (itemStack == null || !ItemNBT.has(itemStack, "type")) return;
        event.setCancelled(true);

        String type = ItemNBT.get(itemStack, "type");
        SimpleGenerator simpleGenerator = generatorManager.getGenerator(type);

        UUID owner = event.getPlayer().getUniqueId();
        Location location = event.getBlock().getLocation().add(0, 1, 0);
        int amount = Integer.parseInt(ItemNBT.get(itemStack, "amount"));
        int level = ItemNBT.has(itemStack, "level") ? Integer.parseInt(ItemNBT.get(itemStack, "level")) : 1;

        Generator target = generatorCache.selectIf(coordinates -> location.distance(coordinates.build()) < 10);
        if (target != null && target.getOwner().equals(owner) && target.getType().equals(simpleGenerator.getType()) && target.getLevel() == level) {
            if (itemStack.getAmount() > 1) itemStack.setAmount(itemStack.getAmount() - 1);
            else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));

            target.setAmount(target.getAmount() + amount);
            generatorController.updateAmount(target.getAmount(), target.getCoordinates());
            return;
        } else if (target != null) {
            event.getPlayer().sendMessage("§cThere's a generator nearby, so you can't put another one here.");
            return;
        }

        if (itemStack.getAmount() > 1) itemStack.setAmount(itemStack.getAmount() - 1);
        else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
        Generator generator = new Generator(owner, Coordinates.of(location), type);
        generator.setAmount(amount);
        generator.setLevel(level);
        generator.setTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(simpleGenerator.getItemsDelay()));

        generatorCache.insert(generator);
        generatorController.insert(generator);

        GeneratorUtil.buildStruct(location);
        GeneratorUtil.hologram(generator, simpleGenerator);
    }

}
