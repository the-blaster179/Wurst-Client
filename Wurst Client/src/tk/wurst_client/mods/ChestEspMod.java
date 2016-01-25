/*
 * Copyright © 2014 - 2015 Alexander01998 and contributors
 * All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import tk.wurst_client.events.listeners.RenderListener;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.utils.RenderUtils;

@Info(category = Category.RENDER,
	description = "Allows you to see chests through walls.\n"
		+ "Tip: This works with the piston crates on HiveMC.",
	name = "ChestESP")
public class ChestEspMod extends Mod implements UpdateListener, RenderListener
{
	private int range = 50;
	private int maxChests = 1000;
	public boolean shouldInform = true;
	private ArrayList<BlockPos> matchingBlocks = new ArrayList<BlockPos>();
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.itemEspMod, wurst.mods.searchMod,
			wurst.mods.xRayMod};
	}
	
	@Override
	public void onEnable()
	{
		shouldInform = true;
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onRender()
	{
		int i = 0;
		for(Object o : mc.theWorld.loadedTileEntityList)
		{
			if(i >= maxChests)
				break;
			if(o instanceof TileEntityChest)
			{
				i++;
				RenderUtils.blockESPBox(((TileEntityChest)o).getPos());
			}else if(o instanceof TileEntityEnderChest)
			{
				i++;
				RenderUtils.blockESPBox(((TileEntityEnderChest)o).getPos());
			}
		}
		for(Object o : mc.theWorld.loadedEntityList)
		{
			if(i >= maxChests)
				break;
			if(o instanceof EntityMinecartChest)
			{
				i++;
				RenderUtils.blockESPBox(((EntityMinecartChest)o).getPosition());
			}
		}
		for(BlockPos blockPos : matchingBlocks)
		{
			if(i >= maxChests)
				break;
			i++;
			RenderUtils.blockESPBox(blockPos);
		}
		if(i >= maxChests && shouldInform)
		{
			wurst.chat.warning(getName() + " found §lA LOT§r of chests.");
			wurst.chat.message("To prevent lag, it will only show the first "
				+ maxChests + " chests.");
			shouldInform = false;
		}else if(i < maxChests)
			shouldInform = true;
	}
	
	@Override
	public void onUpdate()
	{
		updateMS();
		if(hasTimePassedM(3000))
		{
			matchingBlocks.clear();
			for(int y = range; y >= -range; y--)
				for(int x = range; x >= -range; x--)
					for(int z = range; z >= -range; z--)
					{
						int posX = (int)(mc.thePlayer.posX + x);
						int posY = (int)(mc.thePlayer.posY + y);
						int posZ = (int)(mc.thePlayer.posZ + z);
						BlockPos pos = new BlockPos(posX, posY, posZ);
						IBlockState state = mc.theWorld.getBlockState(pos);
						Block block = state.getBlock();
						int metadata = block.getMetaFromState(state);
						if(Block.getIdFromBlock(block) == 33
							&& (metadata == 6 || metadata == 7 || metadata == 15))
							matchingBlocks.add(pos);
					}
			updateLastMS();
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
	}
}
