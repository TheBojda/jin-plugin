<?php
	
	require_once "lib/jin_plugin/Plugin.class.php";
	
	class plugin1 implements Plugin {
	
		public function init($pluginManager) {
			echo "plugin1 initialized!<br/>";
			$testService = $pluginManager->getService("testService");
			$testService->testFunction();
		}
		
	}
	
?>