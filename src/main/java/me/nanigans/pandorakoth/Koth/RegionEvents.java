package me.nanigans.pandorakoth.Koth;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.nanigans.pandorakoth.Commands.KothEditor;
import me.nanigans.pandorakoth.Koth.Data.KothRegions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class RegionEvents implements Listener {
    private static final Map<String, KothRegions> playersInRegions = new HashMap<>();
    private final FPlayers instance = FPlayers.getInstance();

    @EventHandler
    public void onEnterRegion(RegionEnterEvent event){
        final ProtectedRegion region = event.getRegion();
        if (KothEditor.regionContainsFlag(region, "is-koth")) {
            final Player player = event.getPlayer();
            final String regionName = region.getId();

            if(!playersInRegions.containsKey(regionName)) playersInRegions.put(regionName, new KothRegions(region));
            final KothRegions kothRegion = playersInRegions.get(regionName);

            final List<UUID> usersInRegion = kothRegion.getPlayersInRegion();
            usersInRegion.add(player.getUniqueId());

            final Player capper = kothRegion.getCapper();
            if(capper == null) kothRegion.setCapper(player);

        }
    }

    /**
     * Detects when a player leaves a koth region and updates the current capper
     * @param event region leave event
     */
    @EventHandler
    public void onLeaveRegion(RegionLeaveEvent event){
        final ProtectedRegion region = event.getRegion();
        if (KothEditor.regionContainsFlag(region, "is-koth")) {
            final String regionName = region.getId();
            final Player player = event.getPlayer();
            final KothRegions kothRegion = playersInRegions.get(regionName);
            final List<UUID> uuids = kothRegion.getPlayersInRegion();
            uuids.remove(player.getUniqueId());
            final Player capper = kothRegion.getCapper();

            if(player.getUniqueId().equals(capper.getUniqueId())){//if event player (capper) leaves
                kothRegion.updateCapper(getSameFactionPlayer(regionName, instance.getByPlayer(capper).getFaction()), uuids);
            }
        }
    }

    private Player getSameFactionPlayer(String kothRegion, Faction inFaction){

        final List<UUID> uuids = playersInRegions.get(kothRegion).getPlayersInRegion();
        final Set<FPlayer> fPlayers = inFaction.getFPlayers();
        for (UUID uuid : uuids) {
            final Player player = Bukkit.getPlayer(uuid);
            final FPlayer fPlayer = instance.getByPlayer(player);
            if(fPlayers.contains(fPlayer)){
                return fPlayer.getPlayer();
            }
        }
        return null;
    }

}
