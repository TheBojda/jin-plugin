<?php
	if(!$_GET['version'])
	{
		echo json_encode(array(
			array('url' => 'http://localhost/jin-plugin-php/release.zip')
		));
	} 
	else 
	{
		echo json_encode(array(
			array('url' => 'http://localhost/jin-plugin-php/update.zip')
		));
	}
?>