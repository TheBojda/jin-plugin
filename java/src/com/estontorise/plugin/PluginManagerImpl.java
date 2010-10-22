package com.estontorise.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.estontorise.plugin.interfaces.ActionProcessor;
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
			if(!pluginDir.getName().startsWith("."))
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

	private class ActionProcessorElement implements Comparable<ActionProcessorElement> {

		private ActionProcessor processor;
		private int priority;
				
		public ActionProcessorElement(ActionProcessor processor, int priority) {
			this.processor = processor;
			this.priority = priority;
		}

		@Override
		public int compareTo(ActionProcessorElement o) {
			return o.priority - this.priority;
		}

		public ActionProcessor getProcessor() {
			return processor;
		}
		
	}

	private class ActionProcessorList {
		
		private List<ActionProcessorElement> actionProcessorElements = new ArrayList<ActionProcessorElement>();

		public void addActionProcessor(ActionProcessor processor, int priority) {
			actionProcessorElements.add(new ActionProcessorElement(processor, priority));
			Collections.sort(actionProcessorElements);
		}

		public void removeActionProcessor(ActionProcessor processor) {
			ActionProcessorElement element = null;
			for(ActionProcessorElement ape : actionProcessorElements) {
				if(ape.getProcessor() == processor)
					element = ape;
			}
			if(element != null)
				actionProcessorElements.remove(element);
		}

		public void call(Map<String, Object> context) {
			for(ActionProcessorElement ape : actionProcessorElements) {
				ape.getProcessor().call(context);
			}
		}
		
	}
	
	private Map<String, ActionProcessorList> actionProcessors = new HashMap<String, ActionProcessorList>(); 
		
	@Override
	public void addActionProcessor(String actionName, ActionProcessor processor) {
		addActionProcessor(actionName, processor, 0);
	}

	@Override
	public void addActionProcessor(String actionName,
			ActionProcessor processor, int priority) {
		ActionProcessorList apl = actionProcessors.get(actionName);
		if(apl == null)
			actionProcessors.put(actionName, apl = new ActionProcessorList());
		apl.addActionProcessor(processor, priority);
	}

	@Override
	public void removeActionProcessor(String actionName,
			ActionProcessor processor) {
		ActionProcessorList apl = actionProcessors.get(actionName);
		if(apl == null)
			return;
		apl.removeActionProcessor(processor);
	}

	@Override
	public void callAction(String actionName, Map<String, Object> context) {
		ActionProcessorList apl = actionProcessors.get(actionName);
		if(apl == null)
			return;
		apl.call(context);
	}

}
