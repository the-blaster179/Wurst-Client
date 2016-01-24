/*
 * Copyright © 2014 - 2016 | Alexander01998 and contributors
 * All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.navigator.settings;

import com.google.gson.JsonObject;

import tk.wurst_client.navigator.gui.NavigatorFeatureScreen;

public interface NavigatorSetting
{
	public void addToFeatureScreen(NavigatorFeatureScreen featureScreen);
	
	public void save(JsonObject json);
	
	public void load(JsonObject json);
	
	public void update();
}
