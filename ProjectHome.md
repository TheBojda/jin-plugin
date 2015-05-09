jin-plugin is a simple plugin framework for Java and PHP.

There are some existing plugin framework for Java (OSGI, jpf, etc.) and PHP, but all of them was too complex for my needs. jin-plugin is a minimalistic solution.

Look at a Java example, which loads the plugins from the 'plugins' directory:

```
PluginManager pm = PluginManagerFactory.createPluginManager("plugins");
pm.init();
```

and the same in PHP:

```
$pm = PluginManagerFactory::createPluginManager('plugins');
$pm->init();
```

Both of the codes load plugins from the plugins directory. Every plugin is a directory in the plugins folder. All of them contains a plugin.yaml file which describes the plugin dependencies and the plugin class.

Example plugin.yaml in Java:

```
plugin_class: com.estontorise.plugin.test.Plugin1
dependencies: plugin2
```

and PHP:

```
plugin_class: plugin1
dependencies: plugin2
```

The plugin directory contains the plugin classes. If you use PHP, the classes are directly in the plugin directory, if you use Java, classes must be in the classes directory, and  libraries in the lib directory.

The plugin class is very simple. It implements the Plugin interface, which has a simple init method. Plugins can register services, which can be used by other plugins.

An example in Java:

```
public class Plugin2 implements Plugin {

    @Override
    public void init(PluginManager pm) {
        System.out.println("plugin2 initialized!");
        pm.registerService("testService", new TestService());
    }

}

public class Plugin1 implements Plugin {

	@Override
	public void init(PluginManager pm) {
		System.out.println("plugin1 initialized!");
		TestService testService = (TestService) pm.getService("testService");
		testService.testFunction();
	}

}
```

and the same in PHP:

```
class plugin2 implements Plugin {
	
	public function init($pluginManager) {
		echo "plugin2 initialized!<br/>";
		$pluginManager->registerService("testService", new TestService);
	}
		
}

class plugin1 implements Plugin {
	
	public function init($pluginManager) {
		echo "plugin1 initialized!<br/>";
		$testService = $pluginManager->getService("testService");
		$testService->testFunction();
	}
	
}
```

# Action support #

Actions are hooks (or callbacks) which can be called on different places in the plugin or service. Every action is a list of action processor, and every service can add its own processors to the specified action. For example: you have a blog plugin which renders the list of blog entires. If you call an action if an entry is entered, you make possible for 3rd parties to add something to the end of the entries. It can be a "share this" line, or a Facebook Like button, etc.

Look at a simple example in Java for the action support. The first code sample defines the test\_action which do something, call another action (test\_hook), and after that do something other.

```
public void init(final PluginManager pm) {
	pm.addActionProcessor("test_action", new ActionProcessor() {
			
		@Override
		public void call(Map<String, Object> context) {
			System.out.println("Before test_hook ...");
			pm.callAction("test_hook", context);
			System.out.println("After test_hook ...");				
		}
			
	});
}
```

and the same in PHP:

```
class TestAction implements ActionProcessor {

	public function __construct($pluginManager) {
		$this->pluginManager = $pluginManager;
	}

	public function call(&$context) {
		echo "Before test_hook ...<br/>";
		$this->pluginManager->callAction("test_hook", $context);
		echo "After test_hook ...<br/>";
	}
	
}

class plugin2 implements Plugin {
		
	public function init($pluginManager) {
		$pluginManager->addActionProcessor("test_action", new TestAction($pluginManager));
	}
		
}
```

The second code registers an action processor to the test\_hook, and calls the test\_action.

```
public void init(PluginManager pm) {
	pm.addActionProcessor("test_hook", new ActionProcessor() {
			
		@Override
		public void call(Map<String, Object> context) {
			System.out.println("Test hook called!");
		}
			
	});
	pm.callAction("test_action", new HashMap<String, Object>());
}
```

and the same in PHP:

```
class TestHookProcessor implements ActionProcessor {
			
	public function call(&$context) {
		echo "Test hook called!<br/>";
	}
			
}
	
class plugin1 implements Plugin {
		
	public function init($pluginManager) {
		$pluginManager->addActionProcessor("test_hook", new TestHookProcessor);
		$context = array();
		$pluginManager->callAction("test_action", $context);
	}
		
}
```

# Update support #

jin-plugin has an easy update mechanism. The plugin yaml can contain an update url and a version number. Look at a simple example:

```
plugin_class: plugin3
dependencies: plugin2
update_url: http://localhost/jin-plugin-php/update_test.php
version: 1.0
```

The update\_url points to the update site. The update site can be called with or without version parameter. Both of the cases the update site gives a list of urls in json format. If the version parameter is not set, the update site gives back the url of the actual release, if it is set, the script gives beck the urls of the updates to upgrade the plugin to the actual version. Every URL points to a zip file. You can use the update functionality through 3 method.

The first is `installPlugin`. The parameter of the method is the url of an update site. This method calls the update site URL without any parameter, downloads the zip file from the given url, unpacks it to the plugin directory, and deletes zip file.

Usage: `$pm->installPlugin('http://localhost/jin-plugin-php/update_test.php');`

The `checkPluginUpdates` method checks the update site of the plugins, and gives back an associative list, where an url list is assigned to every plugin.

The third method is `updatePlugin`, which downloads and installs the updates for the plugin which name are given in the parameter list.

Usage: `$pm->updatePlugin('plugin3');`

See also:
> http://code.google.com/p/jin-template/ - simple template engine for Java and PHP, using plain HTML as source

> http://code.google.com/p/jin-webcore/ - minimalistic web framework for Java and PHP

&lt;wiki:gadget url="http://www.ohloh.net/p/486106/widgets/project\_users\_logo.xml" height="43" border="0"/&gt;

<a href='https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=LMQGC6YTEQKE4&item_name=Beer'>
<img src='http://www.paypal.com/en_US/i/btn/x-click-but04.gif' /><br />Buy me some beer if you like my code ;)</a>

If you like the code, look at my other projects on http://code.google.com/u/TheBojda/.

If you have any question, please feel free to contact me at thebojda AT gmail DOT com.