package com.xiaoe_tech.cloud.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TokenManager {
    private static final long MAX_TIME = 7200 * 1000;
    private static final String BASE_URL = "https://api.xiaoe-tech.com/token";

    private String app_id = "";
    private String client_id = "";
    private String secret_key = "";
    private String grant_type = "";

    public TokenManager(String app_id, String client_id, String secret_key, String grant_type) {
        this.app_id = app_id;
        this.client_id = client_id;
        this.secret_key = secret_key;
        this.grant_type = grant_type;
    }

    public synchronized String getAccessToken() {
        String access_token = "";
        File file = new File("access_token.json");
        try {
            //文件不存在，则创建文件
            if (!file.exists()) file.createNewFile();
            //若文件为空，则第一次访问
            if (file.length() == 0) {
                //获取access_token写入文件
                access_token = this.get();
                HashMap<String, String> map = new HashMap<>();
                map.put("access_token", access_token);
                map.put("expires_in", System.currentTimeMillis() + "");
                this.writeToken(file, map);
            } else {
                //从磁盘读取文件
                String json = this.readToken(file);
                JSONObject jsonObject = JSONObject.parseObject(json);
                if (jsonObject.get("expires_in") != null) {
                    long saveTime = Long.parseLong((String) jsonObject.get("expires_in"));
                    long nowTime = System.currentTimeMillis();
                    long remainTime = nowTime - saveTime;
                    //若间隔时间小于2小时
                    if (remainTime < MAX_TIME) {
                        access_token = (String) jsonObject.get("access_token");
                    } else {
                        //若间隔时间大于等于2小时，需要刷新access_token,重新写入文件
                        access_token = this.get();
                        HashMap<String, String> map = new HashMap<>();
                        map.put("access_token", access_token);
                        map.put("expires_in", System.currentTimeMillis() + "");
                        this.writeToken(file, map);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return access_token;
    }

    //手动强制刷新缓存
    public String refreshAccessToken() {
        File file = new File("access_token.json");
        if (file.exists()) file.delete();
        String newAccessToken = getAccessToken();
        return newAccessToken;
    }

    //token持久化到磁盘
    private void writeToken(File file, Map map) {
        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            String json = JSON.toJSONString(map);
            fos.write(json.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //从磁盘读取token
    private String readToken(File file) {
        //若文件存在access_token，则读取access_token
        FileInputStream fis = null;
        String json = "";
        try {
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];

            int len;
            while ((len = fis.read(b)) != -1) {
                json += new String(b, 0, len);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    //发起get请求获取access_token
    private String get() {
        String urlString = BASE_URL +
                "?app_id=" + this.app_id +
                "&client_id=" + this.client_id +
                "&secret_key=" + this.secret_key +
                "&grant_type=" + this.grant_type;
        String result = null;
        try {
            URL reqURL = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) reqURL.openConnection();
            InputStreamReader isr = new InputStreamReader(httpURLConnection.getInputStream());
            char[] chars = new char[1024];
            result = "";
            int len;
            while ((len = isr.read(chars)) != -1) {
                result += new String(chars, 0, len);
            }
            isr.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        Map data = (Map)jsonObject.get("data");
        String access_token = (String) data.get("access_token");
        if (access_token!= null) {
            return access_token;
        } else {
            return null;
        }
    }
}
