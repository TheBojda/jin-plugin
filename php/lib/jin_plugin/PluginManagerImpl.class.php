<?php

	require_once "lib/spyc/spyc.php";

	class PluginManagerImpl {
	
		public function __construct($pluginsDir) {
			$this->pluginsDir = $pluginsDir;
			$this->loadedPlugins = array();
			$this->services = array();
			$this->actionProcessors = array();
		}
		
		private function loadPlugin($pluginDir) {
			if(in_array($pluginDir, $this->loadedPlugins))
				return;
			$plugin_conf = spyc_load_file($pluginDir . "/plugin.yaml");
			$plugin_class = $plugin_conf['plugin_class'];
			$deps = $plugin_conf['dependencies'];
			if($deps)
				$this->loadPluginDependencies($deps);
			include($pluginDir . '/' . $plugin_class . ".class.php");
			$plugin = new $plugin_class;
			$plugin->init($this);
			$this->loadedPlugins[] = $pluginDir;
		}
		
		private function loadPluginDependencies($deps) {
			$deps = split(',', $deps);
			foreach($deps as $dep)
				$this->loadPlugin($this->pluginsDir . '/' . $dep);
		}
		
		public function init() {
			$dh = opendir($this->pluginsDir);
			while ($pluginDir = readdir($dh)) {
				if(strpos($pluginDir, '.') === 0)
					continue;
				$this->loadPlugin($this->pluginsDir . '/' . $pluginDir);
			}
			closedir($dh);		
		}
		
		public function registerService($name, $service) {
			$this->services[$name] = $service;
		}
		
		public function getService($name) {
			return $this->services[$name];
		}
		
		public function addActionProcessor($actionName, $processor) {
			$actionProcessors = &$this->actionProcessors;
			if(!isset($actionProcessors[$actionName]))
				$actionProcessors[$actionName] = array();
			$actionProcessors[$actionName][] = $processor;	
		}
		
		public function callAction($actionName, &$context) {
			$actionProcessors = &$this->actionProcessors;
			$actionList = $actionProcessors[$actionName];
			foreach($actionList as $action)
				$action->call($context);
		}
		
		private function getPluginUpdates($pluginDir) {
			$plugin_conf = spyc_load_file($this->pluginsDir . '/' . $pluginDir .  "/plugin.yaml");
			if(!isset($plugin_conf['update_url']))
				return;
			if(!isset($plugin_conf['version']))
				return;
			$update_url = $plugin_conf['update_url'];
			$version = $plugin_conf['version'];
			$updates = json_decode(file_get_contents($update_url . '?version=' . $version));
			return $updates;
		}
		
		public function checkPluginUpdates() {
			$dh = opendir($this->pluginsDir);
			$result = array();
			while ($pluginDir = readdir($dh)) {
				if(strpos($pluginDir, '.') === 0)
					continue;
				$result[$pluginDir] = $this->getPluginUpdates($pluginDir);
			}
			closedir($dh);	
			return $result;
		}
		
		private function inner_download($url, $file) {
			$url_handle = fopen($url, "r");
			$file_handle = fopen($file, "w");
			while(!feof($url_handle)) {
				$buffer = fread($url_handle, 8192);
				fwrite($file_handle, $buffer);
			}
			fclose($file_handle);
			fclose($url_handle);
		}
		
		private function unzip($zip_file, $dir) {
			$zip = zip_open($zip_file);
			while($zip_entry = zip_read($zip)) {
				$entry_name = zip_entry_name($zip_entry);
				$file = $dir . '/' . $entry_name;
				$parent_dir = dirname($file);
				if(!file_exists($parent_dir))
					mkdir($parent_dir, 0, TRUE);
				if(is_dir($file)) 
					continue;
				if(substr($file, -1) == '/')
					continue;
				$fp = fopen($file, "w");
				if (zip_entry_open($zip, $zip_entry, "r")) {
					$buf = zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));
					fwrite($fp, $buf);
					zip_entry_close($zip_entry);
				}
				fclose($fp);
			}
			zip_close($zip);
		}
		
		private function downloadAndInstallUpdates($updates) {
			foreach($updates as $update) {
				$file = $this->pluginsDir . '/' . md5(uniqid()) . '.zip';
				$this->inner_download($update->url, $file);
				$this->unzip($file, $this->pluginsDir);
				unlink($file);
			}
		}

		public function installPlugin($plugin_site_url) {
			$updates = json_decode(file_get_contents($plugin_site_url));
			$this->downloadAndInstallUpdates($updates);
		}
		
		public function updatePlugin($pluginDir) {
			$updates = $this->getPluginUpdates($pluginDir);
			$this->downloadAndInstallUpdates($updates);
		}
		
	}
	
?>