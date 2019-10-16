package pw.valaria.requirementsprocessor.defaults;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import me.clip.placeholderapi.PlaceholderAPI;
import pw.valaria.requirementsprocessor.RequirementsProcessor;
import pw.valaria.requirementsprocessor.RequirementsUtil;

public class JavascriptProcessor implements RequirementsProcessor {

    ScriptEngine scriptEngine;

    public JavascriptProcessor() {
        scriptEngine = new ScriptEngineManager().getEngineByName("javascript");
    }


    @Override
    public boolean checkMatch(@NotNull Player player, @NotNull ConfigurationSection requirement) {
        String expression = requirement.getString("expression");

        if (expression == null)
            throw new IllegalArgumentException("expression is not set!");

        if (RequirementsUtil.hasPlaceholderApi()) {
            expression = PlaceholderAPI.setPlaceholders(player, expression);
        }


        final SimpleBindings bindings = new SimpleBindings();
        bindings.put("BukkitServer", Bukkit.getServer());
        bindings.put("BukkitPlayer", player);

        try {
            final Object eval = scriptEngine.eval(expression, bindings);
            if (!(eval instanceof Boolean))
                throw new IllegalArgumentException("expression did not resolve a boolean!");

            return (Boolean) eval;
        } catch (ScriptException e) {
            // Wrap exception
            throw new RuntimeException("exception processing expression", e);
        }

    }
}
