/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.tggl.pluckerpluck.multiinv;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * API designed to allow MultiInv to improve compatability with other plugins
 * @author Pluckerpluck
 */
public class MultiInvAPI {
      
    /**
     * Ignores the player by the given name (Name must be exact)
     * Ignored players will not change inventory when teleporting
     * @param playerName The name of the player
     */
    public static void ignorePlayer(String playerName){
        MultiInv.ignoreList.add(playerName);
    }
    
    /**
     * Un-ignores a previously ignored player
     * Will not un-ignore players ignored by MultiInv unless forced
     * @param playerName The name of the player
     * @param force If true will force an un-ignore of the Player
     */
    public static void unignorePlayer(String playerName, boolean force){
        MultiInv.ignoreList.remove(playerName);
    }
    
    /**
     * Un-ignores a previously ignored player
     * Will not un-ignore players ignored by MultiInv
     * @param playerName The name of the player
     */
    public static void unignorePlayer(String playerName){
        unignorePlayer(playerName, false);
    }
    
    /**
     * Checks whether a player is in the ignore list
     * @param playerName The name of the player
     * @return true if player is ignored
     */
    public static boolean isIgnored(String playerName){
        boolean ignored = false;
        if (MultiInv.ignoreList.contains(playerName)){
            ignored = true;
        }
        return ignored;
    }
    
    /**
     * Saves the inventory of the chosen player to the chosen world
     * @param player Player whose inventory you want to save
     * @param world Name of the world you want to save the inventory to
     * @return false if world does not exist
     */
    public static boolean saveInventory(Player player, String world){
        String inventoryName = "MultiInvInventory";
        boolean success = false;
        if (player.isOnline() && Bukkit.getServer().getWorld(world) != null){
            MultiInvPlayerData.storeManualInventory(player, inventoryName, world);
            success = true;
        }
        return success;
    }
    
    /**
     * Loads the inventory from the chosen world and gives it to the player
     * @param player The player whose inventory you want to set
     * @param world The world's inventory that you wish to load
     * @return false if world does not exist
     */
    public static boolean loadInventory(Player player, String world){
        boolean success = false;
        if (Bukkit.getServer().getWorld(world) != null){
            MultiInvPlayerData.loadWorldInventory(player, world, false);
            success = true;
        }
        return success;
    }

    
}