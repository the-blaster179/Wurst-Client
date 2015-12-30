/*
 * Copyright © 2014 - 2015 | Alexander01998 and contributors
 * All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.navigator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import tk.wurst_client.WurstClient;
import tk.wurst_client.commands.CmdManager;
import tk.wurst_client.mods.ModManager;

public class Navigator
{
	private ArrayList<NavigatorItem> navigatorList = new ArrayList<>();
	private final HashMap<String, Long> clicksMap = new HashMap<>();
	
	public Navigator()
	{
		// add mods
		Field[] modFields = ModManager.class.getFields();
		try
		{
			for(int i = 0; i < modFields.length; i++)
			{
				Field field = modFields[i];
				if(field.getName().endsWith("Mod"))
					navigatorList.add((NavigatorItem)field
						.get(WurstClient.INSTANCE.mods));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// add commands
		Field[] cmdFields = CmdManager.class.getFields();
		try
		{
			for(int i = 0; i < cmdFields.length; i++)
			{
				Field field = cmdFields[i];
				if(field.getName().endsWith("Cmd"))
					navigatorList.add((NavigatorItem)field
						.get(WurstClient.INSTANCE.commands));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void copyNavigatorList(ArrayList<NavigatorItem> list)
	{
		if(!list.equals(navigatorList))
		{
			list.clear();
			list.addAll(navigatorList);
		}
	}
	
	public void getSearchResults(ArrayList<NavigatorItem> list, String query)
	{
		list.clear();
		for(NavigatorItem mod : navigatorList)
			if(mod.getName().toLowerCase().contains(query)
				|| mod.getDescription().toLowerCase().contains(query))
				list.add(mod);
		list.sort(new SearchResultsComparator(query));
	}
	
	public long getClicks(String feature)
	{
		Long clicks = clicksMap.get(feature);
		if(clicks == null)
			clicks = 0L;
		return clicks;
	}
	
	public void addClick(String feature)
	{
		Long clicks = clicksMap.get(feature);
		if(clicks == null)
			clicks = 0L;
		clicks++;
		clicksMap.put(feature, clicks);
	}
	
	public void setClicks(String feature, long clicks)
	{
		clicksMap.put(feature, clicks);
	}
	
	public Iterator<Entry<String, Long>> getClicksIterator()
	{
		return clicksMap.entrySet().iterator();
	}
	
	public void sortFeatures()
	{
		navigatorList.sort(new Comparator<NavigatorItem>()
		{
			@Override
			public int compare(NavigatorItem o1, NavigatorItem o2)
			{
				long clicks1 = getClicks(o1.getName());
				long clicks2 = getClicks(o2.getName());
				if(clicks1 < clicks2)
					return 1;
				else if(clicks1 > clicks2)
					return -1;
				else
					return 0;
			}
		});
	}
}
