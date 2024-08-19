package de.janoschbl.teamsmp.listeners;

import de.janoschbl.teamsmp.Main;
import de.janoschbl.teamsmp.MongoAddon.Config;
import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import de.janoschbl.teamsmp.tools.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Bukkit.getLogger;

public class PlayerDeath implements Listener {

    private final MongoDBManager dbManager;

    public PlayerDeath(MongoDBManager dbManager) {
        this.dbManager = dbManager;
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {

        String prefix = Main.getProvidingPlugin(Main.class).getConfig().getString("server.settings.prefix");



        Player player = event.getEntity();
        Team team = dbManager.getTeamByUUID(player.getUniqueId());
        if (team == null) {
            player.sendMessage(STR."\{prefix}§4§lError #2301BA §7- please contact the Staff Team via the ticket support in the Discord immediately!");
            return;
        }
        if (team.getHearts() == 1) {

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), STR."ban \{player.getName()} \{player.getName()}, du hast keine Herzen mehr und bist damit ausgeschieden! Doch keine Sorge, die nächste Season steht schon in den Startlöchern. Halte Ausschau auf unserem Discord-Server, wo du dich bald wieder anmelden kannst. Viel Glück beim nächsten Mal!");

            Config config = dbManager.getConfig();
            Integer blocksPerPlayerDeath = config.getBlockPerPlayerDeath();

            WorldBorder wb = Objects.requireNonNull(Bukkit.getWorld("world")).getWorldBorder();
            Integer wbSize = config.getWorldBorderBlocks();

            int newSize = wbSize - blocksPerPlayerDeath;
            if (newSize == 0) {
                newSize = 1;
            }

            dbManager.setWorldBoarderCounter(newSize);

            wb.setSize(newSize, TimeUnit.DAYS, 1);

            List<Player> listOfPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

            for (Player oneplayer : listOfPlayers) {
                oneplayer.sendMessage(STR."\{prefix}Jemand ist ausgeschieden - Die World Border schrumpft somit um §9\{blocksPerPlayerDeath} Blöcke§7");
            }


            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1273249913165316109/NQDhWSA5I6-0Xr1O8_QTDFTzZvppu5pDGF4dAhjRAHxb3J6_oJy1WxoZ9WXZN9EFbPzB");
            webhook.setUsername("Rotstein Alert");
            webhook.setAvatarUrl("https://pbs.twimg.com/profile_images/1810344661829181440/tgEW_ZKx_400x400.jpg");
            webhook.setContent("@everyone");
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle(STR."Die Worldborder verkleinert sich um **\{config.getBlockPerPlayerDeath()} Blöcke**!")
                    .setDescription(STR."**Achtung, ein weiterer Spieler hat die Welt verlassen!** \\n\\n**Was bedeutet das für euch?** \\nDie Worldborder wird um **\{config.getBlockPerPlayerDeath()}** Blöcke kleiner. \\n\\n🔔 **Wichtiger Hinweis:** Die Verkleinerung der Worldborder erfolgt **über einen Zeitraum von 24 Stunden**. Das bedeutet, dass die Grenze kontinuierlich schrumpfen wird, bis die komplette Reduzierung abgeschlossen ist. Sichert also eure Ausrüstung und Ressourcen rechtzeitig, um Verluste zu vermeiden!")
                    // .setAuthor("Rotstein Alert", "rotsteinsmp.de", "https://i.imgur.com/VJbZixD.png")
                    .setColor(Color.red)
            );
            try {
                webhook.execute();
            }
            catch (java.io.IOException e) {
                getLogger().severe(STR."Failed to execute Discord webhook: \{e.getMessage()}");
                getLogger().severe(Arrays.toString(e.getStackTrace()));
            }

            return;
        }
        dbManager.removeHeartFromTeam(team.getId());
        Integer hearts = team.getHearts();
        List<UUID> members = team.getMembers();
        for (UUID member : members) {
            Player memberPlayer = Bukkit.getServer().getPlayer(member);
            if (memberPlayer != null) {
                memberPlayer.sendMessage(STR."\{prefix}Euer Team hat §c1 Herz §7verloren | §9\{player.getName()} §7ist gestorben");
                memberPlayer.setHealthScale(hearts * 2);
                if (memberPlayer.getHealth() < hearts) {
                    return;
                }
                memberPlayer.setHealth(hearts * 2);
            }
        }
    }
}
