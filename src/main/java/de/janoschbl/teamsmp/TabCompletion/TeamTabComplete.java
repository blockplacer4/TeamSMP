package de.janoschbl.teamsmp.TabCompletion;

import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamTabComplete implements TabCompleter {

    private final List<String> subCommands = Arrays.asList("create", "add", "remove", "chat", "delete");
    private final MongoDBManager dbManager;

    public TeamTabComplete(MongoDBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
            return completions;
        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("add")) {
                return dbManager.getAllTeams();
            } else if (args[0].equalsIgnoreCase("delete")) {
                return dbManager.getAllTeams();
            } else if (args[0].equalsIgnoreCase("remove")) {
                return dbManager.getAllTeams();
            }
        } else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("create")) {
                return Arrays.asList("Green", "DarkGreen","Red", "Blue", "Aqua", "DarkAqua", "Yellow", "Gold");
            }
        } else if (args.length == 6) {
            if (args[0].equalsIgnoreCase("create")) {
                return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20");
            }
        }
        return null;
    }
}
