<?php
require_once __DIR__ . '/TokenManage.php';
require_once __DIR__ . '/Http.php';

class Client
{

    public function request($method, $url, $params)
    {

        if (empty($method) && empty($url) && empty($params)) {
            return false;
        }

        $tokenMange = new TokenManage();
        $token = $tokenMange->getToken();
        if(!$token){
            return false;
        }

        $params['access_token'] = $token;
        if ($method == 'post') {
            $result = Http::curlPost($url, array(), $params, array());
        } else {
            $result = Http::curlGet($url, $params);
        }

        if (!empty($result['body']) && $result['body']['code'] == 0) {
            return $result['body'];
        }
        return false;

    }


}

