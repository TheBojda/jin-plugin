package com.estontorise.plugin.test;

import java.io.FileNotFoundException;

import com.estontorise.plugin.PluginManagerFactory;
import com.estontorise.plugin.interfaces.PluginManager;

public class PluginTest {

	public static void main(String[] args) throws FileNotFoundException {
		PluginManager pm = PluginManagerFactory.createPluginManager("plugins");
		pm.init();
	}

}
