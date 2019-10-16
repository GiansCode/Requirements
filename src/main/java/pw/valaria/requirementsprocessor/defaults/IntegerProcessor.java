package pw.valaria.requirementsprocessor.defaults;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

import me.clip.placeholderapi.PlaceholderAPI;
import pw.valaria.requirementsprocessor.RequirementsProcessor;
import pw.valaria.requirementsprocessor.RequirementsUtil;

public class IntegerProcessor implements RequirementsProcessor {

    private final IntegerCheckType checkType;

    public IntegerProcessor(IntegerCheckType checkType) {
        this.checkType = checkType;
    }

    @Override
    public boolean checkMatch(@NotNull Player player, @NotNull ConfigurationSection requirement) {
        String inputString = requirement.getString("input");
        String outputString = requirement.getString("output");

        Validate.notNull(inputString, "input");
        Validate.notNull(outputString, "output");

        if (RequirementsUtil.hasPlaceholderApi()) {
            inputString = PlaceholderAPI.setPlaceholders(player, inputString);
            outputString = PlaceholderAPI.setPlaceholders(player, outputString);
        }

        Integer input;
        try {
            input = Integer.parseInt(inputString);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("illegal input", ex);
        }


        Integer output;
        try {
            output = Integer.parseInt(outputString);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("illegal output", ex);
        }


        return checkType.check(input, output);
    }


    public static enum IntegerCheckType {
        EQUALS(Integer::equals, "=="),
        GREATER_THAN_INCLUSIVE((input, output) -> {
            return input <= output;
        }, "=<"),
        LESS_THAN_INCLUSIVE((input, output) -> {
            return input >= output;
        }, ">="),
        NOT_EQUALS((input, output) -> {
            return !input.equals(output);
        }, "!="),
        GREATER_THAN((input, output) -> {
            return input > output;
        }, ">"),
        LESS_THAN((input, output) -> {
            return input < output;
        }, "<");

        private final BiFunction<Integer, Integer, Boolean> checker;
        private final String operator;

        IntegerCheckType(BiFunction<Integer, Integer, Boolean> checker, String operator) {
            this.checker = checker;
            this.operator = operator;
        }

        public boolean check(Integer input, Integer output) {
            return checker.apply(input, output);
        }

        public String getOperator() {
            return operator;
        }
    }
}
