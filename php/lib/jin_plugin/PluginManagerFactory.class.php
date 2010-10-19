<?php
	require_once "PluginManagerImpl.class.php";

	class PluginManagerFactory {
		public static function createPluginManager($pluginDir) {
			return new PluginManagerImpl($pluginDir);
		}
	}
	
?>