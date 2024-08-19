package de.janoschbl.teamsmp.listeners;

import de.janoschbl.teamsmp.Main;
import de.janoschbl.teamsmp.MongoAddon.MongoDBManager;
import de.janoschbl.teamsmp.MongoAddon.Team;
import de.janoschbl.teamsmp.tools.UpdateHearts;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.sound.midi.SysexMessage;

public class HeartItemClick implements Listener {

    private final NamespacedKey customKey;
    private final String prefix = Main.getProvidingPlugin(Main.class).getConfig().getString("server.settings.prefix");
    private final MongoDBManager dbManager;
    private final UpdateHearts updateHearts;

    public HeartItemClick(Main plugin, MongoDBManager dbManager, UpdateHearts updateHearts) {
        this.customKey = new NamespacedKey(plugin, "uniqueID");
        this.dbManager = dbManager;
        this.updateHearts = updateHearts;
    }

    @EventHandler
    public void OnItemClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) return;

        if (item != null && OraxenItems.getIdByItem(item) != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if (data.has(customKey, PersistentDataType.STRING)) {
                    String uniqueID = data.get(customKey, PersistentDataType.STRING);
                    if ("selteneGeheimeID2104&2807LJ".equals(uniqueID)) {
                        Team team = dbManager.getTeamByUUID(player.getUniqueId());
                        if (team == null) {
                            player.sendMessage(STR."\{prefix}§4§lError #2301BA §7- please contact the Staff Team via the ticket support in the Discord immediately!");
                            return;
                        }
                        item.setAmount(item.getAmount() - 1);
                        player.sendMessage(STR."\{prefix}Du hast ein Herz für dein Team eingelöst");
                        dbManager.addHeartToTeam(team.getId());
                        updateHearts.updateHearts(team);
                    }
                }
            }
        }
    }

}
