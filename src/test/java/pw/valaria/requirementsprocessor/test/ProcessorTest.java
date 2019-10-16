package pw.valaria.requirementsprocessor.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import pw.valaria.requirementsprocessor.RequirementsProcessor;
import pw.valaria.requirementsprocessor.RequirementsUtil;

public class ProcessorTest {

    ServerMock serverMock;
    MockPlugin mockPlugin;
    PlayerMock testplayer;

    @Before
    public void preTest() {
        serverMock = MockBukkit.mock();
        mockPlugin = MockBukkit.createMockPlugin();

        testplayer = new PlayerMock(serverMock, "testplayer") {
            @Override
            public boolean hasPermission(String name) {
                return "test.perm.true".equals(name);
            }
        };
        serverMock.addPlayer(testplayer);
    }


    @Test(expected = IllegalArgumentException.class)
    public void badType() {
        Configuration config = new YamlConfiguration();

        final ConfigurationSection booleanSection = config.createSection("requirements")
                                                            .createSection("test");

        booleanSection.set("requirement-type", "ILLEGAL_TYPE");

        RequirementsUtil.handle(testplayer, config);
    }

    @Test
    public void integerEquals() {
        Configuration config = new YamlConfiguration();
        final ConfigurationSection booleanSection = config.createSection("requirements")
                                                            .createSection("test");

        booleanSection.set("requirement-type", "==");
        booleanSection.set("input", "1");
        booleanSection.set("output", "1");

        Assert.assertTrue(RequirementsUtil.handle(testplayer, config));

        booleanSection.set("output", "2");
        Assert.assertFalse(RequirementsUtil.handle(testplayer, config));
    }

/*    @Test
    public void hasItem() {
        Configuration config = new YamlConfiguration();
        final ConfigurationSection booleanSection = config.createSection("requirements")
                                                            .createSection("test");

        String itemName = "&5testItem";
        List<String> itemLore = new ImmutableList.Builder<String>().add("&5test").add("&6test").build();

        booleanSection.set("requirement-type", "HAS_ITEM");
        final Optional<Material> material = Arrays.stream(Material.values()).filter(Material::isItem).findFirst();

        assert material.isPresent();

        booleanSection.set("material", material.get().toString());
        booleanSection.set("name", itemName);
        booleanSection.set("lore", itemLore);
        booleanSection.set("amount", 1);

        ItemStack testStack = new ItemStack(material.get(), 3);
        final ItemMeta itemMeta = testStack.getItemMeta();

        assert itemMeta != null;

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
        itemMeta.setLore(itemLore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList()));

        Assert.assertFalse(RequirementsUtil.handle(testplayer, config));

        testplayer.getInventory().setItem(1, testStack);

        Assert.assertTrue(RequirementsUtil.handle(testplayer, config));
    }*/

    @Test
    public void javascriptTest() {
        Configuration config = new YamlConfiguration();

        final ConfigurationSection booleanSection = config.createSection("requirements")
                                                            .createSection("test");

        booleanSection.set("requirement-type", "EXPRESSION");
        booleanSection.set("expression", "1 == 1");

        Assert.assertTrue(RequirementsUtil.handle(testplayer, config));
    }

    @Test
    public void permissionTest() {

        Configuration config = new YamlConfiguration();

        final ConfigurationSection booleanSection = config.createSection("requirements")
                                                     .createSection("boolean");

        booleanSection.set("requirement-type", "HAS_PERMISSION");
        booleanSection.set("input", "test.perm.true");


        Assert.assertTrue(RequirementsUtil.handle(testplayer, config));

        booleanSection.set("input", "test.perm.false");
        Assert.assertFalse(RequirementsUtil.handle(testplayer, config));

    }

    @Test
    public void testEquals() {
        Configuration config = new YamlConfiguration();

        final ConfigurationSection booleanSection = config.createSection("requirements")
                                                            .createSection("test");

        booleanSection.set("requirement-type", "STRING_EQUALS");
        booleanSection.set("input", "test");
        booleanSection.set("output", "test");


        Assert.assertTrue(RequirementsUtil.handle(testplayer, config));

        booleanSection.set("input", "Test");
        Assert.assertFalse(RequirementsUtil.handle(testplayer, config));
    }

    @Test
    public void testEqualsIgnore() {
        Configuration config = new YamlConfiguration();

        final ConfigurationSection booleanSection = config.createSection("requirements")
                                                            .createSection("test");

        booleanSection.set("requirement-type", "STRING_EQUALS_IGNORECASE");
        booleanSection.set("input", "test");
        booleanSection.set("output", "test");


        Assert.assertTrue(RequirementsUtil.handle(testplayer, config));

        booleanSection.set("input", "Test");
        Assert.assertTrue(RequirementsUtil.handle(testplayer, config));

        booleanSection.set("input", "nope");
        Assert.assertFalse(RequirementsUtil.handle(testplayer, config));
    }

    @Test
    public void testEqualsContains() {
        Configuration config = new YamlConfiguration();

        final ConfigurationSection booleanSection = config.createSection("requirements")
                                                            .createSection("test");

        booleanSection.set("requirement-type", "STRING_CONTAINS");
        booleanSection.set("input", "test");
        booleanSection.set("output", "test");


        Assert.assertTrue(RequirementsUtil.handle(testplayer, config));

        booleanSection.set("input", "_test_");
        Assert.assertTrue(RequirementsUtil.handle(testplayer, config));

        booleanSection.set("input", "nope");
        Assert.assertFalse(RequirementsUtil.handle(testplayer, config));
    }

    @After
    public void tearDown()
    {
        MockBukkit.unload();
    }
}
