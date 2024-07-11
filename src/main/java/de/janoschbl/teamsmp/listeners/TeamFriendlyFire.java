package de.janoschbl.teamsmp.listeners;

import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.net.http.WebSocket;
import java.util.List;
import java.util.UUID;

public class TeamFriendlyFire implements WebSocket.Listener {

    private final MongoDBManager dbManager;

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
        List<UUID> members = team.getMembers();
        return members.contains(player2);
    }
}
