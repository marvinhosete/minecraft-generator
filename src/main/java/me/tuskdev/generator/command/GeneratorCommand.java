package me.tuskdev.generator.command;

import me.saiintbrisson.bukkit.command.command.BukkitContext;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.tuskdev.generator.config.GeneratorManager;
import me.tuskdev.generator.config.data.SimpleGenerator;
import me.tuskdev.generator.util.ItemNBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GeneratorCommand {

    private final ItemStack generatorItem;
    private final GeneratorManager generatorManager;
    private final ConfigurationSection messages;

    public GeneratorCommand(ItemStack generatorItem, GeneratorManager generatorManager, ConfigurationSection messages) {
        this.generatorItem = generatorItem;
        this.generatorManager = generatorManager;
        this.messages = messages;

        System.out.println(messages);
        messages.getKeys(true).forEach(System.out::println);
    }

    @Command(
            name = "generator",
            permission = "cmd.generator"
    )
    public void handleCommand(BukkitContext context, String[] args) {
        if (args.length <= 2) {
            context.sendMessage(message("usage"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            context.sendMessage(message("player-not-found"));
            return;
        }

        SimpleGenerator simpleGenerator = generatorManager.getGenerator(args[1]);
        if (simpleGenerator == null) {
            context.sendMessage(message("generator-not-found"));
            return;
        }

        Integer amount = tryParseInt(args[2]);
        if (amount == null) {
            context.sendMessage(message("invalid-amount"));
            return;
        }

        target.getInventory().addItem(ItemNBT.set(simpleGenerator.build(generatorItem.clone(), amount), "type", args[1].toUpperCase(), "amount", "" + amount));
        context.sendMessage(message("success"));
    }

    Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    String message(String key) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(key, "&cAn error occurred while trying to send the message."));
    }

}
