package pw.valaria.requirementsprocessor;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RequirementsProcessor {

    boolean checkMatch(@NotNull Player player, @NotNull ConfigurationSection requirement);
}
