package pw.valaria.requirementsprocessor;


import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import pw.valaria.requirementsprocessor.defaults.IntegerProcessor;
import pw.valaria.requirementsprocessor.defaults.ItemStackProcessor;
import pw.valaria.requirementsprocessor.defaults.JavascriptProcessor;
import pw.valaria.requirementsprocessor.defaults.PermissionProcessor;
import pw.valaria.requirementsprocessor.defaults.StringProcessor;
import pw.valaria.requirementsprocessor.defaults.VaultProcessor;

public final class RequirementsUtil {

    private static final Map<String, RequirementsProcessor> requirementsProcessors = new HashMap<>();
    private static boolean debug = false;

    private static Boolean hasPapi;

    private RequirementsUtil(){}

    static {
        // Strings
        registerProcessor("STRING_EQUALS", new StringProcessor(StringProcessor.StringCheckType.STRING_EQUALS));
        registerProcessor("STRING_EQUALS_IGNORECASE", new StringProcessor(StringProcessor.StringCheckType.STRING_EQUALS_IGNORECASE));
        registerProcessor("STRING_CONTAINS", new StringProcessor(StringProcessor.StringCheckType.STRING_CONTAINS));

        // Perms
        registerProcessor("HAS_PERMISSION", new PermissionProcessor());

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            registerProcessor("HAS_MONEY", new VaultProcessor());
        }

        registerProcessor("EXPRESSION", new JavascriptProcessor());

        for (IntegerProcessor.IntegerCheckType value : IntegerProcessor.IntegerCheckType.values()) {
            registerProcessor(value.getOperator(), new IntegerProcessor(value));
        }

        registerProcessor("HAS_ITEM", new ItemStackProcessor());

    }

    /**
     * See {@link RequirementsUtil#registerProcessor(String, RequirementsProcessor, boolean)}  }
     * @param id of the processor to register
     * @param processor the processor to register
     */
    public static void registerProcessor(@NotNull String id, @NotNull RequirementsProcessor processor) {
        registerProcessor(id, processor, false);
    }

    /**
     * See {@link RequirementsUtil#registerProcessor(String, RequirementsProcessor, boolean)}  }
     * @param id of the processor to register
     * @param processor the processor to register
     * @param force force registration
     * @throws IllegalArgumentException if a processor is already registered with the ID and registration is not forced
     */
    public static void registerProcessor(@NotNull String id, @NotNull RequirementsProcessor processor, boolean force) {
        Validate.notNull(id, "id");
        Validate.notNull(processor, "processor");

        if (!force && requirementsProcessors.get(id) != null)
            throw new IllegalArgumentException("Cannot register duplicate processor: " + id);

        requirementsProcessors.put(id, processor);
    }


    /**
     *
     *     <pre>
     *      requirements:
     *      'test':
     *         requirement-type: 'STRING_EQUALS'
     *         input: '%player_name%' #PlaceholderAPI support
     *         output: 'Gianluca'
     *       </pre>
     *
     *
     * @param player Player to test
     * @param config configuration section
     * @return if the requirements have been met
     */
    public static boolean handle(@NotNull Player player, @NotNull ConfigurationSection config) {
        Validate.notNull(player, "player");
        Validate.notNull(config, "config");

        ConfigurationSection requirements = config.getConfigurationSection("requirements");

        // We're making the assumption that if we're passed a config with no requirements, there are none
        if (requirements == null) {
            return true;
        }

        for (String requirementsKey : requirements.getKeys(false)) {
            ConfigurationSection requirementSection = requirements.getConfigurationSection(requirementsKey);

            assert requirementSection != null;

            String requirementType = requirementSection.getString("requirement-type");

            if (requirementType == null)
                throw new IllegalArgumentException(requirementsKey + " does not have a requirement-type set!");

            RequirementsProcessor requirementsProcessor = requirementsProcessors.get(requirementType);

            if (requirementsProcessor == null)
                throw new IllegalArgumentException(requirementType + " is not registered as a valid requirement type");

            try {
                if (!requirementsProcessor.checkMatch(player, requirementSection)) {
                    return false;
                }
            } catch (Exception ex){
                throw new RuntimeException("An error occured while processing the requirement " + requirementsKey, ex);
            }
        }

        // if we get here, none of the processors said nope!
        return true;
    }

    public static void setDebug(boolean debug) {
        RequirementsUtil.debug = debug;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static boolean hasPlaceholderApi() {
        // micro-optimization
        if (hasPapi == null) {
            hasPapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        }

        return hasPapi;
    }
}
