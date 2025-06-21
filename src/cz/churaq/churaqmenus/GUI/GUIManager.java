package cz.churaq.churaqmenus.GUI;

import cz.churaq.churaqmenus.Main;
import cz.churaq.churaqmenus.Utils.EditSession;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GUIManager {

    private final Main plugin;

    public GUIManager(Main plugin) {
        this.plugin = plugin;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void openGUI(Player player, String guiName) {
        File guiFile = new File(plugin.getDataFolder(), "menus/" + guiName + ".yml");
        if (!guiFile.exists()) {
            player.sendMessage("§cGUI " + guiName + " not found!");
            plugin.getLogger().warning("Pokus o otevření neexistujícího GUI: " + guiFile.getAbsolutePath());
            return;
        }

        FileConfiguration guiConfig = YamlConfiguration.loadConfiguration(guiFile);
        if (guiConfig.getConfigurationSection("") == null) {
            player.sendMessage("§cGUI " + guiName + " má neplatnou konfiguraci!");
            plugin.getLogger().warning("Neplatná konfigurace v souboru: " + guiFile.getAbsolutePath());
            return;
        }

        String title = guiConfig.getString("title", "&cChybí název").replace("&", "§");
        int size = guiConfig.getInt("size", 9);
        if (size % 9 != 0 || size < 9 || size > 54) {
            player.sendMessage("§cNeplatná velikost GUI " + guiName + "!");
            plugin.getLogger().warning("Neplatná velikost " + size + " pro GUI " + guiName);
            return;
        }

        Inventory inv = Bukkit.createInventory(null, size, title);

        var items = guiConfig.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                var item = items.getConfigurationSection(key);
                if (item == null) {
                    plugin.getLogger().warning("Neplatná konfigurace pro item " + key + " v GUI " + guiName);
                    continue;
                }

                int slot = item.getInt("slot", -1);
                if (slot < 0 || slot >= size) {
                    plugin.getLogger().warning("Neplatný slot " + slot + " pro item " + key + " v GUI " + guiName);
                    continue;
                }

                String materialName = item.getString("material");
                Material material;
                try {
                    material = Material.valueOf(materialName);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Neplatný materiál '" + materialName + "' pro item " + key + " v GUI " + guiName);
                    continue;
                }

                ItemStack stack = new ItemStack(material);
                ItemMeta meta = stack.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(item.getString("name", " ").replace("&", "§"));
                    List<String> lore = item.getStringList("lore").stream()
                            .map(s -> s.replace("&", "§"))
                            .collect(Collectors.toList());
                    meta.setLore(lore);
                    stack.setItemMeta(meta);
                }
                inv.setItem(slot, stack);
            }
        }

        player.openInventory(inv);
    }

    public boolean createGUI(String guiName, Player player) {
        File menusFolder = new File(plugin.getDataFolder(), "menus");
        if (!menusFolder.exists()) {
            if (!menusFolder.mkdirs()) {
                player.sendMessage("§cChyba: Nelze vytvořit složku menus!");
                plugin.getLogger().severe("Nelze vytvořit složku: " + menusFolder.getAbsolutePath());
                return false;
            }
        }

        File guiFile = new File(menusFolder, guiName + ".yml");
        if (guiFile.exists()) {
            player.sendMessage("§cGUI " + guiName + " již existuje!");
            return false;
        }

        try {
            if (!guiFile.createNewFile()) {
                player.sendMessage("§cChyba při vytváření souboru " + guiName + ".yml!");
                plugin.getLogger().warning("Nepodařilo se vytvořit soubor: " + guiFile.getAbsolutePath());
                return false;
            }
            plugin.getLogger().info("Vytvořen soubor: " + guiFile.getAbsolutePath());

            FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(guiFile);
            fileConfig.set("title", "&6" + guiName);
            fileConfig.set("size", 27);
            fileConfig.set("items.priklad.slot", 0);
            fileConfig.set("items.priklad.material", "STONE");
            fileConfig.set("items.priklad.name", "&ePříklad");
            fileConfig.set("items.priklad.lore", List.of("&7Toto je příklad itemu"));

            fileConfig.save(guiFile);
            plugin.getLogger().info("Uložen soubor: " + guiFile.getAbsolutePath());

            if (!guiFile.exists()) {
                player.sendMessage("§cChyba: Soubor " + guiName + ".yml nebyl vytvořen!");
                plugin.getLogger().severe("Soubor " + guiFile.getAbsolutePath() + " nebyl nalezen po uložení!");
                return false;
            }

            player.sendMessage("§aGUI " + guiName + " vytvořeno!");
            return true;
        } catch (IOException e) {
            player.sendMessage("§cChyba při vytváření GUI " + guiName + ": " + e.getMessage());
            plugin.getLogger().severe("Chyba při vytváření souboru " + guiFile.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void editGUI(Player player, String guiName) {
        File guiFile = new File(plugin.getDataFolder(), "menus/" + guiName + ".yml");
        if (!guiFile.exists()) {
            player.sendMessage("§cGUI " + guiName + " not found!");
            plugin.getLogger().warning("Pokus o úpravu neexistujícího GUI: " + guiFile.getAbsolutePath());
            return;
        }

        FileConfiguration guiConfig = YamlConfiguration.loadConfiguration(guiFile);
        String title = guiConfig.getString("title", "&cChybí název").replace("&", "§") + " §7[Editor]";
        int size = guiConfig.getInt("size", 9);
        if (size % 9 != 0 || size < 9 || size > 54) {
            player.sendMessage("§cNeplatná velikost GUI " + guiName + "!");
            plugin.getLogger().warning("Neplatná velikost " + size + " pro GUI " + guiName);
            return;
        }

        Inventory inv = Bukkit.createInventory(null, size, title);

        var items = guiConfig.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                var item = items.getConfigurationSection(key);
                if (item == null) continue;

                int slot = item.getInt("slot", -1);
                if (slot < 0 || slot >= size) continue;

                String materialName = item.getString("material");
                Material material;
                try {
                    material = Material.valueOf(materialName);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                ItemStack stack = new ItemStack(material);
                ItemMeta meta = stack.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(item.getString("name", " ").replace("&", "§"));
                    List<String> lore = item.getStringList("lore").stream()
                            .map(s -> s.replace("&", "§"))
                            .collect(Collectors.toList());
                    meta.setLore(lore);
                    stack.setItemMeta(meta);
                }
                inv.setItem(slot, stack);
            }
        }

        // Uložíme, že hráč edituje toto GUI
        player.openInventory(inv);
        plugin.getEditingPlayers().put(player.getUniqueId(), new EditSession(guiName, guiFile, inv));
    }

    public void saveGUIEdit(Player player, Inventory inventory) {
        EditSession session = plugin.getEditingPlayers().get(player.getUniqueId());
        if (session == null) return;

        String guiName = session.getGuiName();
        File guiFile = session.getGuiFile();
        FileConfiguration guiConfig = YamlConfiguration.loadConfiguration(guiFile);

        // Zachováme původní title a size
        String title = guiConfig.getString("title", "&6" + guiName);
        int size = guiConfig.getInt("size", 27);

        // Vytvoříme novou konfiguraci pro items
        guiConfig.set("items", null); // Smažeme staré items
        int itemCounter = 1;
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack == null || stack.getType() == Material.AIR) continue;

            String itemKey = "item" + itemCounter++;
            guiConfig.set("items." + itemKey + ".slot", slot);
            guiConfig.set("items." + itemKey + ".material", stack.getType().name());
            if (stack.hasItemMeta() && stack.getItemMeta() != null) {
                ItemMeta meta = stack.getItemMeta();
                if (meta.hasDisplayName()) {
                    guiConfig.set("items." + itemKey + ".name", meta.getDisplayName().replace("§", "&"));
                }
                if (meta.hasLore()) {
                    guiConfig.set("items." + itemKey + ".lore", meta.getLore().stream()
                            .map(s -> s.replace("§", "&"))
                            .collect(Collectors.toList()));
                }
            }
        }

        try {
            guiConfig.save(guiFile);
            plugin.getLogger().info("Uložen soubor po editaci: " + guiFile.getAbsolutePath());
            player.sendMessage("§aZměny v GUI " + guiName + " uloženy!");
        } catch (IOException e) {
            player.sendMessage("§cChyba při ukládání GUI " + guiName + ": " + e.getMessage());
            plugin.getLogger().severe("Chyba při ukládání souboru " + guiFile.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
        }

        // Odstraníme hráče z editačního módu
        plugin.getEditingPlayers().remove(player.getUniqueId());
    }

    public void reloadGUIs() {
        plugin.getLogger().info("GUI soubory byly reloadovány.");
    }
}