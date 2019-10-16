package pw.valaria.requirementsprocessor.defaults;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

import me.clip.placeholderapi.PlaceholderAPI;
import pw.valaria.requirementsprocessor.RequirementsProcessor;
import pw.valaria.requirementsprocessor.RequirementsUtil;

public class StringProcessor implements RequirementsProcessor {

    StringCheckType checkType;

    public StringProcessor(StringCheckType checkType){
        this.checkType = checkType;
    }

    @Override
    public boolean checkMatch(@NotNull Player player, @NotNull ConfigurationSection requirement) {
        String input = requirement.getString("input");
        String output = requirement.getString("output");

        Validate.notNull(input, "missing input!");
        Validate.notNull(output, "missing output!");

        if (RequirementsUtil.hasPlaceholderApi()) {
            input = PlaceholderAPI.setPlaceholders(player, input);
            output = PlaceholderAPI.setPlaceholders(player, output);
        }

        return checkType.check(input, output);
    }

    public static enum StringCheckType {
        STRING_EQUALS(String::equals),
        STRING_EQUALS_IGNORECASE(String::equalsIgnoreCase),
        STRING_CONTAINS(String::contains);

        BiFunction<String, String, Boolean> checker;

        StringCheckType(BiFunction<String, String, Boolean> checker) {
            this.checker = checker;
        }

        public boolean check(String input, String output) {
            return checker.apply(input, output);
        }
    }
}
