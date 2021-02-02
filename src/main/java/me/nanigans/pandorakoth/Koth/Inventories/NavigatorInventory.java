package me.nanigans.pandorakoth.Koth.Inventories;

import me.nanigans.pandorakoth.PandoraKOTH;
import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@FunctionalInterface
interface Methods{
    void execute(ItemStack itemClicked);
}

public abstract class NavigatorInventory implements Listener {

    protected final Player player;
    protected final YamlGenerator yaml;
    protected final String kothName;
    private boolean isSwitching = false;
    protected final static PandoraKOTH plugin = PandoraKOTH.getPlugin(PandoraKOTH.class);
    protected Inventory inventory;

    public NavigatorInventory(Player player, YamlGenerator yaml, String kothName){
        this.player = player;
        this.yaml = yaml;
        this.kothName = kothName;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event){
        if(event.getInventory().equals(this.inventory) && !isSwitching){
            HandlerList.unregisterAll(this);
        }
    }

    protected void execute(Map<String, Methods> methods, String method, ItemStack item){
        if(methods.containsKey(method))
            methods.get(method).execute(item);
    }

    protected abstract void back();

    protected void swapInventories(NavigatorInventory inv){
        final Inventory inventory = inv.createInventory();
        isSwitching = true;
        this.inventory = inventory;
        player.openInventory(inventory);
        isSwitching = false;
    }

    protected abstract Inventory createInventory();

}
