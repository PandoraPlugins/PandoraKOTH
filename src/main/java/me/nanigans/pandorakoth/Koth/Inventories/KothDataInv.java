package me.nanigans.pandorakoth.Koth.Inventories;

import me.nanigans.pandorakoth.Koth.Utility.ItemUtils;
import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KothDataInv extends NavigatorInventory implements Listener {

    private final Map<String, Methods> methodsMap = new HashMap<String, Methods>(){{
        put("openRewards", KothDataInv.this::openRewards);
        put("toggleEnabled", KothDataInv.this::toggleEnabled);
        put("setTime", KothDataInv.this::setTime);

    }};

    public KothDataInv(Player player, YamlGenerator yaml, String kothName) {
        super(player, yaml, kothName);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){



    }

    private void openRewards(ItemStack item){

    }
    private void toggleEnabled(ItemStack item){

    }
    private void setTime(ItemStack item){

    }

    @Override
    protected void back() {
        HandlerList.unregisterAll(this);
        swapInventories(new KothEditorInv(yaml, player, kothName));
    }

    @Override
    protected Inventory createInventory() {

        final Inventory inv = Bukkit.createInventory(player, 36, "Event Data");

        inv.setItem(11, ItemUtils.createItem(Material.BOOK_AND_QUILL, "Rewards", "METHOD~openRewards"));
        inv.setItem(13, ItemUtils.createItem(KothEditorInv.timeIsEnabled(yaml.getData(), kothName), "Toggle Enabled", "METHOD~toggleEnabled"));
        inv.setItem(15, ItemUtils.createItem(Material.WATCH, "Set Time", "METHOD~setTime"));
        inv.setItem(22, ItemUtils.createItem(Material.COMPASS, "Back", "METHOD~back"));

        return inv;
    }
}
