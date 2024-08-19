package de.janoschbl.teamsmp.listeners;

import de.janoschbl.teamsmp.Main;
import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.net.http.WebSocket;
import java.util.List;
import java.util.UUID;

public class TeamFriendlyFire implements Listener {

    private final MongoDBManager dbManager;
    private final String prefix = Main.getProvidingPlugin(Main.class).getConfig().getString("server.settings.prefix");

    public TeamFriendlyFire(MongoDBManager dbManager) {
        this.dbManager = dbManager;
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageByEntityEvent event) {
        Player betroffener = (Player) event.getEntity();
        Player angreifer = (Player) event.getDamager();
        if (areInSameTeam(betroffener.getUniqueId(), angreifer.getUniqueId())) {
            event.setCancelled(true);
        }
    }


    private boolean areInSameTeam(UUID player1, UUID player2) {
        Team team = dbManager.getTeamByUUID(player1);
        if (team == null) {
            Player player = Bukkit.getPlayer(player1);
            assert player != null;
            player.sendMessage(STR."\{prefix}§4§lError #2301BA §7- please contact the Staff Team via the ticket support in the Discord immediately!");
            return false;
        }
        List<UUID> members = team.getMembers();
        return members.contains(player2);
    }
}
