/*
 * Copyright © 2014 - 2015 | Alexander01998 and contributors
 * All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.navigator;

public class NavigatorKeybind
{
	private String command;
	private String description;
	private int key;
	
	public NavigatorKeybind(String command, String description, int key)
	{
		this.command = command;
		this.description = description;
		this.key = key;
	}
	
	public String getCommand()
	{
		return command;
	}
	
	public void setCommand(String command)
	{
		this.command = command;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public int getKey()
	{
		return key;
	}
	
	public void setKey(int key)
	{
		this.key = key;
	}
}
