package me.nanigans.pandorakoth.Koth.Inventories;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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
    private final String name;
    private final String yamlName;
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

    public KothRewardsInv(Player player, YamlGenerator yaml, String kothName, ProtectedRegion region) {
        super(player, yaml, kothName, region);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        final ItemStack itemClicked = handleClick(event);
        final boolean isItemDropped = event.getAction().toString().contains("DROP");
        if(itemClicked != null){

            if(isItemDropped && NBTData.containsNBT(itemClicked, "isDeletable")){
                removeReward(itemClicked);
            }else if(event.getClick() == ClickType.LEFT && NBTData.containsNBT(itemClicked, "ID")){
                final ItemStack newItem = swapRewardType(itemClicked);
                final int slot = event.getSlot();
                setDisplayLore(newItem, NBTData.getNBT(newItem, "rewardType"));
                event.getClickedInventory().setItem(slot, newItem);
            }

        }
    }

    private void removeReward(ItemStack itemClicked){
        final List<Map<String, String>> stringList = event.getRewards().getRewardList();
        final Map<String, String> id = event.getRewards().getRewardFromList(NBTData.getNBT(itemClicked, "ID"));
        if(id != null) {
            stringList.remove(id);
            this.inventory.removeItem(itemClicked);
        }
    }

    private ItemStack swapRewardType(ItemStack itemClicked){

        final String rewardType = NBTData.getNBT(itemClicked, "rewardType");
        RewardType next = RewardType.getByYMLName(rewardType);
        if(next != null) {
            next = next.next();
            final List<Map<String, String>> mapList = event.getRewards().getRewardList();
            final Map<String, String> id = event.getRewards().getRewardFromList(NBTData.getNBT(itemClicked, "ID"));
            if(id != null)
                id.put("rewardType", next.getYamlName());
            event.getRewards().setRewards(mapList);

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
        handleInvClose(event);
    }

    @Override
    protected void back(ItemStack ignored) {
        HandlerList.unregisterAll(this);
        swapInventories(new KothDataInv(player, yaml, kothName, region));
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
                final List<Map<String, String>> rewards = event.getRewards().getRewardList();
                final Map<String, String> rewardMap = new HashMap<>();
                rewardMap.put("command", msg);
                rewardMap.put("rewardType", "Synced");
                rewardMap.put("ID", UUID.randomUUID().toString());
                rewards.add(rewardMap);
                event.getRewards().setRewards(rewards);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    HandlerList.unregisterAll(KothRewardsInv.this);
                    swapInventories(new KothRewardsInv(player, yaml, kothName, region));
                }
            }.runTask(plugin);
        }).runTaskAsynchronously(plugin);

    }

    @Override
    protected Inventory createInventory() {

        List<Map<String, String>> rewardsList;
        if(event != null)
         rewardsList = event.getRewards().getRewardList();
        else rewardsList = new ArrayList<>();

        final int size = calcInvSize(rewardsList.size());
        final Inventory inventory = Bukkit.createInventory(player, size, "Rewards");
        final int loopSize = Math.min(45, rewardsList.size());
        for (int i = 0; i < loopSize; i++) {
            final Map<String, String> cmd = rewardsList.get(i);
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
