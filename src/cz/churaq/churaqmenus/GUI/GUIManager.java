package cz.churaq.churaqmenus.GUI;

import cz.churaq.churaqmenus.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class GUIManager {

    private final Main plugin;

    public GUIManager(Main plugin) {
        this.plugin = plugin;
    }

    // Přidána metoda getPlugin()
    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void openGUI(Player player, String guiName) {
        var guiConfig = plugin.getConfig().getConfigurationSection("guis." + guiName);
        if (guiConfig == null) {
            player.sendMessage("§cGUI " + guiName + " not found!");
            return;
        }

        String title = guiConfig.getString("title").replace("&", "§");
        int size = guiConfig.getInt("size");
        Inventory inv = Bukkit.createInventory(null, size, title);

        var items = guiConfig.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                var item = items.getConfigurationSection(key);
                if (item == null) continue;

                int slot = item.getInt("slot");
                Material material;
                try {
                    material = Material.valueOf(item.getString("material"));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Wrong material ID " + key + " in gui " + guiName);
                    continue;
                }

                ItemStack stack = new ItemStack(material);
                ItemMeta meta = stack.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(item.getString("name").replace("&", "§"));
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
}