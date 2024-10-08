package de.janoschbl.teamsmp.commands;

import de.janoschbl.teamsmp.Main;
import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TeamCommand implements CommandExecutor {

    private final MongoDBManager dbManager;
    private final LuckPerms luckPerms;

    private final String prefix = Main.getProvidingPlugin(Main.class).getConfig().getString("server.settings.prefix");

    public TeamCommand(MongoDBManager dbManager, LuckPerms luckPerms) {
        this.dbManager = dbManager;
        this.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            if (sender.hasPermission("team.admin")) {
                sender.sendMessage(STR."\{prefix}Usage: /team <create|add|remove|chat|delete> [args]");
            }
            else {
                sender.sendMessage(STR."\{prefix}Usage: /team chat <message> or /tc <message>");
            }
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                if (!sender.hasPermission("team.create")) {
                    sender.sendMessage(STR."\{prefix}Usage: /team chat <message> or /tc <message>");
                    return true;
                }
                handleCreate(sender, args);
                break;

            case "add":
                if (!sender.hasPermission("team.add")) {
                    sender.sendMessage(STR."\{prefix}Usage: /team chat <message> or /tc <message>");
                    return true;
                }
                handleAdd(sender, args);
                break;

            case "remove":
                if (!sender.hasPermission("team.remove")) {
                    sender.sendMessage(STR."\{prefix}Usage: /team chat <message> or /tc <message>");
                    return true;
                }
                handleRemove(sender, args);
                break;

            case "chat":
                handleChat(sender, args);
                break;

            case "delete":
                if (!sender.hasPermission("team.delete")) {
                    sender.sendMessage(STR."\{prefix}Usage: /team chat <message> or /tc <message>");
                    return true;
                }
                handleDelete(sender, args);
                break;

            default:
                if (sender.hasPermission("team.admin")) {
                    sender.sendMessage(STR."\{prefix}Usage: /team <create|add|remove|chat|delete> [args]");
                }
                else {
                    sender.sendMessage(STR."\{prefix}Usage: /team chat <message> or /tc <message>");
                }
                break;
        }

        return true;
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (args.length < 6) {
            sender.sendMessage(STR."\{prefix}Usage: /team create <name> <tag> <leader> <color> [hearts]");
            return;
        }

        String name = args[1];
        String tag = args[2];
        Player leader = Bukkit.getPlayer(args[3]);
        String color = args[4];
        Integer hearts = Integer.valueOf(args[5]);

        if (leader == null) {
            sender.sendMessage(STR."\{prefix}Leader not found.");
            return;
        }

        if (dbManager.getTeamByName(name) != null) {
            sender.sendMessage(STR."\{prefix}Team already exists.");
            return;
        }

        Team team = new Team(name, tag, leader.getUniqueId(), color, hearts);
        sender.sendMessage(STR."\{prefix}Team created successfully.");

        // database -> create document
        dbManager.addTeam(team);
        Team newTeam = dbManager.getTeamByName(name);
        dbManager.addMemberToTeam(newTeam.getId(), leader.getUniqueId());

        // luckperms create group
        Group group = luckPerms.getGroupManager().createAndLoadGroup(team.getName()).join();
        ChatColor chatcolor = Main.teamColors.getOrDefault(team.getColor(), ChatColor.WHITE);
        Node prefixNode = Node.builder("prefix.10." + chatcolor + team.getTag() + "§8 | §7").build();
        group.data().add(prefixNode);
        luckPerms.getGroupManager().saveGroup(group);

        // add user to group
        User user = luckPerms.getUserManager().getUser(leader.getUniqueId());
        if (user == null) {
            user = luckPerms.getUserManager().loadUser(leader.getUniqueId()).join();
        }

        Node groupNode = InheritanceNode.builder(team.getName()).build();
        user.data().add(groupNode);
        luckPerms.getUserManager().saveUser(user);
    }

    private void handleAdd(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(STR."\{prefix}Usage: /team add <teamName> <player>");
            return;
        }

        String teamName = args[1];
        Player player = Bukkit.getPlayer(args[2]);

        if (player == null) {
            sender.sendMessage(STR."\{prefix}Player not found.");
            return;
        }

        Team team = dbManager.getTeamByName(teamName);
        if (team == null) {
            sender.sendMessage(STR."\{prefix}Team not found.");
            return;
        }


        boolean found = false;
        for (UUID memberuuid : team.getMembers()) {
            if (memberuuid.equals(player.getUniqueId())) {
                found = true;
                break;
            }
        }

        if (found) {
            sender.sendMessage(STR."\{prefix}The player is already part of the team");
            return;
        }
        sender.sendMessage(STR."\{prefix}Player added to the team.");

        // add to database
        dbManager.addMemberToTeam(team.getId(), player.getUniqueId());

        // add user to luckperms group X.x
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            user = luckPerms.getUserManager().loadUser(player.getUniqueId()).join();
        }

        Node groupNode = InheritanceNode.builder(team.getName()).build();
        user.data().add(groupNode);
        luckPerms.getUserManager().saveUser(user);
    }

    private void handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(STR."\{prefix}Usage: /team delete <teamName>");
            return;
        }

        String teamName = args[1];

        Team team = dbManager.getTeamByName(teamName);
        if (team == null) {
            sender.sendMessage(STR."\{prefix}Team not found.");
            return;
        }
        sender.sendMessage(STR."\{prefix}Team deleted.");

        // handle Team deletion in Database X,x
        dbManager.deleteTeam(team);

        // delete Luckperms group
        GroupManager groupManager = luckPerms.getGroupManager();
        Group group = groupManager.getGroup(team.getName());
        groupManager.deleteGroup(group).join();
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(STR."\{prefix}Usage: /team remove <teamName> <player>");
            return;
        }

        String teamName = args[1];
        Player player = Bukkit.getPlayer(args[2]);

        if (player == null) {
            sender.sendMessage(STR."\{prefix}Player not found.");
            return;
        }

        Team team = dbManager.getTeamByName(teamName);
        if (team == null) {
            sender.sendMessage(STR."\{prefix}Team not found.");
            return;
        }

        List<UUID> members = team.getMembers();
        boolean found = false;
        for (UUID memberuuid : members) {
            if (memberuuid.equals(player.getUniqueId())) {
                found = true;
                break;
            }
        }

        if (!found) {
            sender.sendMessage(STR."\{prefix}The player is not part of this team.");
            return;
        }


        dbManager.removeMemberFromTeam(team.getId(), player.getUniqueId());
        sender.sendMessage(STR."\{prefix}Player removed from the team.");
    }

    private void handleChat(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(STR."\{prefix}This command can only be used by players.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(STR."\{prefix}Usage: /team chat <message>");
            return;
        }

        Team team = dbManager.getTeamByUUID(player.getUniqueId());
        if (team == null) {
            player.sendMessage(STR."\{prefix}§4§lError #2301BA §7- please contact the Staff Team via the ticket support in the Discord immediately!");
            return;
        }
        List<UUID> members = team.getMembers();
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        for (UUID member : members) {
            Player memberPlayer = Bukkit.getServer().getPlayer(member);
            if (memberPlayer != null) {
                memberPlayer.sendMessage(STR."§cTeam Chat §8|§7 \{sender.getName()} §7>>§f \{message}");
            }
        }
    }
}
