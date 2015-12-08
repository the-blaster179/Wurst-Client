/*
 * Copyright © 2014 - 2015 Alexander01998 and contributors
 * All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import tk.wurst_client.WurstClient;
import tk.wurst_client.commands.Cmd.Info;

@Info(help = "Allows you to copy items from other's hand and armor",
	name = "copyitem",
	syntax = {"<player> <head/chest/leg/foot>"})
public class CopyItemCmd extends Cmd
{
	private String playerName;
	private ItemStack item;
	private boolean found = false;
	
	@Override
	public void execute(String[] args) throws Error
	{
		found = false;
		if(args.length < 1)
			syntaxError();
		if(!Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode)
		{
			WurstClient.INSTANCE.chat.error("Creative mode only.");
			return;
		}
		playerName = args[0];
		
		for(Object entity : Minecraft.getMinecraft().theWorld.loadedEntityList)
			if(entity instanceof EntityOtherPlayerMP)
			{
				EntityOtherPlayerMP player = (EntityOtherPlayerMP)entity;
				if(player.getName().equals(playerName))
				{
					if(args.length > 1)
					{
						if(args[1].equalsIgnoreCase("head"))
							item = player.inventory.armorItemInSlot(3);
						if(args[1].equalsIgnoreCase("chest"))
							item = player.inventory.armorItemInSlot(2);
						if(args[1].equalsIgnoreCase("leg"))
							item = player.inventory.armorItemInSlot(1);
						if(args[1].equalsIgnoreCase("foot"))
							item = player.inventory.armorItemInSlot(0);
					}else
						item = player.inventory.getCurrentItem();
					found = true;
					WurstClient.INSTANCE.chat.message("Copyed "
						+ player.getName() + " item.");
				}
			}
		
		if(!found)
		{
			WurstClient.INSTANCE.chat.error("Player not found.");
			playerName = null;
			item = null;
			return;
		}
		
		for(int i = 0; i < 9; i++)
			if(Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i) == null)
			{
				Minecraft.getMinecraft().thePlayer.sendQueue
					.addToSendQueue(new C10PacketCreativeInventoryAction(
						36 + i, item));
				item = null;
				return;
			}
		error("Please clear a slot of your hotbar.");
		
	}
}
