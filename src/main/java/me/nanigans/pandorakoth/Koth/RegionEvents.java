package me.nanigans.pandorakoth.Koth;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.nanigans.pandorakoth.Commands.KothEditor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

public class RegionEvents implements Listener {

    @EventHandler
    public void onEnterRegion(RegionEnterEvent event){
        final ProtectedRegion region = event.getRegion();
        if (KothEditor.regionContainsFlag(region, "is-koth")) {



        }
    }

}
