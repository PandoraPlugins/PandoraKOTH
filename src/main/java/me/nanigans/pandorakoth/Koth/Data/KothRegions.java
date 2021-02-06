package me.nanigans.pandorakoth.Koth.Data;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class KothRegions {
    private final static Map<String, KothRegions> kothRegions = new HashMap<>();
    private final ProtectedRegion region;
    private Player capper;

    public KothRegions(ProtectedRegion region) {
        this.region = region;
        kothRegions.put(region.getId(), this);
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
