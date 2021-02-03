package me.nanigans.pandorakoth.Koth.Inventories;

import me.nanigans.pandorakoth.Koth.Utility.NBTData;
import me.nanigans.pandorakoth.PandoraKOTH;
import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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

    public NavigatorInventory(Player player, YamlGenerator yaml, String kothName){
        this.player = player;
        this.yaml = yaml;
        this.kothName = kothName;
    }

    protected void handleClick(InventoryClickEvent event){
        if(event.getInventory().equals(this.inventory)){
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
            final ItemStack item = event.getCurrentItem();
            if(item != null){
                if (NBTData.containsNBT(item, "METHOD")) {
                    final String method = NBTData.getNBT(item, "METHOD");
                    execute(method, item);
                }
            }

        }
    }

    protected abstract void execute(String method, ItemStack item);

    protected abstract void back(ItemStack ignored);

    protected void swapInventories(NavigatorInventory inv){
        isSwitching = true;
        final Inventory inventory = inv.createInventory();
        inv.inventory = inventory;
        player.openInventory(inventory);
        isSwitching = false;
    }

    protected abstract Inventory createInventory();

}
