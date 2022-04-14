package me.tuskdev.generator.hologram;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class EntityHologramStand implements ArmorHologram {

    private final HologramLine line;
    private final ArmorStand armorStand;

    public EntityHologramStand(Location toSpawn, HologramLine line) {
        ArmorStand armorStand = toSpawn.getWorld().spawn(toSpawn, ArmorStand.class);
        armorStand.setArms(false);
        armorStand.setBasePlate(false);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setSmall(true);
        armorStand.setCanPickupItems(false);

        this.line = line;
        this.armorStand = armorStand;
    }

    @Override
    public void setText(String text) {
        if (text != null && text.length() > 300) text = text.substring(0, 300);

        armorStand.setCustomName(text == null ? "" : text);
        armorStand.setCustomNameVisible(text != null && !text.isEmpty());
    }

    @Override
    public void killEntity() {
        armorStand.remove();
    }

    @Override
    public HologramLine getLine() {
        return line;
    }

    @Override
    public Hologram getHologram() {
        return line == null ? null : line.getHologram();
    }

}
