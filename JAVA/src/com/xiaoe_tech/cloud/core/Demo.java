package com.xiaoe_tech.cloud.core;
import java.util.Map;

public class Demo {

    public static void main(String[] args) {
        String url = "https://api.xiaoe-tech.com/xe.user.batch.get/1.0.0";
        String data = "{" +
                "  \"page\": 1," +
                "  \"page_size\": 3" +
                "}";
        XiaoeClient client = new XiaoeClient();
        Map result = client.request("post",url, data);
        System.out.println(result);
    }
}
