package com.estontorise.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.estontorise.plugin.interfaces.Plugin;
import com.estontorise.plugin.interfaces.PluginManager;
import com.estontorise.plugin.interfaces.Service;

public class PluginManagerImpl implements PluginManager {

	private String pluginsDir;
	private PluginClassLoader classLoader;
	private List<String> loadedPlugins = new ArrayList<String>();
	private Map<String, Service> services = new HashMap<String, Service>();

	public PluginManagerImpl(String pluginsDir) {
		this.pluginsDir = pluginsDir;
		this.classLoader = new PluginClassLoader();
	}

	@Override
	public void init() {
		File dh = new File(this.pluginsDir);
		for (File pluginDir : dh.listFiles())
			loadPlugin(pluginDir);
	}

	@SuppressWarnings("unchecked")
	private void loadPlugin(File pluginDir) {
		try {
			if (loadedPlugins.contains(pluginDir.getAbsolutePath()))
				return;
			classLoader.loadPlugin(pluginDir);
			Yaml yaml = new Yaml();
			Map<String, String> pluginConf = (Map<String, String>) yaml
					.load(new FileInputStream(
							new File(pluginDir, "plugin.yaml")));
			String plugin_class = pluginConf.get("plugin_class");
			String deps = pluginConf.get("dependencies");
			if (deps != null)
				loadPluginDependencies(deps);
			Class<?> pluginClass = classLoader.loadClass(plugin_class);
			Plugin plugin = (Plugin) pluginClass.newInstance();
			plugin.init(this);
			loadedPlugins.add(pluginDir.getAbsolutePath());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void loadPluginDependencies(String dependencies) {
		String[] deps = dependencies.split(",");
		for (String dep : deps)
			loadPlugin(new File(pluginsDir, dep));
	}

	@Override
	public void registerService(String name, Service service) {
		services.put(name, service);
	}

	@Override
	public Service getService(String name) {
		return services.get(name);
	}

}
