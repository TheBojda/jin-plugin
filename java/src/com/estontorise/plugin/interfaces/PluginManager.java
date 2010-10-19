package com.estontorise.plugin.interfaces;

import java.io.FileNotFoundException;

public interface PluginManager {

	public void init() throws FileNotFoundException;

	public void registerService(String name, Service service);
	public Service getService(String string);

}
