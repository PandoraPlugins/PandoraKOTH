package me.nanigans.pandorakoth.Koth.Inventories;

import me.nanigans.pandorakoth.Koth.Utility.ItemUtils;
import me.nanigans.pandorakoth.Koth.Utility.NBTData;
import me.nanigans.pandorakoth.Utils.AwaitInput;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KothRewardsInv extends NavigatorInventory implements Listener {

    private final Map<String, Methods> methods = new HashMap<String, Methods>(){{
        put("back", KothRewardsInv.this::back);
        put("addReward", KothRewardsInv.this::addReward);
    }};

    public KothRewardsInv(Player player, YamlGenerator yaml, String kothName) {
        super(player, yaml, kothName);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        final ItemStack itemClicked = handleClick(event);
        if(itemClicked != null && event.getAction().toString().contains("DROP")){

            if(NBTData.containsNBT(itemClicked, "isDeletable")){

                final List<String> stringList = yaml.getData().getStringList(kothName + ".rewards");
                stringList.remove(itemClicked.getItemMeta().getDisplayName());
                yaml.getData().set(kothName+".rewards", stringList);
                yaml.save();
                this.inventory.removeItem(itemClicked);

            }

        }
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
        HandlerList.unregisterAll(this);
        swapInventories(new KothDataInv(player, yaml, kothName));
    }

    protected void addReward(ItemStack ignored){

        new Title().send(player, ChatColor.GOLD+"Input the command without a '/'", "Replace the player's name with <player>", 10, 40, 10);
        player.sendMessage(ChatColor.GOLD+"Input the command without a '/'");
        player.sendMessage(ChatColor.WHITE+"Replace the player's name with <player>");
        isSwitching = true;
        player.closeInventory();

        new AwaitInput(player, 20000, msg -> {

            if(msg != null) {
                final List<String> rewards = yaml.getData().getStringList(kothName + ".rewards");
                rewards.add(msg);
                yaml.getData().set(kothName + ".rewards", rewards);
                yaml.save();
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    HandlerList.unregisterAll(KothRewardsInv.this);
                    swapInventories(new KothRewardsInv(player, yaml, kothName));
                }
            }.runTask(plugin);
        }).runTaskAsynchronously(plugin);

    }

    @Override
    protected Inventory createInventory() {

        final List<String> stringList = yaml.getData().getStringList(kothName + "." + "rewards");
        final int size = calcInvSize(stringList.size());
        final Inventory inventory = Bukkit.createInventory(player, size, "Rewards");
        final int loopSize = Math.min(45, stringList.size());
        for (int i = 0; i < loopSize; i++) {
            final String cmd = stringList.get(i);
            final ItemStack item = ItemUtils.createItem(Material.PAPER, cmd, "isDeletable~true");
            final ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setLore(Collections.singletonList("Press Q to delete this reward"));
            item.setItemMeta(itemMeta);
            inventory.setItem(i, item);
        }

        inventory.setItem(inventory.getSize()-9, ItemUtils.createItem(Material.COMPASS, "Back", "METHOD~back"));
        inventory.setItem(inventory.getSize()-5, ItemUtils.createItem("160/5", "Add Reward", "METHOD~addReward"));

        return inventory;
    }
}
