package me.tuskdev.generator.util;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.tuskdev.generator.inventory.ReflectionUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class CustomHead {

    private static Encoder encoder;
    private static ItemStack base;
    private static Field profileField;

    static {
        try {
            base = new ItemStack(Material.SKULL_ITEM);
            base.setDurability((short) 3);
            Class<?> skullMetaClass = ReflectionUtils.getCraftClass("inventory.CraftMetaSkull");
            profileField = skullMetaClass.getDeclaredField("profile");
            profileField.setAccessible(true);
            encoder = Base64.getEncoder();
        } catch (Throwable ignored) {}
    }

    public static ItemStack skull(String url) {
        ItemStack skull = base.clone();
        try {
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            byte[] encodedData = encoder.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
            profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
            profileField.set(skullMeta, profile);
            skull.setItemMeta(skullMeta);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return skull;
    }

}