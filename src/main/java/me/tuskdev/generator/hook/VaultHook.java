package me.tuskdev.generator.hook;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private Economy economy;

    public VaultHook() {
        RegisteredServiceProvider<Economy> registeredServiceProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (registeredServiceProvider == null) return;
        economy = registeredServiceProvider.getProvider();
    }

    public double getBalance(Player player) {
        return (economy == null ? 0 : economy.getBalance(player));
    }

    public void withdrawBalance(Player player, double value) {
        if (economy == null) return;
        economy.withdrawPlayer(player, value);
    }

}
