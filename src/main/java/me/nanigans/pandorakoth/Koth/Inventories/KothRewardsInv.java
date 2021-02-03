package me.nanigans.pandorakoth.Koth.Inventories;

import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KothRewardsInv extends NavigatorInventory implements Listener {

    private final Map<String, Methods> methods = new HashMap<String, Methods>(){{

    }};
    private final String kothEventName;

    public KothRewardsInv(Player player, YamlGenerator yaml, String kothName, String kothEventName) {
        super(player, yaml, kothName);
        this.kothEventName = kothEventName;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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

    @Override
    protected void back(ItemStack ignored) {

    }

    @Override
    protected Inventory createInventory() {
        return null;
    }
}
