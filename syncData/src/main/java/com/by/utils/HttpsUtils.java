package com.by.utils;

import com.alibaba.fastjson.JSON;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.Map;

/**
 * @author : CLy
 * @ClassName : httpsQuery
 * @date : 2020/7/8 16:27
 **/
@Component
public class HttpsUtils {
	
	
    /**HTTPS
     * requestUrl ： 请求路径
     * requestMethod ： GET/POST请求方式，大写
     * param ：请求参数（可为空）
     * header：请求头部数据(可为空)
     * */
    public static String httpsRequest(String requestUrl,String requestMethod,String body,Map<String,String> header){
        StringBuffer buffer=null;
        try{
            //创建SSLContext
            SSLContext sslContext=SSLContext.getInstance("SSL");
            TrustManager[] tm={new MyX509TrustManager()};//用自定义认证
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());;
            //获取SSLSocketFactory对象
            SSLSocketFactory ssf=sslContext.getSocketFactory();
            URL url=new URL(requestUrl);
            HttpsURLConnection conn=(HttpsURLConnection)url.openConnection();//打开连接
            conn.setDoOutput(true);//开启输出
            conn.setDoInput(true);//开启输入
            conn.setUseCaches(false);//开启缓存
            conn.setRequestMethod(requestMethod);//设置访问方式
            /*设置头部参数*/
            //设置请求头部信息
            for (Map.Entry<String, String> entry : header.entrySet()) {
                conn.setRequestProperty(entry.getKey(),entry.getValue());
                //conn.setRequestProperty("Content-Type", "application/json;charset=utf8");
                //conn.setRequestProperty("请求头部参数名", "请求头部参数值");
            }
            //设置当前实例使用的SSLSoctetFactory
            conn.setSSLSocketFactory(ssf);
            conn.connect();//连接

            //往服务器端写入请求参数
            if(null!=body){
                OutputStream outputStream=conn.getOutputStream();
                outputStream.write(body.getBytes("utf-8"));
                outputStream.flush();//刷新
                outputStream.close();
            }
            //读取服务器端返回的内容
            InputStream inputStream=conn.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"utf-8");
            BufferedReader br=new BufferedReader(inputStreamReader);
            buffer=new StringBuffer();
            String line=null;
            while((line=br.readLine())!=null){
                buffer.append(line);
            }
            inputStream.close();
            inputStreamReader.close();
            br.close();
            conn.disconnect();//关闭连接
        }catch(Exception e){
            e.printStackTrace();
        }
        return buffer.toString();//返回响应数据
    }

    /**HTTPS 固定POST请求
     * requestUrl ： 请求路径
     * File ： 请求文件(可为空)
     * param ：请求参数（可为空）
     * header：请求头部数据(可为空)
     * */
    public static String httpsPostWithFile(String requestUrl,File file, Map<String,String> param, Map<String,String> header){
        StringBuffer buffer=null;
        try{
            //创建SSLContext
            SSLContext sslContext=SSLContext.getInstance("SSL");
            TrustManager[] tm={new MyX509TrustManager()};//用自定义认证
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());;
            //获取SSLSocketFactory对象
            SSLSocketFactory ssf=sslContext.getSocketFactory();
            URL url=new URL(requestUrl);
            HttpsURLConnection conn=(HttpsURLConnection)url.openConnection();//打开连接
            conn.setDoOutput(true);//开启输出
            conn.setDoInput(true);//开启输入
            conn.setUseCaches(false);//开启缓存
            conn.setRequestMethod("POST");//设置访问方式
            /*设置头部参数*/
            //设置请求头部信息
            for (Map.Entry<String, String> entry : header.entrySet()) {
                conn.setRequestProperty(entry.getKey(),entry.getValue());
                //conn.setRequestProperty("Content-Type", "application/json;charset=utf8");
                //conn.setRequestProperty("请求头部参数名", "请求头部参数值");
            }
            //设置当前实例使用的SSLSoctetFactory
            conn.setSSLSocketFactory(ssf);
            conn.connect();//连接
            //param请求参数转为json格式
            String outputStr =null;
            if (param.size()>0){
                JSON json = (JSON) JSON.toJSON(param);
                outputStr = json.toJSONString();
            }
            //往服务器端写入请求参数
            if(null!=outputStr){
                OutputStream outputStream=conn.getOutputStream();
                outputStream.write(outputStr.getBytes("utf-8"));//param请求参数写入
                //file写入
                FileInputStream fileInputStream =new FileInputStream(file);
                byte[] data = new byte[2048];
                int len = 0;
                while ((len = fileInputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
                outputStream.flush();//刷新
                fileInputStream.close();
                outputStream.close();
            }
            //读取服务器端返回的内容
            InputStream inputStream=conn.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"utf-8");
            BufferedReader br=new BufferedReader(inputStreamReader);
            buffer=new StringBuffer();
            String line=null;
            while((line=br.readLine())!=null){
                buffer.append(line);
            }
            inputStream.close();
            inputStreamReader.close();
            br.close();
            conn.disconnect();//关闭连接
        }catch(Exception e){
            e.printStackTrace();
        }
        return buffer.toString();//返回响应数据
    }
}

