<?php

	require_once "lib/jin_plugin/Plugin.class.php";
	require_once "lib/jin_plugin/ActionProcessor.class.php";
	require_once "TestService.class.php";

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
			echo "plugin2 initialized!<br/>";
			
			$pluginManager->registerService("testService", new TestService);
			
			$pluginManager->addActionProcessor("test_action", new TestAction($pluginManager));
		}
		
	}
	
?>