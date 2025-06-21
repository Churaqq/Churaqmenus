package cz.churaq.churaqmenus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GUITabCompleter implements TabCompleter {

    private final Main plugin;

    public GUITabCompleter(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Nabízíme pod-příkazy: open, create, reload, edit
            completions.addAll(Arrays.asList("open", "create", "reload", "edit"));
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("edit"))) {
            // Nabízíme názvy existujících menu
            File menusFolder = new File(plugin.getDataFolder(), "menus");
            if (menusFolder.exists()) {
                completions.addAll(Arrays.stream(menusFolder.listFiles((dir, name) -> name.endsWith(".yml")))
                        .map(file -> file.getName().replace(".yml", ""))
                        .collect(Collectors.toList()));
            }
        }

        // Filtrovat podle zadaného vstupu
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}