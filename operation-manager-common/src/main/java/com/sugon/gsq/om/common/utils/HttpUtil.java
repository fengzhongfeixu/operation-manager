package com.sugon.gsq.om.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.sugon.gsq.om.constant.Constant;
import com.sugon.gsq.om.db.entity.OmOperationLog;
import com.sugon.gsq.om.model.PairModel;
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

/*
 * ClassName: HttpUtil
 * Author: Administrator
 * Date: 2019/9/11 11:12
 */
public class HttpUtil {

    private HttpUtil(){}

    public static OmOperationLog sendPost(String url, String message) {
        try{
            HttpPost post = new HttpPost(url);
            CloseableHttpClient httpClient = HttpClients.createDefault();

            StringEntity se = new StringEntity(message, Charset.forName("utf-8"));
            se.setContentEncoding("utf-8");
            se.setContentType("application/json");

            post.addHeader(new BasicHeader("Content-Type", "application/json; charset=utf-8"));
            post.setHeader(new BasicHeader("Accept", "application/json; charset=utf-8"));
            post.setEntity(se);

            CloseableHttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode != 200){
                return new OmOperationLog()
                        .setStatus(Constant.STATUS_FAULT)
                        .setCode(statusCode + "")
                        .setOperation("Unimportance")
                        .setContent("http请求失败");
            }
            String result = entityToString(response.getEntity());
            PairModel agentResponse = JSONObject.parseObject(result, PairModel.class);
            response.close();
            httpClient.close();
            return new OmOperationLog()
                    .setStatus(Constant.STATUS_SUCCESS)
                    .setCode("200")
                    .setOperation(agentResponse.getKey())
                    .setContent(agentResponse.getValue());
        } catch (Exception e){
            return new OmOperationLog()
                    .setStatus(Constant.STATUS_FAULT)
                    .setCode("500")
                    .setOperation("Unimportance")
                    .setContent(e.getMessage());
        }
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
