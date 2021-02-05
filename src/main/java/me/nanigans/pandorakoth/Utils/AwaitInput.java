package me.nanigans.pandorakoth.Utils;

import me.nanigans.pandorakoth.PandoraKOTH;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public class AwaitInput extends BukkitRunnable implements Listener {
    private final Player player;
    private final long duration;
    private volatile String message;
    private final Consumer<String> callback;
    private final static PandoraKOTH plugin = PandoraKOTH.getPlugin(PandoraKOTH.class);

    /**
     * Awaits a single input from a user
     * @param fromPlayer the player to wait for
     * @param duration how long to wait for
     * @param callback what should happen when a message is sent
     */
    public AwaitInput(Player fromPlayer, long duration, Consumer<String> callback){
        this.player = fromPlayer;
        this.duration = duration+System.currentTimeMillis();
        this.callback = callback;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void run() {
        while(System.currentTimeMillis() < duration){
            if(message != null){
                HandlerList.unregisterAll(this);
                callback.accept(message);
                this.cancel();
                break;
            }
        }
        HandlerList.unregisterAll(this);
        callback.accept(null);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){

        if(event.getPlayer().getUniqueId().equals(this.player.getUniqueId())){
            this.message = event.getMessage();
        }

    }

}
