package de.janoschbl.teamsmp.listeners;

import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final MongoDBManager dbManager;

    public PlayerJoin(MongoDBManager dbManager) {
        this.dbManager = dbManager;
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Team team = dbManager.getTeamByUUID(player.getUniqueId());
        Integer hearts = team.getHearts();
        player.setHealthScale(hearts * 2);
        player.setHealth(hearts * 2);
    }
}
