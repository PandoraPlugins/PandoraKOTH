package me.nanigans.pandorakoth.Koth.Inventories;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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
        put("setEventDuration", KothTimeSetInv.this::setEventDuration);
        put("back", KothTimeSetInv.this::back);
    }};

    public KothTimeSetInv(Player player, YamlGenerator yaml, String kothName, String kothEventName, ProtectedRegion region) {
        super(player, yaml, kothName, region);
        this.kothEventName = kothEventName;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event){
        handleInvClose(event);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event){
        handleClick(event);
    }

    private void scheduleTime(ItemStack ignored){

        new Title().send(player, ChatColor.GOLD+"Input a time to repeat", "20 seconds", 10, 40, 10);
        input("scheduleTime");

    }

    private void setEventDuration(ItemStack ignored){

        new Title().send(player, ChatColor.GOLD+"Input event duration", "20 seconds", 10, 40, 10);
        input("eventDuration");

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
                    switch (path) {
                        case "scheduleTime": event.getScheduling().setScheduleTime(time);
                        break;
                        case "capDuration": event.getScheduling().setCapDuration(time);
                        break;
                        case "eventDuration": event.getScheduling().setEventDuration(time);
                    }
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
                swapInventories(new KothTimeSetInv(player, yaml, kothName, kothEventName, region));
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
        swapInventories(new KothDataInv(player, yaml, kothName, region));
    }

    @Override
    protected Inventory createInventory() {

        final Inventory inv = Bukkit.createInventory(player, 18, "Event Time");

        final ItemStack captureDuration = ItemUtils.createItem(Material.REDSTONE_TORCH_ON, "Capture Duration", "METHOD~setCapDuration");
        final long capDurationMilli = event.getScheduling().getCapDuration();
        if(capDurationMilli != 0) {
            final String date = DateParser.formatDateDiff(capDurationMilli+System.currentTimeMillis());
            ItemUtils.setLore(captureDuration, date);
        }

        final ItemStack scheduleEventTime = ItemUtils.createItem(Material.WATCH, "Schedule Event Time", "METHOD~scheduleTime");
        final long aLong = event.getScheduling().getScheduleTime();
        if(aLong != 0){
            ItemUtils.setLore(scheduleEventTime, DateParser.formatDateDiff(aLong+System.currentTimeMillis()));
        }

        final ItemStack eventDurationTime = ItemUtils.createItem(Material.ARROW, "Event Duration", "METHOD~setEventDuration");
        final long eventDur = event.getScheduling().getEventDuration();
        if(eventDur != 0){
            ItemUtils.setLore(eventDurationTime, DateParser.formatDateDiff(eventDur+System.currentTimeMillis()));
        }

        inv.setItem(2, captureDuration);
        inv.setItem(4, eventDurationTime);
        inv.setItem(6, scheduleEventTime);
        inv.setItem(13, ItemUtils.createItem(Material.COMPASS, "Back", "METHOD~back"));
        return inv;
    }
}
