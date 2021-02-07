package me.nanigans.pandorakoth.Koth.Data;

import com.massivecraft.factions.Faction;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.nanigans.pandorakoth.Koth.Data.Scheduling.ScheduleEvent;
import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class KothEvent{

    protected YamlGenerator yaml;
    protected String eventName;
    protected boolean isEnabled;
    protected Rewards rewards;
    protected SchedulingData scheduling;
    private ProtectedRegion region;
    private boolean isDeleted;
    private static final Map<String, KothEvent> events = new HashMap<>();//{regionName: kothevent}
    private Timer eventTimer;
    private boolean isActive = false;

    public KothEvent(String kothEventName, YamlGenerator yaml, ProtectedRegion region){
        this.yaml = yaml;
        this.eventName = kothEventName;
        this.isEnabled = yaml.getData().getBoolean(kothEventName+".enabled");
        this.rewards = new Rewards(this);
        this.scheduling = new SchedulingData(this);
        this.region = region;
        events.put(region.getId(), this);
    }

    public void delete(){
        isDeleted = true;
        yaml = null;
        eventName = null;
        rewards = null;
        scheduling = null;
        saveEvent();
    }


    /**
     * Ends the running event
     * @param capper the player that capped the event
     * @param cappedFac the players faction
     */
    public void endEvent(Player capper, Faction cappedFac){

        this.isActive = false;
        if(capper != null && cappedFac != null)
        Bukkit.broadcastMessage("WINNER OF THE EVENT IS " + capper.getName() + " Faction: " + cappedFac.getTag());
        eventTimer.cancel();

    }

    public void saveEvent(){

        if(!isDeleted) {
            yaml.getData().set(eventName+".enabled", isEnabled);
            yaml.getData().set(eventName + ".rewards", rewards.getRewardList());
            yaml.getData().set(eventName + ".times", scheduling.getTimings());
        }else yaml.getData().set(eventName, null);

        yaml.save();
    }

    public static Map<String, KothEvent> getEvents() {
        return events;
    }


    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        if(enabled && scheduling.getScheduleTime() != 0 && scheduling.getCapDuration() != 0 && scheduling.getEventDuration() != 0){
            final Timer t = new Timer();
            final ScheduleEvent scheduleEvent = new ScheduleEvent(this);
            t.schedule(scheduleEvent, scheduling.getScheduleTime(), scheduling.getScheduleTime());
            this.eventTimer = t;
        }else{
            if(this.eventTimer != null)
            this.eventTimer.cancel();
        }
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getEventName() {
        return eventName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public SchedulingData getScheduling() {
        return scheduling;
    }

    public Rewards getRewards() {
        return rewards;
    }

    public YamlGenerator getYaml() {
        return yaml;
    }

}


