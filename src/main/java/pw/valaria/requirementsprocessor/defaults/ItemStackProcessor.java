package pw.valaria.requirementsprocessor.defaults;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import pw.valaria.requirementsprocessor.RequirementsProcessor;

public class ItemStackProcessor implements RequirementsProcessor {
    public ItemStackProcessor() {
    }

    @Override
    public boolean checkMatch(@NotNull Player player, @NotNull ConfigurationSection requirement) {
        String materialType = requirement.getString("material");
        int amount = requirement.getInt("amount", 1);

        Validate.notNull(materialType, "material type not set");

        Material material = Material.getMaterial(materialType);

        Validate.notNull(material, "invalid material type: " + materialType);

        ItemStack item = new ItemStack(material, amount);

        if (requirement.isSet("data")) {
            //noinspection deprecation
            item.setDurability((short) requirement.getInt("short"));
        }

        String name = requirement.getString("name");
        List<String> lore = requirement.getStringList("lore");

        if (name != null) {
            name = ChatColor.translateAlternateColorCodes('&', name);
        }

        if (lore != null) {
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
            }
        }

        if (name != null || lore != null) {
            final ItemMeta itemMeta = item.getItemMeta();

            itemMeta.setDisplayName(name);
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }

        return player.getInventory().containsAtLeast(item, item.getAmount());

    }
}
