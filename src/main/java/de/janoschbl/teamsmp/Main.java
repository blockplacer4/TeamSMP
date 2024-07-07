package de.janoschbl.teamsmp;

import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import de.janoschbl.teamsmp.commands.TeamCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;


public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        MongoDBManager dbManager = new MongoDBManager("mongodb+srv://blockplacer4:IzOBxFOwJMbE9DAg@cluster0.iczqghu.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0", "Data");

        getCommand("team").setExecutor(new TeamCommand(dbManager));

        getLogger().info("TeamSMP enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
