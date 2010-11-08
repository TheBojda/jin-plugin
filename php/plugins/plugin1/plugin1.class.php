<?php
	
	require_once "lib/jin_plugin/Plugin.class.php";
	
	class TestHookProcessor implements ActionProcessor {
			
		public function call(&$context) {
			echo "Test hook called!<br/>";
		}
			
	}
	
	class plugin1 implements Plugin {
		
		public function init($pluginManager) {
			echo "plugin1 initialized!<br/>";
			
			$testService = $pluginManager->getService("testService");
			$testService->testFunction();
			
			$pluginManager->addActionProcessor("test_hook", new TestHookProcessor);
			$context = array();
			$pluginManager->callAction("test_action", $context);
		}
		
	}
	
?>