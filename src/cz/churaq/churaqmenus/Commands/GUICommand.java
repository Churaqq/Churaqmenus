package cz.churaq.churaqmenus;

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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTento příkaz je pouze pro hráče!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cPoužití: /customgui <open|create|reload|edit> [menu]");
            return true;
        }

        if (args[0].equalsIgnoreCase("open")) {
            if (args.length < 2) {
                player.sendMessage("§cPoužití: /customgui open <menu>");
                return true;
            }
            String guiName = args[1];
            guiManager.openGUI(player, guiName);
        } else if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) {
                player.sendMessage("§cPoužití: /customgui create <menu>");
                return true;
            }
            if (!player.hasPermission("churaqmenus.create")) {
                player.sendMessage("§cNemáš oprávnění k vytváření GUI!");
                return true;
            }
            String guiName = args[1];
            guiManager.createGUI(guiName, player);
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("churaqmenus.reload")) {
                player.sendMessage("§cNemáš oprávnění k reloadu GUI!");
                return true;
            }
            player.sendMessage("§aGUI byly reloadovány!");
            guiManager.reloadGUIs();
        } else if (args[0].equalsIgnoreCase("edit")) {
            if (args.length < 2) {
                player.sendMessage("§cPoužití: /customgui edit <menu>");
                return true;
            }
            if (!player.hasPermission("churaqmenus.edit")) {
                player.sendMessage("§cNemáš oprávnění k úpravě GUI!");
                return true;
            }
            String guiName = args[1];
            guiManager.editGUI(player, guiName);
        } else {
            player.sendMessage("§cNeznámý pod-příkaz! Použití: /customgui <open|create|reload|edit> [menu]");
        }

        return true;
    }
}