package me.nanigans.pandorakoth.Koth.Inventories;

import me.nanigans.pandorakoth.Koth.Utility.ItemUtils;
import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KothDataInv extends NavigatorInventory implements Listener {

    private final Map<String, Methods> methods = new HashMap<String, Methods>(){{
        put("openRewards", KothDataInv.this::openRewards);
        put("toggleEnabled", KothDataInv.this::toggleEnabled);
        put("setTime", KothDataInv.this::setTime);
        put("back", KothDataInv.this::back);
    }};

    private final String kothEventName;

    public KothDataInv(Player player, YamlGenerator yaml, String kothEventName) {
        super(player, yaml, kothEventName);
        this.kothEventName = kothEventName;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        handleClick(event);
    }

    @Override
    protected void execute(String method, ItemStack item) {
        if(methods.containsKey(method))
            methods.get(method).execute(item);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event){
        if(event.getInventory().equals(this.inventory) && !isSwitching){
            HandlerList.unregisterAll(this);
        }
    }

    private void openRewards(ItemStack item){
        HandlerList.unregisterAll(this);
        swapInventories(new KothRewardsInv(player, yaml, kothName));
    }

    private void toggleEnabled(ItemStack item){

        if(KothEditorInv.timeIsEnabled(yaml.getData(), kothName).endsWith("14")){
            inventory.setItem(13, ItemUtils.createItem("160/5", ChatColor.GREEN+"Enabled", "METHOD~toggleEnabled"));
            yaml.getData().set(kothName+".enabled", true);
        }else{
            inventory.setItem(13, ItemUtils.createItem("160/14", ChatColor.RED+"Disabled", "METHOD~toggleEnabled"));
            yaml.getData().set(kothName+".enabled", false);
        }
        yaml.save();

    }

    private void setTime(ItemStack item){
        HandlerList.unregisterAll(this);
        swapInventories(new KothTimeSetInv(player, yaml, kothName, kothEventName));

    }

    @Override
    protected void back(ItemStack ignored) {
        HandlerList.unregisterAll(this);
        swapInventories(new KothEditorInv(yaml, player, kothName));
    }

    @Override
    protected Inventory createInventory() {

        final Inventory inv = Bukkit.createInventory(player, 36, "Event Data");

        inv.setItem(11, ItemUtils.createItem(Material.BOOK_AND_QUILL, "Rewards", "METHOD~openRewards"));
        inv.setItem(13, ItemUtils.createItem(KothEditorInv.timeIsEnabled(yaml.getData(), kothName), "Toggle Event", "METHOD~toggleEnabled"));
        inv.setItem(15, ItemUtils.createItem(Material.WATCH, "Set Time", "METHOD~setTime"));
        inv.setItem(22, ItemUtils.createItem(Material.COMPASS, "Back", "METHOD~back"));
        if(yaml.isNew()){
            yaml.getData().set(kothEventName+".enabled", false);
            yaml.save();
        }

        return inv;
    }
}
