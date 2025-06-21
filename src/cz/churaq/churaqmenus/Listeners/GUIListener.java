package cz.churaq.churaqmenus.Listeners;

import cz.churaq.churaqmenus.GUI.GUIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

    private final GUIManager guiManager;

    public GUIListener(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        String title = e.getView().getTitle();
        var guis = guiManager.getPlugin().getConfig().getConfigurationSection("guis");
        if (guis == null) return;

        String guiName = null;
        for (String key : guis.getKeys(false)) {
            String configTitle = guis.getString(key + ".title").replace("&", "§");
            if (title.equals(configTitle)) {
                guiName = key;
                break;
            }
        }

        if (guiName == null) return;

        e.setCancelled(true);

        int slot = e.getSlot();
        var items = guis.getConfigurationSection(guiName + ".items");
        if (items == null) return;

        for (String key : items.getKeys(false)) {
            if (items.getInt(key + ".slot") == slot) {
                var action = items.getConfigurationSection(key + ".action");
                if (action == null) return;

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
}