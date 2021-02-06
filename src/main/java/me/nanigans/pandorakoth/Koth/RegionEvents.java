package me.nanigans.pandorakoth.Koth;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.nanigans.pandorakoth.Commands.KothEditor;
import me.nanigans.pandorakoth.Koth.Data.KothRegions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

public class RegionEvents implements Listener {
    private static final Map<String, List<UUID>> playersInRegions = new HashMap<>();
    private final FPlayers instance = FPlayers.getInstance();

    @EventHandler
    public void onEnterRegion(RegionEnterEvent event){
        final ProtectedRegion region = event.getRegion();
        System.out.println("1 = " + 1);
        if (KothEditor.regionContainsFlag(region, "is-koth")) {
            final Player player = event.getPlayer();
            final String regionName = region.getId();
            if(!playersInRegions.containsKey(regionName)) playersInRegions.put(regionName, new ArrayList<>());
            final List<UUID> usersInRegion = playersInRegions.get(regionName);
            usersInRegion.add(player.getUniqueId());

            final KothRegions kRegion = getKothRegion(region);
            System.out.println("kRegion.getCapper() = " + kRegion.getCapper());

            final Player capper = kRegion.getCapper();
            if(capper == null) kRegion.setCapper(player);

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
            final List<UUID> uuids = playersInRegions.get(regionName);
            uuids.remove(player.getUniqueId());

            final KothRegions kothRegion = getKothRegion(region);
            final Player capper = kothRegion.getCapper();
            if(player.getUniqueId().equals(capper.getUniqueId())){

                final FPlayer fCapper = instance.getByPlayer(capper);
                final Player sameFactionPlayer = getSameFactionPlayer(regionName, fCapper.getFaction());
                final EntityDamageEvent lastDamageCause = capper.getLastDamageCause();
                Entity entity = null;
                if(lastDamageCause != null){
                   entity = lastDamageCause.getEntity();
                }
                if(sameFactionPlayer != null){
                    kothRegion.setCapper(sameFactionPlayer);
                }else if(entity instanceof Player && uuids.contains(entity.getUniqueId())){
                    kothRegion.setCapper(((Player) entity));
                }else if(uuids.size() > 0){
                    kothRegion.setCapper(Bukkit.getPlayer(uuids.get((int) (Math.random()*uuids.size()))));
                }else kothRegion.setCapper(null);
            }
        }
    }

    private Player getSameFactionPlayer(String kothRegion, Faction inFaction){

        final List<UUID> uuids = playersInRegions.get(kothRegion);
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

    private KothRegions getKothRegion(ProtectedRegion region){

        final KothRegions kRegion;
        final String regionName = region.getId();
        if (!KothRegions.getKothRegions().containsKey(regionName)) {
            kRegion = new KothRegions(region);
        }else kRegion = KothRegions.getKothRegions().get(regionName);

        return kRegion;
    }

}
