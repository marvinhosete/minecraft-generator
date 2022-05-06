package me.tuskdev.generator.config.data;

import me.tuskdev.generator.util.ItemBuilder;
import me.tuskdev.generator.util.StringUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class SimpleGenerator {

    private final String type, itemsCommand;
    private final int itemsAmount, itemsDelay, valueUpgrade, maxUpgrade;

    public SimpleGenerator(ConfigurationSection configurationSection) {
        this.type = configurationSection.getString("type", "spawner").toUpperCase();
        this.itemsCommand = configurationSection.getString("command-items", "");
        this.itemsAmount = configurationSection.getInt("amount-items", 0);
        this.itemsDelay = configurationSection.getInt("delay-items", 0);
        this.valueUpgrade = configurationSection.getInt("value-upgrade", 0);
        this.maxUpgrade = configurationSection.getInt("max-upgrade", 0);
    }

    public String getType() {
        return type;
    }

    public String getItemsCommand() {
        return itemsCommand;
    }

    public int getItemsAmount() {
        return itemsAmount;
    }

    public int getItemsDelay() {
        return itemsDelay;
    }

    public int getValueUpgrade() {
        return valueUpgrade;
    }

    public int getMaxUpgrade() {
        return maxUpgrade;
    }

    public ItemStack build(ItemStack itemStack, int amount) {
        return new ItemBuilder(itemStack)
                .setAmount(amount)
                .setName(itemStack.getItemMeta().getDisplayName().replace("{type}", type.substring(0,1).toUpperCase() + type.substring(1).toLowerCase()))
                .setLore(StringUtil.replace(itemStack.getItemMeta().getLore(), s -> s.replace("{type}", type.substring(0,1).toUpperCase() + type.substring(1).toLowerCase())))
                .toItemStack();
    }
}
