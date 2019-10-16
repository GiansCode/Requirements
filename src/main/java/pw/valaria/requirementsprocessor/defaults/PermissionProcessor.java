package pw.valaria.requirementsprocessor.defaults;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import pw.valaria.requirementsprocessor.RequirementsProcessor;

public class PermissionProcessor implements RequirementsProcessor {
    @Override
    public boolean checkMatch(@NotNull Player player, @NotNull ConfigurationSection requirement) {
        String input = requirement.getString("input");

        if (input == null)
            throw new IllegalArgumentException("Missing input!");

        return player.hasPermission(input);
    }
}
