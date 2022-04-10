package me.tuskdev.generator.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.generator.config.GeneratorManager;
import me.tuskdev.generator.config.data.SimpleGenerator;
import me.tuskdev.generator.util.ItemNBT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GeneratorCommand {

    private final ItemStack generatorItem;
    private final GeneratorManager generatorManager;

    public GeneratorCommand(ItemStack generatorItem, GeneratorManager generatorManager) {
        this.generatorItem = generatorItem;
        this.generatorManager = generatorManager;
    }

    @Command(
            name = "generator",
            permission = "cmd.generator",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context, String[] args) {
        if (args.length <= 2) {
            context.sendMessage("§cUse: /generator <player> <type> <amount>.");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            context.sendMessage("§cThe selected player is offline.");
            return;
        }

        SimpleGenerator simpleGenerator = generatorManager.getGenerator(args[1]);
        if (simpleGenerator == null) {
            context.sendMessage("§cThe selected generator does not exist.");
            return;
        }

        Integer amount = tryParseInt(args[2]);
        if (amount == null) {
            context.sendMessage("§cThe number you entered is invalid.");
            return;
        }

        target.getInventory().addItem(ItemNBT.set(simpleGenerator.build(generatorItem.clone(), amount), "type", args[1].toUpperCase(), "amount", "" + amount));
        context.sendMessage("§eYay! The generator was successfully delivered to the player.");
    }

    Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
