<?php
require_once __DIR__ . '/Http.php';

class TokenManage
{
    const URL = "https://api.xiaoe-tech.com/token";
    private $app_id = "appXXXXXXXXXX";
    private $client_id = "xopXXXXXXXXXX";
    private $secret_key = "XXXXXXXXXX";
    private $grant_type = "client_credential";


    public function getToken()
    {
        $filePath = dirname(dirname(__FILE__)) . "\\" . $this->app_id . ".json";
        if(file_exists($filePath)){
           return $this->getFile($filePath);
        }else{
            return $this->requestToken();
        }

    }

    public function requestToken(){
        $params = [
            "app_id" => $this->app_id,
            "client_id" => $this->client_id,
            "secret_key" => $this->secret_key,
            "grant_type" => $this->grant_type
        ];
        $result = Http::curlGet(self::URL, $params);

        if (!empty($result['body']) && $result['body']['code'] == 0) {
            $this->writeFile($result['body']['data']);
            return $result['body']['data']['access_token'];
        }
        return false;
    }

    public function writeFile($data)
    {
        $file = fopen($this->app_id . ".json", "w") or die("Unable to open file!");
        $fileData = [
            "access_token" => $data['access_token'],
            "expires" => time() + $data['expires_in']
        ];
        fwrite($file, json_encode($fileData));
        fclose($file);
    }

    public function getFile($filePath){
        $json = file_get_contents($filePath);
        $tokenInfo = json_decode($json, true);
        if($tokenInfo['expires'] > time()) {
            return $tokenInfo['access_token'];
        }else{
            return $this->requestToken();
        }
    }
}