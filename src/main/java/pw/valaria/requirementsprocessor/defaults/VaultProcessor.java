package pw.valaria.requirementsprocessor.defaults;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import pw.valaria.requirementsprocessor.RequirementsProcessor;

public class VaultProcessor implements RequirementsProcessor {

    @Override
    public boolean checkMatch(@NotNull Player player, @NotNull ConfigurationSection requirement) {

        final double amount = requirement.getDouble("amount");
        if (amount <= 0)
            throw new IllegalArgumentException("invalid/unset amount!");

        final RegisteredServiceProvider<Economy> registration = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (registration == null)
            throw new IllegalArgumentException("No economy provider found!");

        return registration.getProvider().has(player, amount);
    }
}
