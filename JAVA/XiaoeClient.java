package com.xiaoe_tech.cloud.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class XiaoeClient {
    private static String contentType = "application/json;charset=utf-8";
    private static String grant_type = "client_credential";

    private String app_id = "apphIl2eyC00000";
    private String client_id = "xopntCygI900000";
    private String secret_key = "C9GtUJPyHJYP3uSh3Eg4VIiq2u7xxxxx";

    private TokenManager tokenManager = new TokenManager(app_id, client_id, secret_key, grant_type);


    public Map request(String method,String url, String postParams) {
        try {
            //设置链接
            URL urlGet = new URL(url);
            //启动链接
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            //设置链接参数与要求
            http.setRequestMethod(method.toUpperCase());
            http.setUseCaches(false);
            http.setConnectTimeout(5000);
            http.setReadTimeout(5000);
            http.setRequestProperty("Content-Type", contentType);
            http.setDoOutput(true);
            http.setDoInput(true);
            PrintWriter writer = new PrintWriter(http.getOutputStream());
            JSONObject jsonObject = JSONObject.parseObject(postParams);
            jsonObject.put("access_token", this.tokenManager.getAccessToken());
            writer.write(JSON.toJSONString(jsonObject));
            writer.flush();
            //连接
            http.connect();
            InputStream inputStream = http.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String buffer = "";
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuilder.append(buffer);
            }
            bufferedReader.close();
            http.disconnect();
            return (Map) JSONObject.parseObject(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
