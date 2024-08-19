package de.janoschbl.teamsmp.tools;

import de.janoschbl.teamsmp.Main;
import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class UpdateHearts {
    public void updateHearts(Team team) {
        Integer hearts = team.getHearts();
        List<UUID> members = team.getMembers();
        for (UUID member : members) {
            Player entity = Bukkit.getServer().getPlayer(member);
            if (entity != null) {
                entity.setHealthScale(hearts * 2);
            }
        }
    }
}


