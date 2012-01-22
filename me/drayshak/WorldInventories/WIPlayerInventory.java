package me.drayshak.WorldInventories;

import java.io.Serializable;
import java.util.HashMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class WIPlayerInventory implements Serializable
{
    private static final long serialVersionUID = 6713780477882018072L;
    WIItemStack[] playerItems = null;
    WIItemStack[] playerArmour = null;
    
    public WIPlayerInventory(ItemStack[] tPlayerItems, ItemStack[] tPlayerArmour)
    {
        setItems(tPlayerItems);
        setArmour(tPlayerArmour);
    }
 
    public WIPlayerInventory(WIItemStack[] tPlayerItems, WIItemStack[] tPlayerArmour)
    {
        this.playerItems = tPlayerItems;
        this.playerArmour = tPlayerArmour;
    }    
    
    public void setItems(ItemStack[] items)
    {
        playerItems = new WIItemStack[36];
        for(int i = 0; i < playerItems.length; i++)
        {
            if(items[i] == null) playerItems[i] = null;
            else
            {
                
                MaterialData data = items[i].getData();
                
                try
                {
                    if(data == null)    playerItems[i] = new WIItemStack(items[i].getTypeId(), items[i].getAmount(), items[i].getDurability(), null, items[i].getEnchantments());
                    else                playerItems[i] = new WIItemStack(items[i].getTypeId(), items[i].getAmount(), items[i].getDurability(), data.getData(), items[i].getEnchantments()); 
                }
                catch(NullPointerException e)
                {
                    if(data == null)    playerItems[i] = new WIItemStack(items[i].getTypeId(), items[i].getAmount(), items[i].getDurability(), null, new HashMap<Enchantment, Integer>());
                    else                playerItems[i] = new WIItemStack(items[i].getTypeId(), items[i].getAmount(), items[i].getDurability(), data.getData(), new HashMap<Enchantment, Integer>());                    
                }
            }
        }        
    }
   
    public void setArmour(ItemStack[] items)
    {
        playerArmour = new WIItemStack[4];
        for(int i = 0; i < playerArmour.length; i++)
        {
            if(items[i] == null) playerArmour[i] = null;
            else
            {
                MaterialData data = items[i].getData();
                
                try
                {
                    if(data == null)    playerArmour[i] = new WIItemStack(items[i].getTypeId(), items[i].getAmount(), items[i].getDurability(), null, items[i].getEnchantments());
                    else                playerArmour[i] = new WIItemStack(items[i].getTypeId(), items[i].getAmount(), items[i].getDurability(), data.getData(), items[i].getEnchantments()); 
                }
                catch(NullPointerException e)
                {
                    if(data == null)    playerArmour[i] = new WIItemStack(items[i].getTypeId(), items[i].getAmount(), items[i].getDurability(), null, new HashMap<Enchantment, Integer>());
                    else                playerArmour[i] = new WIItemStack(items[i].getTypeId(), items[i].getAmount(), items[i].getDurability(), data.getData(), new HashMap<Enchantment, Integer>());                    
                }
            }
        }        
    }
    
    public WIItemStack[] getItemsWI()
    {
        return this.playerItems;
    }
    
    public WIItemStack[] getArmourWI()
    {
        return this.playerArmour;
    }
    
    public ItemStack[] getItems()
    {
        ItemStack[] itemRet = new ItemStack[36];
        for(int i = 0; i < itemRet.length; i++)
        {
            if(playerItems[i] == null) itemRet[i] = null;
            else
            {
                WIMaterialData data = playerItems[i].getData();
                if(data == null) itemRet[i] = new ItemStack(playerItems[i].getTypeId(), playerItems[i].getAmount(), playerItems[i].getDurability(), null);
                else             itemRet[i] = new ItemStack(playerItems[i].getTypeId(), playerItems[i].getAmount(), playerItems[i].getDurability(), data.getData());
            
                try
                {
                    if(!playerItems[i].getEnchantments().isEmpty()) itemRet[i].addUnsafeEnchantments(playerItems[i].getEnchantments());            
                }
                catch(NullPointerException e)
                {
                    // Don't worry about it, no enchantments because it loaded an old style file
                }
            }
        }
        
        return itemRet;
    }
    
    public ItemStack[] getArmour()
    {
        ItemStack[] itemRet = new ItemStack[4];
        for(int i = 0; i < itemRet.length; i++)
        {
            if(playerArmour[i] == null) itemRet[i] = null;
            else
            {
                WIMaterialData data = playerArmour[i].getData();
                if(data == null) itemRet[i] = new ItemStack(playerArmour[i].getTypeId(), playerArmour[i].getAmount(), playerArmour[i].getDurability(), null);
                else             itemRet[i] = new ItemStack(playerArmour[i].getTypeId(), playerArmour[i].getAmount(), playerArmour[i].getDurability(), data.getData());
                
                try
                {          
                    if(!playerArmour[i].getEnchantments().isEmpty()) itemRet[i].addUnsafeEnchantments(playerArmour[i].getEnchantments());
                }
                catch(NullPointerException e)
                {
                    // Don't worry about it, no enchantments because it loaded an old style file
                }
            }
        }
        
        return itemRet;        
    }
}
