package me.nanigans.pandorakoth.Koth.Data;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KothRegions {
    private final static Map<String, KothRegions> kothRegions = new HashMap<>();
    private final ProtectedRegion region;
    private Player capper;
    private final static FPlayers instance = FPlayers.getInstance();

    public KothRegions(ProtectedRegion region) {
        this.region = region;
        kothRegions.put(region.getId(), this);
    }

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
        System.out.println("capper = " + capper);
        if(capper != null)
        Bukkit.broadcastMessage(capper.getName()+" is now capping at the koth");
        else Bukkit.broadcastMessage("Nobody is capping the koth");
        this.capper = capper;
    }

    public Player getCapper() {
        return capper;
    }

}
