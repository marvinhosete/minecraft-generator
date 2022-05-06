package me.tuskdev.generator.hologram;

import org.bukkit.entity.ArmorStand;

public interface ArmorHologram {

    void setText(String text);

    void killEntity();

    HologramLine getLine();

    Hologram getHologram();

    ArmorStand getArmorStand();

}
