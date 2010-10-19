package com.estontorise.plugin.test;

import com.estontorise.plugin.interfaces.Plugin;
import com.estontorise.plugin.interfaces.PluginManager;

public class Plugin2 implements Plugin {

	@Override
	public void init(PluginManager pm) {
		System.out.println("plugin2 initialized!");
		pm.registerService("testService", new TestService());
	}

}
