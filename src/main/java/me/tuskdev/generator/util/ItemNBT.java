package me.tuskdev.generator.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.tuskdev.generator.inventory.ReflectionUtils;
import org.bukkit.inventory.ItemStack;

public class ItemNBT {

    private static Class<?> NBTTagCompoundClass;
    private static Method asNMSCopy;
    private static Method asCraftMirror;
    private static Method setNBTTagCompound;
    private static Method hasNBTTagCompound;
    private static Method getNBTTagCompound;
    private static Method hasKey;
    private static Method getString;
    private static Method setString;

    static {
        try {
            Class<?> itemStackClass = ReflectionUtils.getNMSClass("ItemStack");
            Class<?> craftItemStackClass = ReflectionUtils.getCraftClass("inventory.CraftItemStack");

            NBTTagCompoundClass = ReflectionUtils.getNMSClass("NBTTagCompound");
            Class<?> NBTBaseClass = ReflectionUtils.getNMSClass("NBTBase");

            asNMSCopy = craftItemStackClass.getDeclaredMethod("asNMSCopy", ItemStack.class);
            asCraftMirror = craftItemStackClass.getDeclaredMethod("asCraftMirror", itemStackClass);

            getNBTTagCompound = itemStackClass.getDeclaredMethod("getTag");
            hasNBTTagCompound = itemStackClass.getDeclaredMethod("hasTag");
            setNBTTagCompound = itemStackClass.getDeclaredMethod("setTag", NBTTagCompoundClass);

            hasKey = NBTTagCompoundClass.getDeclaredMethod("hasKey", String.class);
            getString = NBTTagCompoundClass.getDeclaredMethod("getString", String.class);
            setString = NBTTagCompoundClass.getDeclaredMethod("setString", String.class, String.class);

            Field map = NBTTagCompoundClass.getDeclaredField("map");
            map.setAccessible(true);

            Method createTag = NBTBaseClass.getDeclaredMethod("createTag", byte.class);
            createTag.setAccessible(true);
        }
        catch (Throwable ignored) {}
    }

    public static boolean has(ItemStack item, String key) {
        try {
            Object craftItemStack = asNMSCopy.invoke(null, item);
            if (!(boolean) hasNBTTagCompound.invoke(craftItemStack)) return false;

            Object nbtTagCompound = getNBTTagCompound.invoke(craftItemStack);
            return (boolean) hasKey.invoke(nbtTagCompound, key);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String get(ItemStack item, String key) {
        try {
            Object craftItemStack = asNMSCopy.invoke(null, item);
            if (!(boolean) hasNBTTagCompound.invoke(craftItemStack)) return null;

            Object NBTTagCompound = getNBTTagCompound.invoke(craftItemStack);
            return getString.invoke(NBTTagCompound, key).toString();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack set(ItemStack item, String... value) {
        try	{
            Object craftItemStack = asNMSCopy.invoke(null, item);
            Object nbtTagCompound = ((boolean) hasNBTTagCompound.invoke(craftItemStack) ? getNBTTagCompound.invoke(craftItemStack) : NBTTagCompoundClass.newInstance());

            for (int i = 0; i < value.length; i+=2) setString.invoke(nbtTagCompound, value[i], value[i+1]);

            setNBTTagCompound.invoke(craftItemStack, nbtTagCompound);
            return (ItemStack) asCraftMirror.invoke(null, craftItemStack);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

}