<?php

	require_once "lib/jin_plugin/Plugin.class.php";
	require_once "TestService.class.php";

	class plugin2 implements Plugin {
	
		public function init($pluginManager) {
			echo "plugin2 initialized!<br/>";
			$pluginManager->registerService("testService", new TestService);
		}
		
	}
	
?>