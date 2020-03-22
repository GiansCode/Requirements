package pw.valaria.requirementsprocessor.defaults;

import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pw.valaria.requirementsprocessor.RequirementsProcessor;
import pw.valaria.requirementsprocessor.RequirementsUtil;

public class RegexProcessor implements RequirementsProcessor {
    
    @Override
    public boolean checkMatch(@NotNull Player player, @NotNull ConfigurationSection requirement) {
        String input = requirement.getString("input");
        String regex = requirement.getString("regex");

        Validate.notNull(input);
        Validate.notNull(regex);

        if (RequirementsUtil.hasPlaceholderApi())
            input = PlaceholderAPI.setPlaceholders(player, input);
        
        return input.matches(regex);
    }
    
}
