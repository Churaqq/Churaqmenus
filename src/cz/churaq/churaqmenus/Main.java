package cz.churaq.churaqmenus;

import cz.churaq.churaqmenus.GUI.GUIManager;
import cz.churaq.churaqmenus.Listeners.GUIListener;
import cz.churaq.churaqmenus.Utils.EditSession;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin {

    private GUIManager guiManager;
    private final HashMap<UUID, EditSession> editingPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        File menusFolder = new File(getDataFolder(), "menus");
        if (!menusFolder.exists()) {
            if (!menusFolder.mkdirs()) {
                getLogger().severe("Nelze vytvořit složku menus: " + menusFolder.getAbsolutePath());
            } else {
                getLogger().info("Vytvořena složka: " + menusFolder.getAbsolutePath());
            }
        }

        File defaultFile = new File(menusFolder, "default.yml");
        if (!defaultFile.exists()) {
            try {
                if (!defaultFile.createNewFile()) {
                    getLogger().warning("Nepodařilo se vytvořit default.yml!");
                }
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultFile);
                defaultConfig.set("title", "&6Defaultní Menu");
                defaultConfig.set("size", 27);
                defaultConfig.set("items.mec.slot", 10);
                defaultConfig.set("items.mec.material", "DIAMOND_SWORD");
                defaultConfig.set("items.mec.name", "&aMeč Bojovníka");
                defaultConfig.set("items.mec.lore", List.of("&7Klikni pro získání meče!"));
                defaultConfig.set("items.mec.action.type", "COMMAND");
                defaultConfig.set("items.mec.action.value", "give %player% diamond_sword 1");
                defaultConfig.set("items.pravidla.slot", 12);
                defaultConfig.set("items.pravidla.material", "BOOK");
                defaultConfig.set("items.pravidla.name", "&ePravidla");
                defaultConfig.set("items.pravidla.lore", List.of("&7Zobrazí pravidla serveru."));
                defaultConfig.set("items.pravidla.action.type", "MESSAGE");
                defaultConfig.set("items.pravidla.action.value", "Pravidla: Buď slušný a necheatuj!");
                defaultConfig.save(defaultFile);
                getLogger().info("Vytvořen a uložen default.yml: " + defaultFile.getAbsolutePath());
            } catch (IOException e) {
                getLogger().warning("Chyba při vytváření default.yml: " + e.getMessage());
                e.printStackTrace();
            }
        }

        guiManager = new GUIManager(this);

        getCommand("customgui").setExecutor(new cz.churaq.churaqmenus.GUICommand(guiManager));
        getCommand("customgui").setTabCompleter(new cz.churaq.churaqmenus.GUITabCompleter(this));

        getServer().getPluginManager().registerEvents(new GUIListener(guiManager), this);

        getLogger().info("ChuraqMenus plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChuraqMenus plugin disabled!");
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public HashMap<UUID, EditSession> getEditingPlayers() {
        return editingPlayers;
    }
}