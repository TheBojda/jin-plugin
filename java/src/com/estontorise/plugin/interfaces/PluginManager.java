package com.estontorise.plugin.interfaces;

import java.io.FileNotFoundException;
import java.util.Map;

public interface PluginManager {

	public void init() throws FileNotFoundException;

	// service management
	
	public void registerService(String name, Service service);
	public Service getService(String string);

	// action management
	
	public void addActionProcessor(String actionName, ActionProcessor processor);
	public void addActionProcessor(String actionName, ActionProcessor processor, int priority);
	public void removeActionProcessor(String actionName, ActionProcessor processor);
	public void callAction(String actionName, Map<String, Object> context);
	
}
