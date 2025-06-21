package cz.churaq.churaqmenus.Listeners;

import cz.churaq.churaqmenus.Utils.EditSession;
import cz.churaq.churaqmenus.GUI.GUIManager;
import cz.churaq.churaqmenus.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.io.File;

public class GUIListener implements Listener {

    private final GUIManager guiManager;

    public GUIListener(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        // Přetypujeme plugin na Main pro přístup k getEditingPlayers
        Main plugin = (Main) guiManager.getPlugin();
        EditSession session = plugin.getEditingPlayers().get(player.getUniqueId());

        // Pokud hráč edituje GUI
        if (session != null) {
            String title = e.getView().getTitle();
            // Kontrolujeme, zda název obsahuje "[Editor]"
            if (title.contains("[Editor]")) {
                e.setCancelled(false); // Povolíme manipulaci s itemy v editačním módu
                return;
            }
        }

        // Normální GUI
        String guiName = null;
        FileConfiguration guiConfig = null;

        File menusFolder = new File(guiManager.getPlugin().getDataFolder(), "menus");
        if (menusFolder.exists()) {
            for (File file : menusFolder.listFiles((dir, name) -> name.endsWith(".yml"))) {
                FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
                String configTitle = fileConfig.getString("title", "").replace("&", "§");
                if (e.getView().getTitle().equals(configTitle)) {
                    guiName = file.getName().replace(".yml", "");
                    guiConfig = fileConfig;
                    break;
                }
            }
        }

        if (guiName == null || guiConfig == null) return;

        e.setCancelled(true);

        int slot = e.getSlot();
        var items = guiConfig.getConfigurationSection("items");
        if (items == null) return;

        for (String key : items.getKeys(false)) {
            var item = items.getConfigurationSection(key);
            if (item == null) {
                guiManager.getPlugin().getLogger().warning("Neplatná konfigurace pro item " + key + " v GUI " + guiName);
                continue;
            }

            if (item.getInt("slot", -1) == slot) {
                var action = item.getConfigurationSection("action");
                if (action == null) continue;

                String type = action.getString("type");
                String value = action.getString("value");

                if (type != null && type.equalsIgnoreCase("COMMAND")) {
                    if (value != null) {
                        value = value.replace("%player%", player.getName());
                        player.getServer().dispatchCommand(player.getServer().getConsoleSender(), value);
                    }
                } else if (type != null && type.equalsIgnoreCase("MESSAGE")) {
                    if (value != null) {
                        player.sendMessage(value.replace("&", "§"));
                    }
                }
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;

        // Přetypujeme plugin na Main
        Main plugin = (Main) guiManager.getPlugin();
        EditSession session = plugin.getEditingPlayers().get(player.getUniqueId());
        if (session != null && e.getView().getTitle().contains("[Editor]")) {
            guiManager.saveGUIEdit(player, e.getInventory());
        }
    }
}