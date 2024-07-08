package de.janoschbl.teamsmp.commands;

import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {

    private final MongoDBManager dbManager;

    public TeamCommand(MongoDBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            if (sender.hasPermission("team.admin")) {
                sender.sendMessage("§c§lRotstein >> §7Usage: /team <create|add|remove|chat> [args]");
            }
            else {
                sender.sendMessage("§c§lRotstein >> §7Usage: /team chat <message> or /tc <message>");
            }
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                if (!sender.hasPermission("team.create")) {
                    sender.sendMessage("§c§lRotstein >> §7Usage: /team chat <message> or /tc <message>");
                    return true;
                }
                handleCreate(sender, args);
                break;

            case "add":
                if (!sender.hasPermission("team.add")) {
                    sender.sendMessage("§c§lRotstein >> §7Usage: /team chat <message> or /tc <message>");
                    return true;
                }
                handleAdd(sender, args);
                break;

            case "remove":
                if (!sender.hasPermission("team.remove")) {
                    sender.sendMessage("§c§lRotstein >> §7Usage: /team chat <message> or /tc <message>");
                    return true;
                }
                handleRemove(sender, args);
                break;

            case "chat":
                handleChat(sender, args);
                break;

            case "delete":
                if (!sender.hasPermission("team.delete")) {
                    sender.sendMessage("§c§lRotstein >> §7Usage: /team chat <message> or /tc <message>");
                    return true;
                }
                handleDelete(sender, args);
                break;

            default:
                if (sender.hasPermission("team.admin")) {
                    sender.sendMessage("§c§lRotstein >> §7Usage: /team <create|add|remove|chat> [args]");
                }
                else {
                    sender.sendMessage("§c§lRotstein >> §7Usage: /team chat <message> or /tc <message>");
                }
                break;
        }

        return true;
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§c§lRotstein >> §7Usage: /team create <name> <tag> <leader>");
            return;
        }

        String name = args[1];
        String tag = args[2];
        Player leader = Bukkit.getPlayer(args[3]);

        if (leader == null) {
            sender.sendMessage("§c§lRotstein >> §7Leader not found.");
            return;
        }

        if (dbManager.getTeamByName(name) != null) {
            sender.sendMessage("§c§lRotstein >> §7Team already exists.");
            return;
        }
        Team team = new Team(name, tag, leader.getUniqueId());
        dbManager.addTeam(team);
        dbManager.addMemberToTeam(team.getId(), leader.getUniqueId());
        sender.sendMessage("§c§lRotstein >> §7Team created successfully.");
    }

    private void handleAdd(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c§lRotstein >> §7Usage: /team add <teamName> <player>");
            return;
        }

        String teamName = args[1];
        Player player = Bukkit.getPlayer(args[2]);

        if (player == null) {
            sender.sendMessage("§c§lRotstein >> §7Player not found.");
            return;
        }

        Team team = dbManager.getTeamByName(teamName);
        if (team == null) {
            sender.sendMessage("§c§lRotstein >> §7Team not found.");
            return;
        }

        dbManager.addMemberToTeam(team.getId(), player.getUniqueId());
        sender.sendMessage("§c§lRotstein >> §7Player added to the team.");
    }

    private void handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c§lRotstein >> §7Usage: /team delete <teamName>");
            return;
        }

        String teamName = args[1];

        Team team = dbManager.getTeamByName(teamName);
        if (team == null) {
            sender.sendMessage("§c§lRotstein >> §7Team not found.");
            return;
        }

        dbManager.deleteTeam(team);
        sender.sendMessage("§c§lRotstein >> §7Team deleted.");
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c§lRotstein >> §7Usage: /team remove <teamName> <player>");
            return;
        }

        String teamName = args[1];
        Player player = Bukkit.getPlayer(args[2]);

        if (player == null) {
            sender.sendMessage("§c§lRotstein >> §7Player not found.");
            return;
        }

        Team team = dbManager.getTeamByName(teamName);
        if (team == null) {
            sender.sendMessage("§c§lRotstein >> §7Team not found.");
            return;
        }

        dbManager.removeMemberFromTeam(team.getId(), player.getUniqueId());
        sender.sendMessage("§c§lRotstein >> §7Player removed from the team.");
    }

    private void handleChat(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c§lRotstein >> §7This command can only be used by players.");
            return;
        }

        Player player = (Player) sender;
        // Implement team chat functionality here
        sender.sendMessage("§c§lRotstein >> §7Team chat functionality not implemented yet.");
    }
}
