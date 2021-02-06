package me.nanigans.pandorakoth.Koth.Data.Scheduling;

import org.bukkit.Bukkit;

import java.util.TimerTask;

public class ScheduleEvent extends TimerTask {


    @Override
    public void run() {

        Bukkit.broadcastMessage("A KOTH Event is currently happening");

    }
}
