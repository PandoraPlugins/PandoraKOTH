package me.nanigans.pandorakoth.Koth.Inventories;


import me.nanigans.pandorakoth.Koth.Utility.ItemUtils;
import me.nanigans.pandorakoth.Koth.Utility.NBTData;
import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class KothEditorInv extends NavigatorInventory implements Listener {
    private final Map<String, Methods> methods = new HashMap<String, Methods>(){{
        put("openKoth", KothEditorInv.this::openKoth);
        put("createKothTime", KothEditorInv.this::createKothTime);
    }};

    public KothEditorInv(YamlGenerator yaml, Player player, String kothArea){
        super(player, yaml, kothArea);
        swapInventories(this);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event){

        if(event.getInventory().equals(this.inventory)){
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
            final ItemStack item = event.getCurrentItem();
            if(item != null){
                if (NBTData.containsNBT(item, "METHOD")) {
                    final String method = NBTData.getNBT(item, "METHOD");
                    execute(methods, method, item);
                }
            }

        }

    }

    private void openKoth(ItemStack item){

        final String kothName = item.getItemMeta().getDisplayName();


    }

    private void createKothTime(ItemStack item){

        final int size = yaml.getData().getKeys(false).size();
        yaml.getData().set(kothName + "_"+ size, new HashMap<>());
        yaml.save();
        HandlerList.unregisterAll(this);
        swapInventories(new KothDataInv(player, yaml, kothName));
    }

    @Override
    protected void back() {
    }

    @Override
    protected Inventory createInventory(){

        final FileConfiguration data = yaml.getData();
        final Set<String> keys = data.getKeys(false);

        final boolean canDelete = player.hasPermission("Koth.Delete");
        final int size = Math.min(Math.min(keys.size() - (keys.size() % 9), 36) + 18, 54);
        Inventory inv = Bukkit.createInventory(player, size, kothName + " Koths");
        short inx = 0;
        for (String key : keys) {
            if(inx <= 45) {
                final ItemStack item = ItemUtils.createItem(timeIsEnabled(data, key), key, "METHOD~openKoth");
                if (canDelete) {
                    final ItemMeta meta = item.getItemMeta();
                    meta.setLore(Collections.singletonList("Press Q to delete this KOTH event"));
                }
                inv.setItem(inx, item);
                inx++;
            }else break;
        }

        final ItemStack newTime = ItemUtils.createItem("160/13", "Create New Time", "METHOD~createKothTime");
        inv.setItem(inv.getSize()-5, newTime);
        return inv;

    }

    public static String timeIsEnabled(FileConfiguration config, String path){
        final boolean aBoolean = config.getBoolean(path + ".enabled");
        if(aBoolean)
            return "160/5";
        else return "160/14";
    }


}
