package com.estontorise.plugin.test;

import java.util.HashMap;
import java.util.Map;

import com.estontorise.plugin.interfaces.ActionProcessor;
import com.estontorise.plugin.interfaces.Plugin;
import com.estontorise.plugin.interfaces.PluginManager;

public class Plugin1 implements Plugin {

	@Override
	public void init(PluginManager pm) {
		System.out.println("plugin1 initialized!");
		
		// service test
		TestService testService = (TestService) pm.getService("testService");
		testService.testFunction();
		
		// action test
		pm.addActionProcessor("test_hook", new ActionProcessor() {
			
			@Override
			public void call(Map<String, Object> context) {
				System.out.println("Test hook called!");
			}
			
		});
		pm.callAction("test_action", new HashMap<String, Object>());
	}

}
