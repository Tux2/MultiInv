package uk.co.tggl.pluckerpluck.multiinv;

import me.drayshak.WorldInventories.WIPlayerInventory;
import me.drayshak.WorldInventories.WIPlayerStats;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;

public class MultiInvCommands {

    public final MultiInv plugin;

    public MultiInvCommands(MultiInv instance) {
        plugin = instance;
    }

    /*
     * Different return integers represent different errors
     * 0 -> Success
     * 1 -> Failed permissions
     * 2 -> Lack of inputs
     * 3 -> No command performed
     */
    int playerCommand(CommandSender sender, String[] split, String permission) {
        Player player = (Player) sender;
        String Str = split[0];
        if (!plugin.permissionCheck((Player) sender, permission)) {
            sender.sendMessage("You do not have permissions to use " + Str);
            return 1;
        }
        if (Str.equalsIgnoreCase("delete")) {
            if (split.length > 1) {
                deleteCommand(sender, split);
            } else {
                sender.sendMessage("Please supply player name to delete the inventories");
            }
            return 0;
        } else if (Str.equalsIgnoreCase("debug")) {
            if (split.length >= 2) {
                debugCommand(sender, split);
                return 0;
            }
            return 2;
        } else if (Str.equalsIgnoreCase("ignore")) {
            Player ignored = player;
            if (split.length >= 2) {
                ignored = plugin.getServer().getPlayer(split[1]);
                if (!ignored.getName().equalsIgnoreCase(split[1])) {
                    sender.sendMessage("Player cannot be found. He must be online");
                    return 0;
                }
            }
            ignoreCommand(sender, ignored);
        } else if (Str.equalsIgnoreCase("unignore")) {
            String ignored = player.getName();
            if (split.length >= 2) {
                ignored = split[1];
            }
            unignoreCommand(sender, ignored);
            return 0;
        } else if (Str.equalsIgnoreCase("addShare")) {
            if (split.length >= 3) {
                String minorWorld = split[1];
                String majorWorld = split[2];
                shareWorlds(minorWorld, majorWorld);
                return 0;
            }
            sender.sendMessage("/MultiInv addShare <minorWorld> <majorWorld>");
            return 2;
        } else if (Str.equalsIgnoreCase("removeShare")) {
            if (split.length >= 2) {
                String minorWorld = split[1];
                removeShareWorld(minorWorld);
            }
            sender.sendMessage("/MultiInv removeShare <minorWorld>");
            return 2;
        } else if (Str.equalsIgnoreCase("import")) {
            importInventories();
            sender.sendMessage("Player file import complete!");
            return 0;
        }
        return 3;
    }

