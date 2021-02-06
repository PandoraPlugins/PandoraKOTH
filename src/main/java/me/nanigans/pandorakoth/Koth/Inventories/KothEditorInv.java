package me.nanigans.pandorakoth.Koth.Inventories;


import me.nanigans.pandorakoth.Koth.Data.KothEvent;
import me.nanigans.pandorakoth.Koth.Utility.ItemUtils;
import me.nanigans.pandorakoth.Koth.Utility.NBTData;
import me.nanigans.pandorakoth.Utils.AwaitInput;
import me.nanigans.pandorakoth.Utils.Title;
import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

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
        super(player, yaml, kothArea.split("_")[0]);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        swapInventories(this);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event){
        final ItemStack itemClicked = handleClick(event);

        if(itemClicked != null && event.getAction().toString().contains("DROP")){
            if(NBTData.containsNBT(itemClicked, "isDeletable")){
                this.event.delete();
                this.event = null;
                this.inventory.removeItem(itemClicked);
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event){
        handleInvClose(event);
    }

    private void openKoth(ItemStack item){

        if(event != null)
        event.saveEvent();
        final String kothName = item.getItemMeta().getDisplayName();
        if(KothEvent.getEvents().containsKey(kothName))
            this.event = KothEvent.getEvents().get(kothName);
        else this.event = new KothEvent(kothName, yaml);
        HandlerList.unregisterAll(this);
        swapInventories(new KothDataInv(player, yaml, kothName));

    }

    private void createKothTime(ItemStack item){

        if(event != null)
            event.saveEvent();
        isSwitching = true;
        player.closeInventory();
        new Title().send(player, ChatColor.GOLD+"Input Event Name", "10 seconds", 5, 50, 10);
        new AwaitInput(player, 10000, name -> {
            if(name != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event = new KothEvent(name, yaml);
                        HandlerList.unregisterAll(KothEditorInv.this);
                        swapInventories(new KothDataInv(player, yaml, name));
                    }
                }.runTask(plugin);
            }else swapInventories(this);
        }).runTaskAsynchronously(plugin);
    }

    @Override
    protected void execute(String method, ItemStack item){
        if(methods.containsKey(method))
            methods.get(method).execute(item);
    }

    @Override
    protected void back(ItemStack ignored) {
    }

    @Override
    protected Inventory createInventory(){

        final FileConfiguration data = yaml.getData();
        final Set<String> keys = data.getKeys(false);

        final boolean canDelete = player.hasPermission("Koth.Delete");
        final int size = Math.min(Math.min(keys.size() - (keys.size() % 9), 36) + 18, 54);
        Inventory inv = Bukkit.createInventory(player, size, "Koths");
        short inx = 0;
        for (String key : keys) {
            if(inx <= 45) {
                ItemStack item = ItemUtils.createItem(timeIsEnabled(data, key), key, "METHOD~openKoth");
                if (canDelete) {
                    item = NBTData.setNBT(item, "isDeletable~true");
                    final ItemMeta meta = item.getItemMeta();
                    meta.setLore(Collections.singletonList("Press Q to delete this KOTH event"));
                    item.setItemMeta(meta);
                }
                inv.setItem(inx, item);
                inx++;
            }else break;
        }

        final ItemStack newTime = ItemUtils.createItem("160/13", "Create New Event", "METHOD~createKothTime");
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
