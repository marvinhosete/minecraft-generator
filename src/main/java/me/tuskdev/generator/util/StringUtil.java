package me.tuskdev.generator.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static List<String> translateAlternateColorCodes(Character altColorChar, List<String> listToTranslate) {
        List<String> result = new ArrayList<>(listToTranslate.size());
        listToTranslate.forEach(input -> result.add(ChatColor.translateAlternateColorCodes(altColorChar, input)));
        return result;
    }

    public static List<String> replace(List<String> value, StringReplacer<String> replacer) {
        List<String> result = new ArrayList<>(value.size());
        value.forEach(input -> result.add(replacer.accept(input)));
        return  result;
    }

}