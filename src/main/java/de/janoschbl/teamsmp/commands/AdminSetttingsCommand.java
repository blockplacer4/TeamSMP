package de.janoschbl.teamsmp.commands;

import io.th0rgal.oraxen.api.OraxenItems;
import de.janoschbl.teamsmp.Main;
import de.janoschbl.teamsmp.MongoAddon.Config;
import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class AdminSetttingsCommand implements CommandExecutor {

    private final MongoDBManager dbManager;
    private final String prefix = Main.getProvidingPlugin(Main.class).getConfig().getString("server.settings.prefix");
    private final NamespacedKey customKey;


    public AdminSetttingsCommand(MongoDBManager dbManager, Main plugin) {
        this.dbManager = dbManager;
        this.customKey = new NamespacedKey(plugin, "uniqueID");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String prefix = Main.getProvidingPlugin(Main.class).getConfig().getString("server.settings.prefix");

        if (!sender.hasPermission("admin.use")) {
            sender.sendMessage(STR."\{prefix}Dieser Befehl existiert nicht");
        }

        if (args.length == 0 ) {
            sender.sendMessage(STR."\{prefix}Usage: /admin <setup|start>");
            return false;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "start":
                handleStart(sender);
                Config config = dbManager.getConfig();
                int members = dbManager.getAllMembers().size();
                int blocksPerPlayer = config.getBlockPerPlayer();
                sender.sendMessage(STR."\{prefix}Season Start - §9\{members} Spieler §7- §9\{members * blocksPerPlayer} Blöcke");
                break;

            case "setup":
                handleSetup(sender, args);
                break;

            case "heart":
                handeGiveHeartItem(sender);
                break;

            default:
                sender.sendMessage(STR."\{prefix}Usage: /admin <setup|start>");
                break;
        }

        return false;
    }

    private void handleStart(CommandSender sender) {

        Set<UUID> members = dbManager.getAllMembers();
        Config config = dbManager.getConfig();
        Integer blocksPerPlayer = config.getBlockPerPlayer();

        Integer playerCount = Math.toIntExact(members.size());
        for (UUID member : members) {
            Player memberPlayer = Bukkit.getServer().getPlayer(member);
            assert memberPlayer != null;
            memberPlayer.setWhitelisted(true);
        }

        dbManager.setWorldBoarderCounter(playerCount * blocksPerPlayer);

        WorldBorder wb = Objects.requireNonNull(Bukkit.getWorld("world")).getWorldBorder();
        wb.setCenter(0,0 );
        wb.setSize(playerCount * blocksPerPlayer);

        Bukkit.reloadWhitelist();
    }

    private void handleSetup(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(STR."\{prefix}Usage: /admin setup <blocksPerPlayer|blocksPerPlayerDeath> <integer>");
            return;
        }

        if (args[1].equalsIgnoreCase("BlocksPerPlayer")) {
            dbManager.setBlockPerPlayer(Integer.valueOf(args[2]));
            sender.sendMessage(STR."\{prefix}Die §9BlocksPerPlayer §7wurden auf §9\{args[2]} Blöcke §7gesetzt!");
            return;
        }
        if (args[1].equalsIgnoreCase("BlocksPerPlayerDeath")) {
            dbManager.setBlockPerPlayerDeath(Integer.valueOf(args[2]));
            sender.sendMessage(STR."\{prefix}Die §9BlocksPerPlayerDeath §7wurden auf §9\{args[2]} Blöcke §7gesetzt!");
        }
    }

    private void handeGiveHeartItem(CommandSender sender) {
        if (sender instanceof Player player) {

            if (OraxenItems.getItemById("custom_heart").build() == null ) {
                sender.sendMessage(STR."\{prefix}Es gab ein Fehler mit Oraxan... D:");
            }

            ItemStack netherStar = OraxenItems.getItemById("custom_heart").build();
            ItemMeta meta = netherStar.getItemMeta();

            assert meta != null;
            meta.setDisplayName("§cEinzigartiges Herz");
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(customKey, PersistentDataType.STRING, "selteneGeheimeID2104&2807LJ");


            netherStar.setItemMeta(meta);

            player.getInventory().addItem(netherStar);
            player.sendMessage(STR."\{prefix}Du hast ein Herz als Admin erhalten");
        }
    }

}
