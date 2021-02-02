package me.nanigans.pandorakoth;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import me.nanigans.pandorakoth.Commands.KothEditor;
import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class PandoraKOTH extends JavaPlugin {
    public static Flag kothFlag;
    public WorldGuardPlugin worldGuardPlugin = getWorldGuard();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("kothedit").setExecutor(new KothEditor());
        try {
            YamlGenerator.createFolder("KOTHS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoad(){
        final FlagRegistry registry = worldGuardPlugin.getFlagRegistry();
        try {
            final StringFlag flag = new StringFlag("is-koth", "true");
            registry.register(flag);
        } catch (FlagConflictException e) {
            e.printStackTrace();
            Flag<?> existing = registry.get("is-koth");
            if (existing instanceof StateFlag) {
                kothFlag = existing;
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Getting this error is bad news, something is conflicting with koth flag and worldguard");
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public WorldGuardPlugin getWorldGuard(){

        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if(!(plugin instanceof WorldGuardPlugin)) return null;
        return (WorldGuardPlugin) plugin;
    }

}
