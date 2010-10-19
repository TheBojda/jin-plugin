package com.estontorise.plugin.test;

import com.estontorise.plugin.interfaces.Plugin;
import com.estontorise.plugin.interfaces.PluginManager;

public class Plugin1 implements Plugin {

	@Override
	public void init(PluginManager pm) {
		System.out.println("plugin1 initialized!");
		TestService testService = (TestService) pm.getService("testService");
		testService.testFunction();
	}

}
