/*
 * Copyright © 2014 - 2016 | Alexander01998 and contributors
 * All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import tk.wurst_client.WurstClient;
import tk.wurst_client.events.ChatOutputEvent;
import tk.wurst_client.events.listeners.ChatOutputListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.CHAT,
	description = "Replaces ASCII characters in sent chat messages with fancier unicode characters. Can be\n"
		+ "used to bypass curse word filters on some servers. Does not work on servers that block\n"
		+ "unicode characters.",
	name = "FancyChat")
public class FancyChatMod extends Mod implements ChatOutputListener
{
	private final String blacklist = "(){}[]|";
	
	@Override
	public void onEnable()
	{
		WurstClient.INSTANCE.events.add(ChatOutputListener.class, this);
	}
	
	@Override
	public void onSentMessage(ChatOutputEvent event)
	{
		if(event.getMessage().startsWith("/") || event.getMessage().startsWith("."))
			return;
			
		String out = "";
		
		for(char chr : event.getMessage().toCharArray())
		{
			if(chr >= 0x21 && chr <= 0x80
				&& !blacklist.contains(Character.toString(chr)))
				out += new String(Character.toChars(((int)chr) + 0xFEE0));
			else
				out += chr;
		}
		
		event.setMessage(out);
	}
	
	@Override
	public void onDisable()
	{
		WurstClient.INSTANCE.events.remove(ChatOutputListener.class, this);
	}
}
