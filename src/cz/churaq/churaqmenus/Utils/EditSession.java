package cz.churaq.churaqmenus.Utils;


import org.bukkit.inventory.Inventory;

import java.io.File;

public class EditSession {
    private final String guiName;
    private final File guiFile;
    private final Inventory inventory;

    public EditSession(String guiName, File guiFile, Inventory inventory) {
        this.guiName = guiName;
        this.guiFile = guiFile;
        this.inventory = inventory;
    }

    public String getGuiName() {
        return guiName;
    }

    public File getGuiFile() {
        return guiFile;
    }

    public Inventory getInventory() {
        return inventory;
    }
}