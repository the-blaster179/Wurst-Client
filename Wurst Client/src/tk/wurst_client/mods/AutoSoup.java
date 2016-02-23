/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;

import org.darkstorm.minecraft.gui.component.BoundedRangeComponent.ValueDisplay;

import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.navigator.settings.SliderSetting;
import tk.wurst_client.utils.NetworkUtils;

@Info(category = Category.COMBAT,
description = "Automatically eat soups.",
name = "AutoSoup", tags = "AutoSoup,auto soup")
public class AutoSoupMod extends Mod implements UpdateListener{
	public float normalHealth = 20F;
	@Override
	public void initSettings()
	{
		settings.add(new SliderSetting("Health", normalHealth, 1, 10,0.5,
			ValueDisplay.DECIMAL)
		{
			@Override
			public void update()
			{
				normalHealth = (float)getValue()*2;
				
			}
		});
	
	}
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.autosplashpotMod};
	}
	@Override
	public void onUpdate() {
		updateMS();
		if (soups() == 0) {
	        return;
	      }
		
		   if ((mc.thePlayer.getHealth() <= (this.normalHealth ) && hasTimePassedM(500l)))
			      {
			    	  
			        if (hasHotbarSoups()) {
			          eatSoup();
			          updateLastMS();
			        } else {
			          getFromUpInv();
			        }
			        
			      }
	}
	  private boolean hasHotbarSoups()
	  {
	    for (int index = 36; index < 45; index++)
	    {
	      ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(
	        index).getStack();
	      if ((stack != null) && 
	        (isStack(stack))) {
	        return true;
	      }
	    }
	    return false;
	  }
	  private boolean isStack(ItemStack stack)
	  {
	    if (stack == null) {
	      return false;
	    }
	    return stack.getItem() instanceof ItemSoup;
	  }
	  private void getFromUpInv()
	  {
	    if ((mc.currentScreen instanceof GuiChest)) {
	      return;
	    }
	    Bowlstack();
	    for (int index = 9; index < 36; index++)
	    {
	      ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(
	        index).getStack();
	      if ((stack != null) && 
	        (isStack(stack)))
	      {
	        mc.playerController.windowClick(0, index, 0, 1, mc.thePlayer);
	        break;
	      }
	    }
	  }
	  private void Bowlstack()
	  {
	    if ((mc.currentScreen instanceof GuiChest)) {
	      return;
	    }
	    for (int index = 9; index < 45; index++)
	    {
	      ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(
	        index).getStack();
	      if ((stack != null) && 
	        (stack.getItem() == Items.bowl))
	      {
	        mc.playerController.windowClick(0, index, 0, 0, mc.thePlayer);
	        mc.playerController.windowClick(0, 18, 0, 0, mc.thePlayer);
	      }
	    }
	  }
	  private void eatSoup()
	  {
	    for (int index = 36; index < 45; index++)
	    {
	      ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(
	        index).getStack();
	      if ((stack != null) && 
	        (isStack(stack)))
	      {
	        Bowlstack();
	        int oldslot = mc.thePlayer.inventory.currentItem;
	        NetworkUtils.sendPacket(
	          new C09PacketHeldItemChange(index - 36));
	        mc.playerController.updateController();
	        NetworkUtils.sendPacket(
	          new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), -1, 
	          stack, 0.0F, 0.0F, 0.0F));
	        NetworkUtils.sendPacket(
	          new C09PacketHeldItemChange(oldslot));
	        break;
	      }
	    }
	  }
	  private int soups()
	  {
	    int counter = 0;
	    for (int index = 9; index < 45; index++)
	    {
	      ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(
	        index).getStack();
	      if ((stack != null) && 
	        (isStack(stack))) {
	        counter += stack.stackSize;
	      }
	    }
	   
	    return counter;
	  }
	  public void onEnable()
		{wurst.events.add(UpdateListener.class, this);}
}
