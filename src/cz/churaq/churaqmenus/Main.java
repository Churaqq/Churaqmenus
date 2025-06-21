package cz.churaq.churaqmenus;

import cz.churaq.churaqmenus.Commands.GUICommand;
import cz.churaq.churaqmenus.GUI.GUIManager;
import cz.churaq.churaqmenus.Listeners.GUIListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private GUIManager guiManager;


    public void onEnable() {

        saveDefaultConfig();

        guiManager = new GUIManager(this);

        getCommand("customgui").setExecutor(new GUICommand(guiManager));

        getServer().getPluginManager().registerEvents(new GUIListener(guiManager), this);

        getLogger().info("ChuraqGUI enabled");
    }

    public void onDisable() {


    }

    public GUIManager getGuiManager() {
        return guiManager;
    }
}
