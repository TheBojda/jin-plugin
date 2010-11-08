<?php
	
	include "lib/jin_plugin/PluginManagerFactory.class.php";

	$pm = PluginManagerFactory::createPluginManager('plugins');
	$pm->installPlugin('http://localhost/jin-plugin-php/update_test.php');
	var_dump($pm->checkPluginUpdates());
	$pm->updatePlugin('plugin3');
	$pm->init();

?>