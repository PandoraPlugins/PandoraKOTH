package me.nanigans.pandorakoth.Koth.Data;

import java.util.HashMap;
import java.util.Map;

public class SchedulingData {

    private final KothEvent kothEvent;
    private Map<String, Long> timings = new HashMap<>();
    private long scheduleTime;
    private long capDuration;

    public SchedulingData(KothEvent kothEvent) {
        this.kothEvent = kothEvent;

        final long schedule = kothEvent.getYaml().getData().getLong(kothEvent.getEventName() + ".times.scheduleTime");
        final long duration = kothEvent.getYaml().getData().getLong(kothEvent.getEventName() + ".times.capDuration");
        this.scheduleTime = schedule;
        this.capDuration = duration;
        timings.put("scheduleTime", schedule);
        timings.put("capDuration", duration);

    }

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime) {
        this.scheduleTime = scheduleTime;
        timings.replace("scheduleTime", scheduleTime);
    }

    public long getCapDuration() {
        return capDuration;
    }

    public void setCapDuration(long capDuration) {
        this.capDuration = capDuration;
        timings.replace("capDuration", capDuration);
    }

    public void setTimings(Map<String, Long> timings) {
        this.timings = timings;
    }

    public Map<String, Long> getTimings() {
        return timings;
    }
}
