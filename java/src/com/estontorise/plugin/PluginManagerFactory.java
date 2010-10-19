package com.estontorise.plugin;

import com.estontorise.plugin.interfaces.PluginManager;

public class PluginManagerFactory {

	public static PluginManager createPluginManager(String pluginsDir) {
		return new PluginManagerImpl(pluginsDir);
	}

}
