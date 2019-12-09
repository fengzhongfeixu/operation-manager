package com.sugon.gsq.om.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/*
 * ClassName: HttpUtil
 * Author: Administrator
 * Date: 2019/9/11 11:12
 */
public class HttpUtil {

    private HttpUtil(){}

    /**
     * 返回code响应码和请求内容
     * @param url
     * @param message
     * @return
     */
    public static Map<String, Object> sendPost(String url, String message) {
        Map<String, Object> result = new HashMap<>();
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try{
            HttpPost post = new HttpPost(url);
            httpClient = HttpClients.createDefault();

            StringEntity se = new StringEntity(message, Charset.forName("utf-8"));
            se.setContentEncoding("utf-8");
            se.setContentType("application/json");

            post.addHeader(new BasicHeader("Content-Type", "application/json; charset=utf-8"));
            post.setHeader(new BasicHeader("Accept", "application/json; charset=utf-8"));
            post.setEntity(se);

            response = httpClient.execute(post);
            Integer statusCode = response.getStatusLine().getStatusCode();
            //拼装返回值
            result.put("code", statusCode);
            result.put("content",entityToString(response.getEntity()));

        } catch (Exception e){
            //http请求错误
            result.put("code", new Integer(5000));
            result.put("content", e.getMessage());
        } finally {
            try{
                if(response != null){
                    response.close();
                }
                if(httpClient != null){
                    httpClient.close();
                }
            } catch (IOException e) {
                //关闭请求流错误
                result.put("code", result.get("code") != null ? ((Integer)result.get("code") + 10000) : 10000);
                result.put("content", e.getMessage());
            }
        }
        return result;
    }

    public static String sendGet(String url) throws IOException {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = null;
        response = httpClient.execute(get);
        if(response != null && response.getStatusLine().getStatusCode() == 200){
            HttpEntity entity = response.getEntity();
            result = entityToString(entity);
        }
        return result;
    }

    private static String entityToString(HttpEntity entity) throws IOException {
        String result = null;
        if(entity != null){
            long lenth = entity.getContentLength();
            if(lenth != -1 && lenth < 2048){
                result = EntityUtils.toString(entity,"UTF-8");
            } else {
                InputStreamReader reader1 = new InputStreamReader(entity.getContent(), "UTF-8");
                CharArrayBuffer buffer = new CharArrayBuffer(2048);
                char[] tmp = new char[1024];
                int l;
                while((l = reader1.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                }
                result = buffer.toString();
            }
        }
        return result;
    }

}
