package de.janoschbl.teamsmp.commands;

import de.janoschbl.teamsmp.Main;
import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TeamChatCommand implements CommandExecutor {

    private final MongoDBManager dbManager;

    public TeamChatCommand(MongoDBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String prefix = Main.getProvidingPlugin(Main.class).getConfig().getString("server.settings.prefix");

        if (!(sender instanceof Player player)) {
            sender.sendMessage(STR."\{prefix}This command can only be used by players.");
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(STR."\{prefix}Usage: /teamchat <message> or /tc <message>");
            return false;
        }

        Team team = dbManager.getTeamByUUID(player.getUniqueId());
        if (team == null) {
            player.sendMessage(STR."\{prefix}§4§lError #2301BA §7- please contact the Staff Team via the ticket support in the Discord immediately!");
            return false;
        }
        List<UUID> members = team.getMembers();
        String message = String.join(" ", Arrays.copyOfRange(args, 0, args.length));

        for (UUID member : members) {
            Player memberPlayer = Bukkit.getServer().getPlayer(member);
            if (memberPlayer != null) {
                memberPlayer.sendMessage(STR."§cTeam Chat §8|§7 \{sender.getName()} §7>>§f \{message}");
            }
        }
        return false;
    }
}
