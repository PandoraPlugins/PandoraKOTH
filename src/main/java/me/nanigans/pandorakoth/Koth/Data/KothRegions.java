package me.nanigans.pandorakoth.Koth.Data;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.nanigans.pandorakoth.Koth.Data.Scheduling.CappingKothTimer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

public class KothRegions {

    private final static Map<String, KothRegions> kothRegions = new HashMap<>();
    private final static Map<String, KothEvent> activeEventsInRegion = new HashMap<>();//{regionName: kothEvent}
    private final List<UUID> playersInRegion = new ArrayList<>();
    private final ProtectedRegion region;
    private static final FPlayers instance = FPlayers.getInstance();
    private Player capper;
    private Faction factionCapping;
    private Timer capTimer;

    public KothRegions(ProtectedRegion region) {
        this.region = region;
        kothRegions.put(region.getId(), this);
    }

    /**
     * Updates
     * @param sameFactionPlayer another player in the same faction to give the cap to
     * @param uuids a list of people in the region
     */
    public void updateCapper(Player sameFactionPlayer, List<UUID> uuids){

        final EntityDamageEvent lastDamageCause = capper.getLastDamageCause();
        Entity entity = null;
        if(lastDamageCause != null){
            entity = lastDamageCause.getEntity();
        }
        if(sameFactionPlayer != null){
            this.setCapper(sameFactionPlayer);
        }else if(entity instanceof Player && uuids.contains(entity.getUniqueId())){
            this.setCapper(((Player) entity));
        }else if(uuids.size() > 0){
            this.setCapper(Bukkit.getPlayer(uuids.get((int) (Math.random()*uuids.size()))));
        }else this.setCapper(null);
    }


    public static Map<String, KothRegions> getKothRegions() {
        return kothRegions;
    }

    public void setCapper(Player capper) {
        this.capper = capper;

        if(capper != null) {

            final FPlayer byPlayer = instance.getByPlayer(capper);
            final Faction faction = byPlayer.getFaction();

            if(faction != null && !faction.isWilderness()){
                if(this.factionCapping == null || !this.factionCapping.getId().equals(faction.getId())){//sets a new faction capping
                    this.factionCapping = faction;
                    final KothEvent kothEvent = KothEvent.getEvents().get(region.getId());
                    if(kothEvent.isActive()) {
                        startTimer(faction, kothEvent);
                    }
                }
            }else{
                factionCapping = null;
                capTimer.cancel();
            }

            Bukkit.broadcastMessage(capper.getName() + " is now capping at the koth in factieon ("
                    + (factionCapping == null ? "none" : factionCapping.getTag()) + ")");

        } else{
            Bukkit.broadcastMessage("Nobody is capping the koth");
            factionCapping = null;
            if(capTimer != null)
                capTimer.cancel();
        }
    }

    private void startTimer(Faction faction, KothEvent kothEvent){
        final CappingKothTimer cappingKothTimer = new CappingKothTimer(capper, faction, kothEvent);
        final Timer timer = new Timer();
        timer.schedule(cappingKothTimer,
                kothEvent.getScheduling().getCapDuration());
        if (capTimer != null)
            capTimer.cancel();
        capTimer = timer;
    }
    public static Map<String, KothEvent> getActiveEventsInRegion() {
        return activeEventsInRegion;
    }

    public List<UUID> getPlayersInRegion() {
        return playersInRegion;
    }

    public Faction getFactionCapping() {
        return factionCapping;
    }

    public Player getCapper() {
        return capper;
    }

}
