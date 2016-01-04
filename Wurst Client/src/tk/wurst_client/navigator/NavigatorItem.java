/*
 * Copyright © 2014 - 2015 | Alexander01998 and contributors
 * All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.navigator;

import java.util.ArrayList;

import org.darkstorm.minecraft.gui.component.basic.BasicSlider;

public interface NavigatorItem
{
	public String getName();
	
	public String getType();
	
	public String getDescription();
	
	public boolean isEnabled();
	
	public boolean isBlocked();
	
	public String[] getTags();
	
	public ArrayList<BasicSlider> getSettings();
	
	public ArrayList<NavigatorPossibleKeybind> getPossibleKeybinds();
	
	public String getPrimaryAction();
	
	public void doPrimaryAction();
	
	public String getTutorialPage();
}
