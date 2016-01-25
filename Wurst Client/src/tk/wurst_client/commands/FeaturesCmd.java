/*
 * Copyright © 2014 - 2015 Alexander01998 and contributors
 * All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import tk.wurst_client.commands.Cmd.Info;
import tk.wurst_client.mods.Mod;
import tk.wurst_client.mods.Mod.Category;

@Info(help = "Counts the features in this release of Wurst.",
	name = "features",
	syntax = {})
public class FeaturesCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length != 0)
			syntaxError();
		wurst.chat.message("Features in this release of Wurst:");
		int mods = wurst.mods.countMods();
		int hiddenMods = 0;
		for(Mod mod : wurst.mods.getAllMods())
			if(mod.getCategory() == Category.HIDDEN)
				hiddenMods++;
		wurst.chat.message(">" + (mods - hiddenMods) + " mods (+" + hiddenMods
			+ " hidden mods)");
		int commands = wurst.commands.countCommands();
		wurst.chat.message(">" + commands + " commands");
		wurst.chat.message(">" + wurst.keybinds.size()
			+ " keybinds in your current configuration");
		int settings = 0;
		for(Mod mod : wurst.mods.getAllMods())
			settings += mod.getSettings().size();
		wurst.chat.message(">" + settings + " settings");
	}
}
