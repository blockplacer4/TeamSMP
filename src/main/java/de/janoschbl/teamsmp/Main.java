package de.janoschbl.teamsmp;

import de.janoschbl.teamsmp.MongoAddon.Config;
import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import de.janoschbl.teamsmp.TabCompletion.AdminTabComplete;
import de.janoschbl.teamsmp.commands.AdminSetttingsCommand;
import de.janoschbl.teamsmp.commands.TeamChatCommand;
import de.janoschbl.teamsmp.commands.TeamCommand;
import de.janoschbl.teamsmp.TabCompletion.TeamTabComplete;
import de.janoschbl.teamsmp.listeners.*;
import de.janoschbl.teamsmp.tools.UpdateHearts;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class Main extends JavaPlugin {

    public static final Map<String, ChatColor> teamColors = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup

        teamColors.put("Green", ChatColor.GREEN);
        teamColors.put("DarkGreen", ChatColor.DARK_GREEN);
        teamColors.put("Red", ChatColor.RED);
        teamColors.put("Blue", ChatColor.BLUE);
        teamColors.put("Aqua", ChatColor.AQUA);
        teamColors.put("DarkAqua", ChatColor.DARK_AQUA);
        teamColors.put("Yellow", ChatColor.YELLOW);
        teamColors.put("Gold", ChatColor.GOLD);

        this.saveDefaultConfig();

        LuckPerms luckPerms = LuckPermsProvider.get();

        if (this.getConfig().getString("server.settings.prefix") == null) {
            this.getConfig().set("server.settings.prefix", "§c§lRotstein >> §7");
            this.saveConfig();
        }

        if (this.getConfig().getString("server.settings.mongodb") == null) {
            this.getConfig().set("server.settings.mongodb", "mongodb-url");
            this.saveConfig();
        }

        MongoDBManager dbManager = new MongoDBManager(this.getConfig().getString("server.settings.mongodb"), "Data");
        UpdateHearts upHearts = new UpdateHearts();

        if (dbManager.getConfig() == null) {
            Config config = new Config(1000, 3000 ,1000);
            dbManager.addConfig(config);
        }

        getCommand("team").setExecutor(new TeamCommand(dbManager, luckPerms));
        getCommand("team").setTabCompleter(new TeamTabComplete(dbManager));
        getCommand("teamchat").setExecutor(new TeamChatCommand(dbManager));

        getCommand("admin").setExecutor(new AdminSetttingsCommand(dbManager, this));
        getCommand("admin").setTabCompleter(new AdminTabComplete());




        Bukkit.getPluginManager().registerEvents(new PlayerDeath(dbManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(dbManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawn(dbManager), this);
        Bukkit.getPluginManager().registerEvents(new TeamFriendlyFire(dbManager), this);
        Bukkit.getPluginManager().registerEvents(new HeartItemClick(this, dbManager, new UpdateHearts()), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> teamList = dbManager.getAllTeams();
                for (String teamName : teamList) {
                    Team team = dbManager.getTeamByName(teamName);
                    upHearts.updateHearts(team);
                }
            }
        }.runTaskTimer(this, 0L, 20L);

        getLogger().info("TeamSMP enabled.");
    }


    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
