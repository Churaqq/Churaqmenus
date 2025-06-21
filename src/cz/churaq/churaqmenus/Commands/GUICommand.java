package cz.churaq.churaqmenus.Commands;

import cz.churaq.churaqmenus.GUI.GUIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GUICommand implements CommandExecutor {

    private final GUIManager guiManager;

    public GUICommand(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c Only use player!");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /customgui open menu");
            return true;
        }
        if (args[0].equalsIgnoreCase("open")) {
            String guiName = args[1];
            guiManager.openGUI(player, guiName);
        } else {
            player.sendMessage("§cUnknown command! usage: /customgui open menu");
        }

        return true;
    }
}
