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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

enum RewardType{

    SYNCED("Synced", "Synced"),
    CAPPER("For Capturer", "Capturer"),
    OWNER("For Owner", "Owner");
    private String name;
    private String yamlName;
    RewardType(String displayName, String yamlName) {
        this.name = displayName;
        this.yamlName = yamlName;
    }

    public RewardType next(){
        final RewardType[] values = RewardType.values();
        return values[(this.ordinal()+1) % values.length];
    }

    public static RewardType getByYMLName(String name){
        for (RewardType value : RewardType.values()) {
            if(value.yamlName.equals(name)){
                return value;
            }
        }
        return null;
    }

    public String getYamlName() {
        return yamlName;
    }

    public String getName() {
        return name;
    }
}

public class KothRewardsInv extends NavigatorInventory implements Listener {

    private final Map<String, Methods> methods = new HashMap<String, Methods>(){{
        put("back", KothRewardsInv.this::back);
        put("addReward", KothRewardsInv.this::addReward);
        put("swapRewardType", KothRewardsInv.this::swapRewardType);
    }};

    public KothRewardsInv(Player player, YamlGenerator yaml, String kothName) {
        super(player, yaml, kothName);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        final ItemStack itemClicked = handleClick(event);
        final boolean isItemDropped = event.getAction().toString().contains("DROP");
        if(itemClicked != null){

            if(isItemDropped && NBTData.containsNBT(itemClicked, "isDeletable")){
                removeReward(itemClicked);
            }else if(event.getClick() == ClickType.LEFT && itemClicked.getType() == Material.PAPER){
                final ItemStack newItem = swapRewardType(itemClicked);
                final int slot = event.getSlot();
                setDisplayLore(newItem, NBTData.getNBT(newItem, "rewardType"));
                event.getClickedInventory().setItem(slot, newItem);
            }

        }
    }

    private void removeReward(ItemStack itemClicked){
        final List<Map<?, ?>> stringList = yaml.getData().getMapList(kothName + ".rewards");
        final Map<?, ?> id = getRewardFromList(stringList, NBTData.getNBT(itemClicked, "ID"));
        if(id != null) {
            stringList.remove(id);
            yaml.getData().set(kothName + ".rewards", stringList);
            yaml.save();
            this.inventory.removeItem(itemClicked);
        }
    }

    private Map<?, ?> getRewardFromList(List<Map<?, ?>> map, String uuid){
        for (Map<?, ?> map1 : map) {
            if(map1.get("ID").equals(uuid))
                return map1;
        }
        return null;
    }

    private ItemStack swapRewardType(ItemStack itemClicked){

        final String rewardType = NBTData.getNBT(itemClicked, "rewardType");
        final RewardType next = RewardType.getByYMLName(rewardType).next();
        if(next != null) {
            final List<Map<?, ?>> mapList = yaml.getData().getMapList(kothName + ".rewards");
            final Map<String, String> id = (Map<String, String>) getRewardFromList(mapList, NBTData.getNBT(itemClicked, "ID"));
            if(id != null)
            id.put("rewardType", next.getYamlName());
            yaml.getData().set(kothName+".rewards", mapList);
            yaml.save();

            return NBTData.setNBT(itemClicked, "rewardType~"+next.getYamlName());
        }
        return null;
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

    /**
     * Creates a new reward
     * @param ignored this item is ignored
     */
    protected void addReward(ItemStack ignored){

        new Title().send(player, ChatColor.GOLD+"Input the command without a '/'", "Replace the player's name with <player>", 10, 40, 10);
        player.sendMessage(ChatColor.GOLD+"Input the command without a '/'");
        player.sendMessage(ChatColor.WHITE+"Replace the player's name with <player>");
        isSwitching = true;
        player.closeInventory();

        new AwaitInput(player, 20000, msg -> {

            if(msg != null) {
                final List<Map<?, ?>> rewards = yaml.getData().getMapList(kothName + ".rewards");
                final Map<String, String> rewardMap = new HashMap<>();
                rewardMap.put("command", msg);
                rewardMap.put("rewardType", "Synced");
                rewardMap.put("ID", UUID.randomUUID().toString());
                rewards.add(rewardMap);
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

        final List<Map<?, ?>> rewardsList = yaml.getData().getMapList(kothName + ".rewards");
        final int size = calcInvSize(rewardsList.size());
        final Inventory inventory = Bukkit.createInventory(player, size, "Rewards");
        final int loopSize = Math.min(45, rewardsList.size());
        for (int i = 0; i < loopSize; i++) {
            final Map<String, String> cmd = (Map<String, String>) rewardsList.get(i);
            final String rewardType = cmd.get("rewardType");

             ItemStack item = ItemUtils.createItem(Material.PAPER, cmd.get("command"), "isDeletable~true",
                    "METHOD~swapRewardType", "rewardType~"+rewardType, "ID~"+cmd.get("ID"));
            setDisplayLore(item, rewardType);
            inventory.setItem(i, item);
        }

        inventory.setItem(inventory.getSize()-9, ItemUtils.createItem(Material.COMPASS, "Back", "METHOD~back"));
        inventory.setItem(inventory.getSize()-5, ItemUtils.createItem("160/5", "Add Reward", "METHOD~addReward"));

        return inventory;
    }

    /**
     * Sets the reward items reward type lore
     * @param item the item to update lore
     * @param rewardType the current reward type
     */
    private static void setDisplayLore(ItemStack item, String rewardType){

        final List<String> lore = new ArrayList<>();

        lore.add("Press Q to delete this reward");
        lore.add(ChatColor.DARK_GRAY+"====Data====");
        lore.add(getRewardTypeColor(rewardType, RewardType.SYNCED.getYamlName())+RewardType.SYNCED.getName());
        lore.add(getRewardTypeColor(rewardType, RewardType.CAPPER.getYamlName())+RewardType.CAPPER.getName());
        lore.add(getRewardTypeColor(rewardType, RewardType.OWNER.getYamlName())+RewardType.OWNER.getName());
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
    }

    private static ChatColor getRewardTypeColor(String rewardType, String enabledType){
        if(enabledType.equals(rewardType)) return ChatColor.GOLD;
        return ChatColor.GRAY;
    }

}
