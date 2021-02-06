package me.nanigans.pandorakoth.Koth.Data;

import me.nanigans.pandorakoth.PandoraKOTH;
import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.event.Listener;

import java.util.*;

public class KothEvent extends TimerTask implements Listener {

    protected YamlGenerator yaml;
    protected String eventName;
    protected boolean isEnabled;
    protected Rewards rewards;
    protected Scheduling scheduling;
    private boolean isDeleted;
    private static final Map<String, KothEvent> events = new HashMap<>();
    private static final Map<String, KothEvent> activeEvents = new HashMap<>();
    private Timer eventTimer;

    public KothEvent(String kothEventName, YamlGenerator yaml){
        this.yaml = yaml;
        this.eventName = kothEventName;
        this.isEnabled = yaml.getData().getBoolean(kothEventName+".enabled");
        this.rewards = new Rewards(this);
        this.scheduling = new Scheduling(this);
        events.put(kothEventName, this);
    }

    @Override
    public void run() {



    }

    public void delete(){
        isDeleted = true;
        yaml = null;
        eventName = null;
        rewards = null;
        scheduling = null;
        this.cancel();
        saveEvent();
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
        if(enabled && scheduling.getScheduleTime() != 0 && scheduling.getCapDuration() != 0){
            final Timer t = new Timer();
            t.schedule(this, scheduling.getScheduleTime(), scheduling.getScheduleTime());
            this.eventTimer = t;
        }else this.cancel();
    }

    public String getEventName() {
        return eventName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public Scheduling getScheduling() {
        return scheduling;
    }

    public static Map<String, KothEvent> getActiveEvents() {
        return activeEvents;
    }

    public Rewards getRewards() {
        return rewards;
    }

    public YamlGenerator getYaml() {
        return yaml;
    }

}


