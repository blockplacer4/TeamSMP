package de.janoschbl.teamsmp.TabCompletion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;
import java.util.Objects;

public class AdminTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return List.of("start", "setup", "heart");
        } else if (Objects.equals(args[0], "setup") && args.length == 2 ) {
            return List.of("BlocksPerPlayer", "BlocksPerPlayerDeath");
        }
        return null;
    }
}
