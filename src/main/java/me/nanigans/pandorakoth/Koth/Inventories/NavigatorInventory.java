package me.nanigans.pandorakoth.Koth.Inventories;

import me.nanigans.pandorakoth.Koth.Data.KothEvent;
import me.nanigans.pandorakoth.Koth.Utility.NBTData;
import me.nanigans.pandorakoth.PandoraKOTH;
import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
interface Methods{
    void execute(ItemStack itemClicked);
}

public abstract class NavigatorInventory implements Listener{

    protected final Player player;
    protected final YamlGenerator yaml;
    protected final String kothName;
    protected boolean isSwitching = false;
    protected final static PandoraKOTH plugin = PandoraKOTH.getPlugin(PandoraKOTH.class);
    protected Inventory inventory;
    protected KothEvent event;

    public NavigatorInventory(Player player, YamlGenerator yaml, String kothName){
        this.player = player;
        this.yaml = yaml;
        this.kothName = kothName;
    }

    protected ItemStack handleClick(InventoryClickEvent event){
        if(event.getInventory().equals(this.inventory)){
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
            final ItemStack item = event.getCurrentItem();
            if(item != null){
                if (!event.getAction().toString().contains("DROP") &&  NBTData.containsNBT(item, "METHOD")) {
                    final String method = NBTData.getNBT(item, "METHOD");
                    execute(method, item);
                }
                return item;
            }

        }
        return null;
    }

    protected void handleInvClose(InventoryCloseEvent event){
        if(event.getInventory().equals(this.inventory) && !isSwitching){
            HandlerList.unregisterAll(this);
            this.event.saveEvent();
        }
    }

    protected abstract void execute(String method, ItemStack item);

    protected abstract void back(ItemStack ignored);

    protected void swapInventories(NavigatorInventory inv){
        isSwitching = true;
        inv.event = event;
        final Inventory inventory = inv.createInventory();
        inv.inventory = inventory;
        player.openInventory(inventory);
        isSwitching = false;
    }
    protected int calcInvSize(int input){
        return Math.min(Math.min(input - (input % 9), 36) + 18, 54);
    }

    protected abstract Inventory createInventory();

}
