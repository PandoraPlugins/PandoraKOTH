package me.nanigans.pandorakoth.Commands;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.nanigans.pandorakoth.Koth.Inventories.KothEditorInv;
import me.nanigans.pandorakoth.PandoraKOTH;
import me.nanigans.pandorakoth.Utils.YamlGenerator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;

public class KothEditor implements CommandExecutor {
    final PandoraKOTH plugin = PandoraKOTH.getPlugin(PandoraKOTH.class);
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equals("kothedit")){

            if(sender instanceof Player){

                if(sender.hasPermission("Koth.Edit")){

                    if(args.length > 0){

                        final String regionName = args[0];
                        final Player player = (Player) sender;
                        RegionManager regionManager = plugin.worldGuardPlugin.getRegionManager(player.getWorld());
                        final ProtectedRegion region = regionManager.getRegion(regionName);
                        if(region != null){

                            if(regionContainsFlag(region, "is-koth")) {
                                final File file = new File(plugin.getDataFolder().getAbsolutePath() + "/KOTHS/" + regionName + ".yml");
                                final YamlGenerator yamlGenerator = new YamlGenerator(file.getAbsolutePath());
                                new KothEditorInv(yamlGenerator, player, regionName);
                            }else{
                                player.sendMessage(ChatColor.RED+"This region is not a koth region");
                            }

                        }else{
                            player.sendMessage(ChatColor.RED+"Couldn't find that region name");
                        }


                    }else{
                        sender.sendMessage(ChatColor.RED+"Please specify a region to edit");
                    }

                }else{
                    sender.sendMessage(ChatColor.RED+"Invalid Permissions");
                }

            }else{
                sender.sendMessage(ChatColor.RED+"Only players may use this command");
            }
            return true;

        }

        return false;
    }

    public static boolean regionContainsFlag(ProtectedRegion region, String name){
        for (Map.Entry<Flag<?>, Object> flagObjectEntry : region.getFlags().entrySet()) {
            if (flagObjectEntry.getKey().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
