package me.tuskdev.generator.view;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import me.tuskdev.generator.cache.GeneratorCache;
import me.tuskdev.generator.config.GeneratorManager;
import me.tuskdev.generator.config.data.SimpleGenerator;
import me.tuskdev.generator.controller.GeneratorController;
import me.tuskdev.generator.hook.VaultHook;
import me.tuskdev.generator.inventory.View;
import me.tuskdev.generator.inventory.ViewContext;
import me.tuskdev.generator.model.Generator;
import me.tuskdev.generator.util.Coordinates;
import me.tuskdev.generator.util.CustomHead;
import me.tuskdev.generator.util.ItemBuilder;
import me.tuskdev.generator.util.ItemNBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GeneratorInventoryView extends View {

    private final GeneratorCache generatorCache;
    private final GeneratorController generatorController;
    private final GeneratorManager generatorManager;
    private final ItemStack itemGenerator;
    private final VaultHook vaultHook = new VaultHook();

    public GeneratorInventoryView(GeneratorCache generatorCache, GeneratorController generatorController, GeneratorManager generatorManager, ItemStack itemGenerator) {
        super(5, "§b§l*§f§l*§b§l* §f§l§nGENERATOR MANAGER§b§l *§f§l*§b§l*");

        setCancelOnClick(true);

        for (int i = 0; i < 45; i++)
            if (i != 11 && i != 15 && i != 29 && i != 33) slot(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).toItemStack());

        slot(11, new ItemBuilder(CustomHead.skull("http://textures.minecraft.net/texture/37018eb436d9473499b68a7bdec42a23309890af97170007e4ae5f6e7af6a90f")).setName("§f§l<§b§l!§f§l> §b§l§nCOLLECTION§f§l <§b§l!§f§l>").setLore("§7Click to collect your balance.").toItemStack()).closeOnClick().onClick(handle -> handleCollection(handle.getPlayer(), handle.get("generator")));
        slot(15, new ItemBuilder(Material.BARRIER).setName("§f§l<§b§l!§f§l> §f§l§nREMOVE§b§l §nGENERATOR§f§l <§b§l!§f§l>").setLore("§7Click to remove your generator.", "§7(You will receive all items generated)").toItemStack()).closeOnClick().onClick(handle -> handleRemove(handle.getPlayer(), handle.get("generator")));
        slot(33, new ItemBuilder(CustomHead.skull("http://textures.minecraft.net/texture/cef6ce1a526517507703d7a2d52da3246d6f62057072b637142b4fd664c8704")).setName("§f§l<§b§l!§f§l> §nGENERATOR§b§l §nINFORMATION§f§l <§b§l!§f§l>").setLore("§f§l§nWhy do you need generators?", "§7Generators are here to help you and your island", "§7Climb the podium when you are working or not even on!", "§7Generators give your island that extra boost to help", "§7Become the #1 Island on TheGalaxies Battlefront!", "§b§l§nTypes of Generators:", "§3| §f§l<§a§l!§f§l> §a§lGEM GENERATOR §f§l<§a§l!§f§l>", "§3| §f§l<§c§l!§f§l> §c§lMOBCOIN GENERATOR §f§l<§c§l!§f§l>", "§3| §f§l<§6§l!§f§l> §6§lSPAWNER GENERATOR §f§l<§6§l!§f§l>", "§3| §f§l<§e§l!§f§l> §eSoon §f§l<§e§l!§f§l>", "§3| §f§l<§b§l!§f§l> §d§lO§b§lR§d§lB §b§lG§d§lE§b§lN§d§lE§b§lR§d§lA§b§lT§d§lO§b§lR §f§l<§d§l!§f§l>", "§f§l§nHow to Get More:", "§7You can get more through.", "§7§l/§c§lM§7§li§c§ln§7§li§c§ln§7§lg §c§lP§7§la§c§ls§7§ls", "§b§l/Orb §d§lShop").toItemStack());

        this.generatorCache = generatorCache;
        this.generatorController = generatorController;
        this.generatorManager = generatorManager;
        this.itemGenerator = itemGenerator;
    }

    @Override
    protected void onRender(ViewContext context) {
        Generator generator = context.get("generator");

        context.slot(29, new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setSkullOwner(context.getPlayer().getName()).setName("§f§l<§b§l!§f§l> §b§l§nUPGRADE§f§l <§b§l!§f§l>").setLore(String.format("§bCurrent Tier: §f%s", generator.getLevel()), String.format("§bNext Tier: §f%s", generator.getLevel()+1), "", "§bUpgrading your generator will", "§bIncrease the amount of", "§bGenerated Items you can get", "").toItemStack()).closeOnClick().onClick(handle -> handleUpgrade(handle.getPlayer(), generator));
    }

    void handleCollection(Player player, Generator generator) {
        int items = generator.getItems();
        if (items <= 0) {
            player.sendMessage("§cThere are no items to collect.");
            return;
        }

        SimpleGenerator simpleGenerator = generatorManager.getGenerator(generator.getType());

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), simpleGenerator.getItemsCommand().replace("{player}", player.getName()).replace("{amount}", "" + items));

        generator.setItems(0);
        generatorController.updateItems(0, generator.getCoordinates());

        player.sendMessage(String.format("§eYou have removed %s items from your generator.", items));
    }

    void handleRemove(Player player, Generator generator) {
        generator.getHologram().delete();

        Coordinates coordinates = generator.getCoordinates();
        generatorCache.delete(coordinates);
        generatorController.delete(coordinates);

        Block block = coordinates.build().getBlock();
        block.setType(Material.AIR);
        for (int x = -1; x < 2; x++) for (int z = -1; z < 2; z++) block.getRelative(x, -1, z).setType(Material.AIR);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), generatorManager.getGenerator(generator.getType()).getItemsCommand().replace("{player}", player.getName()).replace("{amount}", "" + generator.getItems()));
        player.getInventory().addItem(ItemNBT.set(generatorManager.getGenerator(generator.getType()).build(itemGenerator.clone(), generator.getAmount()), "type", generator.getType().toUpperCase(), "amount", "" + generator.getAmount(), "level", "" + generator.getLevel()));
        player.sendMessage("§eYay! Your generator has been successfully removed.");
    }

    void handleUpgrade(Player player, Generator generator) {
        SimpleGenerator simpleGenerator = generatorManager.getGenerator(generator.getType());

        if (generator.getLevel() >= simpleGenerator.getMaxUpgrade()) {
            player.sendMessage("§cYour generator is already at maximum level.");
            return;
        }

        int next = generator.getLevel()+1;
        double balance = vaultHook.getBalance(player);
        double value = simpleGenerator.getValueUpgrade()*next;
        if (balance < value) {
            player.sendMessage(String.format("§cYou need more %s to be able to evolve your generator.", value-balance));
            return;
        }

        Hologram hologram = generator.getHologram();
        hologram.removeLine(2);
        hologram.insertTextLine(2, String.format("§f§lTier: §b§l%s", next));

        generator.setLevel(next);
        generatorController.updateLevel(next, generator.getCoordinates());

        vaultHook.withdrawBalance(player, value);
        player.sendMessage("§eYay! Your generator has successfully leveled up.");
    }
}
