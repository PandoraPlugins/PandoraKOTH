package me.nanigans.pandorakoth.Koth.Inventories;

import me.nanigans.pandorakoth.Utils.YamlGenerator;
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

public class MessageInv extends NavigatorInventory implements Listener {
    private final String kothEventName;

    private final Map<String, Methods> methods = new HashMap<String, Methods>(){{
        put("back", MessageInv.this::back);
    }};

    public MessageInv(Player player, YamlGenerator yaml, String kothName) {
        super(player, yaml, kothName);
        this.kothEventName = kothName;
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

    @Override
    protected void back(ItemStack ignored) {

    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event){
        if(event.getInventory().equals(this.inventory) && !isSwitching){
            HandlerList.unregisterAll(this);
        }
    }

    @Override
    protected Inventory createInventory() {
        return null;
    }
}
