<?php
require_once __DIR__ . '/libs/Client.php';
$client = new Client();
$url = "https://api.xiaoe-tech.com/xe.user.batch.get/1.0.0";
$method = "post";
$params = ['page' => 1, 'page_size' => 2];
$result = $client->request($method, $url, $params);
print_r($result);