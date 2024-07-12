package de.janoschbl.teamsmp.listeners;

import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.UUID;

public class PlayerDeath implements Listener {

    private final MongoDBManager dbManager;

    public PlayerDeath(MongoDBManager dbManager) {
        this.dbManager = dbManager;
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Team team = dbManager.getTeamByUUID(player.getUniqueId());
        System.out.println(team.getName());
        System.out.println(player.getUniqueId());
        System.out.println(team.getMembers());
        if (team != null) {
            if (team.getHearts() == 1) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + player.getName() + " Du hast keine Herzen mehr ( Cringe )");
                return;
            }
            dbManager.removeHeartFromTeam(team.getId());
            List<UUID> members = team.getMembers();
            Integer hearts = team.getHearts();
            for (UUID member : members) {
                Player memberPlayer = Bukkit.getServer().getPlayer(member);
                if (memberPlayer != null) {
                    memberPlayer.sendMessage("§c§lRotstein >> §7Euer Team hat §c1 Herz §7verloren | §9" + player.getName() + " §7ist gestorben");
                    memberPlayer.setHealthScale(hearts * 2);
                    if (memberPlayer.getHealth() < hearts) {
                        return;
                    }
                    memberPlayer.setHealth(hearts);
                }
            }
        }
    }
}
