package de.janoschbl.teamsmp.listeners;

import de.janoschbl.teamsmp.Main;
import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;
import java.util.UUID;

public class PlayerRespawn implements Listener {

    private final MongoDBManager dbManager;
    private final String prefix = Main.getProvidingPlugin(Main.class).getConfig().getString("server.settings.prefix");

    public PlayerRespawn(MongoDBManager dbManager) {
        this.dbManager = dbManager;

    }

    @EventHandler
    public void OnPlayerRespawn(PlayerRespawnEvent event)  {
        Player player = event.getPlayer();
        Team team = dbManager.getTeamByUUID(player.getUniqueId());
        if (team == null) {
            player.sendMessage(STR."\{prefix}§4§lError #2301BA §7- please contact the Staff Team via the ticket support in the Discord immediately!");
            return;
        }
        Integer hearts = team.getHearts();
        player.setHealthScale(hearts * 2);
        player.setHealth(hearts * 2);
        List<UUID> members = team.getMembers();
        for (UUID member : members) {
            Player memberPlayer = Bukkit.getServer().getPlayer(member);
            if (memberPlayer != null) {
                memberPlayer.setHealthScale(hearts * 2);
                memberPlayer.setHealth(hearts * 2);
            }
        }
    }
}
