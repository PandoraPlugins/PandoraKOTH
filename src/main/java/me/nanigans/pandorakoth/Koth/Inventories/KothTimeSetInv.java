package me.nanigans.pandorakoth.Koth.Inventories;

import me.nanigans.pandorakoth.Koth.Utility.ItemUtils;
import me.nanigans.pandorakoth.Utils.AwaitInput;
import me.nanigans.pandorakoth.Utils.DateParser;
import me.nanigans.pandorakoth.Utils.Title;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class KothTimeSetInv extends NavigatorInventory implements Listener {
    private final String kothEventName;

    private final Map<String, Methods> methods = new HashMap<String, Methods>(){{
        put("scheduleTime", KothTimeSetInv.this::scheduleTime);
        put("setCapDuration", KothTimeSetInv.this::setCapDuration);
        put("back", KothTimeSetInv.this::back);
    }};

    public KothTimeSetInv(Player player, YamlGenerator yaml, String kothName, String kothEventName) {
        super(player, yaml, kothName);
        this.kothEventName = kothEventName;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event){
        if(event.getInventory().equals(this.inventory) && !isSwitching){
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event){
        handleClick(event);
    }

    private void scheduleTime(ItemStack ignored){

        new Title().send(player, ChatColor.GOLD+"Input a time to repeat", "20 seconds", 10, 40, 10);
        input("scheduleTime");

    }

    private void setCapDuration(ItemStack ignored){
        new Title().send(player, ChatColor.GOLD+"Input a duration time", "20 seconds", 10, 40, 10);
        input("capDuration");
    }


    private void input(String path){

        isSwitching = true;
        player.closeInventory();

        new AwaitInput(player, 20000, msg -> {

            if(msg != null) {
                try {
                    final long time = DateParser.parseDateDiff(msg, true) - System.currentTimeMillis();
                    yaml.getData().set(kothEventName+".times."+path, time);
                    yaml.save();
                    openInv();
                } catch (Exception e) {
                    isSwitching = false;
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "Invalid Input Time");
                }
            }else openInv();

        }).runTaskAsynchronously(plugin);

    }

    private void openInv(){
        new BukkitRunnable() {
            @Override
            public void run() {
                HandlerList.unregisterAll(KothTimeSetInv.this);
                swapInventories(new KothTimeSetInv(player, yaml, kothName, kothEventName));
            }
        }.runTask(plugin);
    }

    @Override
    protected void execute(String method, ItemStack item) {
        if(methods.containsKey(method))
            methods.get(method).execute(item);

    }

    @Override
    protected void back(ItemStack ignored) {
        HandlerList.unregisterAll(this);
        swapInventories(new KothDataInv(player, yaml, kothName));
    }

    @Override
    protected Inventory createInventory() {

        final Inventory inv = Bukkit.createInventory(player, 18, "Event Time");

        final ItemStack captureDuration = ItemUtils.createItem(Material.REDSTONE_TORCH_ON, "Capture Duration", "METHOD~setCapDuration");
        final long capDurationMilli = yaml.getData().getLong(kothEventName + ".times.capDuration");
        if(capDurationMilli != 0) {
            final String date = DateParser.formatDateDiff(capDurationMilli+System.currentTimeMillis());
            ItemUtils.setLore(captureDuration, date);
        }
        inv.setItem(3, captureDuration);

        final ItemStack scheduleEventTime = ItemUtils.createItem(Material.WATCH, "Schedule Event Time", "METHOD~scheduleTime");
        final long aLong = yaml.getData().getLong(kothEventName + ".times.scheduleTime");
        if(aLong != 0){
            ItemUtils.setLore(scheduleEventTime, DateParser.formatDateDiff(aLong+System.currentTimeMillis()));
        }
        inv.setItem(5, scheduleEventTime);
        inv.setItem(13, ItemUtils.createItem(Material.COMPASS, "Back", "METHOD~back"));
        return inv;
    }
}
