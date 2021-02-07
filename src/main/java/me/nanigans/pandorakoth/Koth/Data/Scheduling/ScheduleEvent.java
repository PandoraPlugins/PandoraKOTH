package me.nanigans.pandorakoth.Koth.Data.Scheduling;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.nanigans.pandorakoth.Koth.Data.KothEvent;
import me.nanigans.pandorakoth.Koth.Data.KothRegions;
import org.bukkit.Bukkit;

import java.util.Timer;
import java.util.TimerTask;

public class ScheduleEvent extends TimerTask {
    private final KothEvent event;

    public ScheduleEvent(KothEvent event){
        this.event = event;
    }

    @Override
    public void run() {

        event.setActive(true);
        Bukkit.broadcastMessage("A KOTH Event is currently happening");
        final EndEvent endEvent = new EndEvent();
        final Timer timer = new Timer();
        timer.schedule(endEvent, event.getScheduling().getEventDuration());

    }

    private final class EndEvent extends TimerTask{

        private final ProtectedRegion region;

        public EndEvent() {
            region = event.getRegion();
        }

        @Override
        public void run() {

            final KothRegions kothRegions = KothRegions.getKothRegions().get(region.getId());
            Bukkit.broadcastMessage("This event is now over");
            event.endEvent(kothRegions.getCapper(), kothRegions.getFactionCapping());
            this.cancel();

        }
    }
}
