package me.nanigans.pandorakoth.Koth.Data.Scheduling;

import com.massivecraft.factions.Faction;
import me.nanigans.pandorakoth.Koth.Data.KothEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.TimerTask;

public class CappingKothTimer extends TimerTask {

    private final Player player;
    private final Faction faction;
    private final KothEvent event;

    public CappingKothTimer(Player player, Faction faction, KothEvent event){
        this.player = player;
        this.faction = faction;
        this.event = event;
    }

    @Override
    public void run() {
        event.endEvent(player, faction);
        this.cancel();
    }
}