    private void importInventories() {
        System.out.println("getting World Inventories Directory");
    	String worldinventoriesdir = plugin.getDataFolder().getParentFile().getAbsolutePath() + File.separator + "WorldInventories"  + File.separator;
    	File worldinvdir = new File(worldinventoriesdir);
    	if(worldinvdir.exists()) {
        	File[] thedirs = worldinvdir.listFiles();
        	for(File fdir : thedirs) {
        		if(fdir.isDirectory()) {
                    System.out.println("In group directory " + fdir.getName());
        			File[] playerfiles = fdir.listFiles();
        			for(File pfile : playerfiles) {
        				if(pfile.getName().endsWith(".inventory")) {
                            System.out.println("Importing player " + pfile.getName());
        					try
        			        {
        			            FileInputStream fIS = new FileInputStream(pfile);
        			            ObjectInputStream obIn = new ObjectInputStream(fIS);
        			            WIPlayerInventory playerInventory = (WIPlayerInventory) obIn.readObject();
        			            obIn.close();
        			            fIS.close();
        			            
        			            //Now that we have the file in, let's convert it.
        			            String playername = pfile.getName().substring(0, pfile.getName().lastIndexOf("."));
        			            File dataFile = plugin.getDataFolder();
        			            File file = new File(dataFile, "Worlds" + File.separator + fdir.getName() + File.separator + playername + ".yml");
        			            Configuration playerFile = new Configuration(file);
        			            playerFile.load();
        			            String inventoryName = "survival";
        			            if (MultiInv.currentInventories.containsKey(playername)) {
        			                inventoryName = MultiInv.currentInventories.get(playername)[0];
        			            }
        			            MultiInvInventory inventory = new MultiInvInventory((PlayerInventory)null, inventoryName, MultiInv.pluginName);
        			            inventory.setContents(playerInventory.getItems());
        			            inventory.setArmourContents(playerInventory.getArmour());
        			            playerFile.setProperty(inventoryName, inventory.toString());
        			            
        			            //Let's load all the other state stuff about the player...
        			            File fplayerstats = new File(fdir, playername + ".stats");
        			            if(fplayerstats.exists()) {
        			            	fIS = new FileInputStream(fplayerstats);
        			                obIn = new ObjectInputStream(fIS);
        			                WIPlayerStats playerstats = (WIPlayerStats) obIn.readObject();
        			                obIn.close();
        			                fIS.close();
        			                
        			                if (MultiInvPlayerData.isHealthSplit) {
        			                	int health = playerstats.getHealth();
        			                    if (health <= 0 || health > 20) {
        			                    	health = 20;
        			                    }
        			                    playerFile.setProperty("health", health);
        			                }
        			                if(MultiInvPlayerData.isHungerSplit){
        			                    playerFile.setProperty("hungerSaturation", playerstats.getSaturation());
        			                    playerFile.setProperty("hungerLevel", playerstats.getFoodLevel());
        			                    playerFile.setProperty("exhaustion", playerstats.getExhaustion());
        			                }
        			                if (MultiInvPlayerData.isExpSplit) {
        			                    int exp = playerFile.getInt("expLevel", 0);
        			                    double expgain = playerFile.getInt("otherExp", 0);
        			                    playerFile.setProperty("expLevel", playerstats.getLevel());
        			                    playerFile.setProperty("otherExp", playerstats.getExp());
        			                }
        			            }

        			            // Save gameMode
        			            int creativeGroup = 0;
        			            if (MultiInv.creativeGroups.contains(fdir.getName())){
        			                creativeGroup = 1;
        			            }
        			            playerFile.setProperty("gameMode", playerFile.getInt("gameMode", creativeGroup));

        			            playerFile.save();
        			        }
        			        catch (FileNotFoundException e)
        			        {
        			            System.out.println("Uhoh, a file wasn't found");
        			        }
        			        catch (Exception e)
        			        {
        			        	
        			        }
        				}
        			}
        		}
        	}
    	}
	}

	/* Below here are the actual commands called that perform the required action */
    private void deleteCommand(CommandSender sender, String[] split) {
        int invs = plugin.deletePlayerInventories(split[1]);
        if (invs != 0) {
            if (invs == 1) {
                sender.sendMessage("Deleted 1 invetory for player " + split[1]);
            } else {
                sender.sendMessage("Deleted " + invs + " invetories for player " + split[1]);
            }
        } else {
            sender.sendMessage("Player " + split[1] + " does not exist");
        }
    }

    private void debugCommand(CommandSender sender, String[] split) {
        if (split[1].equalsIgnoreCase("start")) {
            if (split.length >= 3 && split[2].equalsIgnoreCase("show") && sender instanceof Player) {
                plugin.debugger.addDebugger((Player) sender);
                sender.sendMessage("Debugging started (shown)");
            } else {
                plugin.debugger.startDebugging();
                sender.sendMessage("Debugging started (hidden)");
            }
        } else if (split[1].equalsIgnoreCase("stop")) {
            plugin.debugger.stopDebugging();
            sender.sendMessage("Debugging stopped");
        } else if (split[1].equalsIgnoreCase("save")) {
            plugin.debugger.saveDebugLog();
            sender.sendMessage("Debugging saved");
        }
    }

    private void ignoreCommand(CommandSender sender, Player player) {
        String playerName = player.getName();
        if (MultiInv.ignoreList.contains(playerName.toLowerCase())) {
            sender.sendMessage("Player is already being ignored");
            return;
        }
        MultiInv.ignoreList.add(playerName.toLowerCase());
        sender.sendMessage(playerName + " is now being ignored");
    }

    private void unignoreCommand(CommandSender sender, String playerName) {
        if (MultiInv.ignoreList.contains(playerName.toLowerCase())) {
            MultiInv.ignoreList.remove(playerName.toLowerCase());
            sender.sendMessage(playerName + " is no longer ignored");
            return;
        }
        sender.sendMessage(playerName + " was not being ignored");
    }

    private void shareWorlds(String minorWorld, String majorWorld) {
        String file = plugin.getDataFolder() + File.separator + "shares.properties";
        MultiInvProperties.saveToProperties(file, minorWorld, majorWorld);
        MultiInv.sharesMap.put(minorWorld, majorWorld);
    }

    private void removeShareWorld(String minorWorld) {
        String file = plugin.getDataFolder() + File.separator + "shares.properties";
        MultiInvProperties.removeProperty(file, minorWorld, "");
        MultiInv.sharesMap.remove(minorWorld);
    }
}
