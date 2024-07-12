package de.janoschbl.teamsmp;

import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.commands.TeamCommand;
import de.janoschbl.teamsmp.commands.TeamTabComplete;
import de.janoschbl.teamsmp.listeners.PlayerDeath;
import de.janoschbl.teamsmp.listeners.PlayerJoin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
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

        LuckPerms luckPerms = LuckPermsProvider.get();

        MongoDBManager dbManager = new MongoDBManager("mongodb+srv://blockplacer4:IzOBxFOwJMbE9DAg@cluster0.iczqghu.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0&ssl=true&connectTimeoutMS=30000&socketTimeoutMS=30000", "Data");

        getCommand("team").setExecutor(new TeamCommand(dbManager, luckPerms));
        getCommand("team").setTabCompleter(new TeamTabComplete(dbManager));

        Bukkit.getPluginManager().registerEvents(new PlayerDeath(dbManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(dbManager), this);

        getLogger().info("TeamSMP enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
