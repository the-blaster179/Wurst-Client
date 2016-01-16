/*
 * Copyright © 2014 - 2016 | Alexander01998 and contributors
 * All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.special;

import java.lang.reflect.Field;

import tk.wurst_client.navigator.settings.CheckboxSetting;

@SpecialFeature.Info(description = "Controls what entities are targeted by "
	+ "other features (e.g. Killaura).", name = "Target")
public class TargetFeature extends SpecialFeature
{
	private boolean players = true;
	private boolean animals = true;
	private boolean monsters = true;
	private boolean golems = true;
	
	private boolean sleeping_players = false;
	private boolean invisible_players = false;
	private boolean invisible_mobs = false;
	
	private boolean teams = false;
	
	public TargetFeature()
	{
		for(Field field : TargetFeature.class.getFields())
		{
			if(!field.getType().equals(boolean.class))
				continue;
			
			String name =
				field.getName().substring(0, 1).toUpperCase()
					+ field.getName().substring(1).replace("_", " ");
			
			boolean checked = false;
			try
			{
				checked = field.getBoolean(this);
			}catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
			
			settings.add(new CheckboxSetting(name, checked)
			{
				@Override
				public void update()
				{
					try
					{
						field.setBoolean(TargetFeature.this, isChecked());
					}catch(IllegalArgumentException | IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "";
	}
	
	@Override
	public void doPrimaryAction()
	{	
		
	}

	public boolean isPlayers()
	{
		return players;
	}

	public void setPlayers(boolean players)
	{
		this.players = players;
	}

	public boolean isAnimals()
	{
		return animals;
	}

	public void setAnimals(boolean animals)
	{
		this.animals = animals;
	}

	public boolean isMonsters()
	{
		return monsters;
	}

	public void setMonsters(boolean monsters)
	{
		this.monsters = monsters;
	}

	public boolean isGolems()
	{
		return golems;
	}

	public void setGolems(boolean golems)
	{
		this.golems = golems;
	}

	public boolean isSleeping_players()
	{
		return sleeping_players;
	}

	public void setSleeping_players(boolean sleeping_players)
	{
		this.sleeping_players = sleeping_players;
	}

	public boolean isInvisible_players()
	{
		return invisible_players;
	}

	public void setInvisible_players(boolean invisible_players)
	{
		this.invisible_players = invisible_players;
	}

	public boolean isInvisible_mobs()
	{
		return invisible_mobs;
	}

	public void setInvisible_mobs(boolean invisible_mobs)
	{
		this.invisible_mobs = invisible_mobs;
	}

	public boolean isTeams()
	{
		return teams;
	}

	public void setTeams(boolean teams)
	{
		this.teams = teams;
	}
}
