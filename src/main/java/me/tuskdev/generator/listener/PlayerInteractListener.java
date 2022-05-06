package me.tuskdev.generator.listener;

import com.google.common.collect.ImmutableMap;
import me.tuskdev.generator.cache.GeneratorCache;
import me.tuskdev.generator.hologram.HologramLibrary;
import me.tuskdev.generator.inventory.ViewFrame;
import me.tuskdev.generator.model.Generator;
import me.tuskdev.generator.util.Coordinates;
import me.tuskdev.generator.view.GeneratorInventoryView;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final GeneratorCache generatorCache;
    private final ViewFrame viewFrame;

    public PlayerInteractListener(GeneratorCache generatorCache, ViewFrame viewFrame) {
        this.generatorCache = generatorCache;
        this.viewFrame = viewFrame;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("BLOCK")) return;

        Player player = event.getPlayer();
        Generator generator = generatorCache.select(Coordinates.of(event.getClickedBlock().getLocation()));
        if (generator == null || (!generator.getOwner().equals(player.getUniqueId()) && !player.hasPermission("generators.staff"))) return;

        viewFrame.open(GeneratorInventoryView.class, player, ImmutableMap.of("generator", generator));
    }

    // BLOCK ADD ITEM ON HOLOGRAM
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity == null || entity.getType() != EntityType.ARMOR_STAND) return;

        event.setCancelled(HologramLibrary.isHologram(entity));
    }

}
