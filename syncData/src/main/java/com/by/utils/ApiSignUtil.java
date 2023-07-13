package com.by.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class ApiSignUtil {
    private static final String HMAC_SHA256 = "HmacSHA256";
//    private static final String OPEN_KEY = "1FFEF58CF10E484EA0D3FF69680E3E0A";
//    private static final String SECRET_KEY = "77520E24AD234EDF9E9A80CD78462277";
    private static final Integer RANDOM_LENGTH = 5;
    private static final String ENCODING = "UTF-8";

//    private static final String TEST_SERVER = "https://sop-sit01.sheincorp.cn";
//    private static final String API_PATH = "/sop-api/fsp/sync-inventory";

    public static String getData() {
        return "{'supplierCode': '',\n" +
                "            'supplierName': '',\n" +
                "            'storeName': 'test',\n" +
                "            'cardNo': '111CF2004181051',\n" +
                "            'barcode': '11CF2004181051002',\n" +
                "            'weight': '25.50',\n" +
                "            'productCode': '11036',\n" +
                "            'productName': '132支纯棉拉架食毛',\n" +
                "            'colorCode': 'C041969',\n" +
                "            'color': '无尘环保黑',\n" +
                "            'fabricWidth': '170cm实用',\n" +
                "            'grammWeight': '180g/m2',\n" +
                "            'inventory': '1'}";

    }

    public static String hmacSha256(String message, String secret) {
        String hash = "";
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKey);
            byte[] bytes = mac.doFinal(message.getBytes());
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String tmp;
        for (int n = 0; b != null && n < b.length; n++) {
            tmp = Integer.toHexString(b[n] & 0XFF);
            if (tmp.length() == 1) {
                hs.append('0');
            }
            hs.append(tmp);
        }
        return hs.toString().toLowerCase();
    }

    public static String base64Encode(String data) {
        String result = "";
        if (StringUtils.isNotBlank(data)) {
            result = new String(Base64.encodeBase64(data.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        }
        return result;
    }
    
    public static String Post(String url, String json,String OPEN_KEY, String SECRET_KEY,String API_PATH) throws Exception {
        // 创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        /**
         * setConnectTimeout：设置连接超时时间，单位毫秒。
         * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
         * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
         * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
         */
        RequestConfig requestConfig = RequestConfig.custom().build();
        httpPost.setConfig(requestConfig);
        
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
//        System.out.println(System.currentTimeMillis()+"");
//        System.out.println(timestamp);
        String signString = OPEN_KEY + "&" + timestamp+ "&" + API_PATH;
        String randomKey = UUID.randomUUID().toString().substring(0, RANDOM_LENGTH);
//        System.out.println(randomKey);
        String secretKey = SECRET_KEY + randomKey;
        String hashValue = ApiSignUtil.hmacSha256(signString, secretKey);
        String base64Value = ApiSignUtil.base64Encode(hashValue);
        String signature = randomKey + base64Value;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("x-lt-openKeyId", OPEN_KEY);
        headers.put("x-lt-timestamp", timestamp);
//        System.out.println(signature);
        headers.put("x-lt-signature", signature);
        // 封装请求头
        if (headers != null) {
            Set<Map.Entry<String, String>> entrySet = headers.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                // 设置到请求头到HttpRequestBase对象中
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 封装请求参数
        if (json != null) {
            // 设置到请求的http对象中
            httpPost.setEntity(new StringEntity(json, ENCODING));
        }

        try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost);) {
            // 执行请求并获得响应结果
            return EntityUtils.toString(httpResponse.getEntity(), ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String doPost(String url, Map<String, String> headers, String json) throws Exception {
        // 创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        /**
         * setConnectTimeout：设置连接超时时间，单位毫秒。
         * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
         * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
         * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
         */
        RequestConfig requestConfig = RequestConfig.custom().build();
        httpPost.setConfig(requestConfig);
        // 封装请求头
        if (headers != null) {
            Set<Map.Entry<String, String>> entrySet = headers.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                // 设置到请求头到HttpRequestBase对象中
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 封装请求参数
        if (json != null) {
            // 设置到请求的http对象中
            httpPost.setEntity(new StringEntity(json, ENCODING));
        }

        try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost);) {
            // 执行请求并获得响应结果
            return EntityUtils.toString(httpResponse.getEntity(), ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) throws Exception {
//        Date date = new Date();
//        String timestamp = String.valueOf(date.getTime());
//        System.out.println(System.currentTimeMillis()+"");
//        System.out.println(timestamp);
//        String signString = OPEN_KEY + "&" + timestamp+ "&" + API_PATH;
//        String randomKey = UUID.randomUUID().toString().substring(0, RANDOM_LENGTH);
//        System.out.println(randomKey);
//        String secretKey = SECRET_KEY + randomKey;
//        String hashValue = ApiSignUtil.hmacSha256(signString, secretKey);
//        String base64Value = ApiSignUtil.base64Encode(hashValue);
//        String signature = randomKey + base64Value;
//        String url = TEST_SERVER + API_PATH;
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json;charset=UTF-8");
//        headers.put("x-lt-openKeyId", OPEN_KEY);
//        headers.put("x-lt-timestamp", timestamp);
//        System.out.println(signature);
//        headers.put("x-lt-signature", signature);
//        System.out.println(doPost(url, headers, getData()));
//
//        String s1 = "6497eMmMzOTA5NDAyMzYwZTQzNjQ5MjRhODg5ZDQwMTI4YTVlOTRjM2RhOTJjNWVjNzdiOGUxYjUwNWNiNDI3MjYxOQ==";
//        String s2 = "6497eMmMzOTA5NDAyMzYwZTQzNjQ5MjRhODg5ZDQwMTI4YTVlOTRjM2RhOTJjNWVjNzdiOGUxYjUwNWNiNDI3MjYxOQ==";
//        System.out.println(s1.equals(s2));
//    }
}