/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import net.minecraft.network.play.client.C01PacketChatMessage;

@Cmd.Info(help = "Leaves the current server or changes the mode of AutoLeave.",
	name = "leave",
	syntax = {"[chars|quit]", "mode chars|quit"})
public class LeaveCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length > 2)
			syntaxError();
		if(mc.isIntegratedServerRunning()
			&& mc.thePlayer.sendQueue.getPlayerInfo().size() == 1)
			error("Cannot leave server when in singleplayer.");
		switch(args.length)
		{
			case 0:
				disconnectWithMode(wurst.options.autoLeaveMode);
				break;
			case 1:
				if(args[0].equalsIgnoreCase("taco"))
					for(int i = 0; i < 128; i++)
						mc.thePlayer.sendAutomaticChatMessage("Taco!");
				else
					disconnectWithMode(parseMode(args[0]));
				break;
			case 2:
				wurst.options.autoLeaveMode = parseMode(args[1]);
				wurst.files.saveOptions();
				wurst.chat
					.message("AutoLeave mode set to \"" + args[1] + "\".");
				break;
			default:
				break;
		}
	}
	
	private void disconnectWithMode(int mode)
	{
		switch(mode)
		{
			case 0:
				mc.theWorld.sendQuittingDisconnectingPacket();
				break;
			case 1:
				mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(
					"§"));
				break;
			default:
				break;
		}
	}
	
	private int parseMode(String input) throws SyntaxError
	{
		if(input.equalsIgnoreCase("quit"))
			return 0;
		else if(input.equalsIgnoreCase("chars"))
			return 1;
		syntaxError("Invalid mode: " + input);
		return 0;
	}
}
