package me.nanigans.pandorakoth.Koth.Inventories;

import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KothTimeSetInv extends NavigatorInventory implements Listener {
    private final String kothEventName;
    public KothTimeSetInv(Player player, YamlGenerator yaml, String kothName, String kothEventName) {
        super(player, yaml, kothName);
        this.kothEventName = kothEventName;
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event){
        if(event.getInventory().equals(this.inventory) && !isSwitching){
            HandlerList.unregisterAll(this);
        }
    }

    @Override
    protected void execute(String method, ItemStack item) {

    }

    @Override
    protected void back(ItemStack ignored) {

    }

    @Override
    protected Inventory createInventory() {
        return null;
    }
}
