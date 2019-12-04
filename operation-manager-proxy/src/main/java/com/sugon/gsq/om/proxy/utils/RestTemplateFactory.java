package com.sugon.gsq.om.proxy.utils;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.*;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

/*
 * ClassName: RestTemplateFactory
 * Author: Administrator
 * Date: 2019/10/24 11:34
 */
public class RestTemplateFactory {

    public static RestTemplate Build(String proxyHost, Integer proxyPort){
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Proxy proxy= new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        requestFactory.setProxy(proxy);
        requestFactory.setReadTimeout(35000);
        requestFactory.setConnectTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new FastJsonHttpMessageConverter());
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new ResourceRegionHttpMessageConverter());
        messageConverters.add(new ProtobufHttpMessageConverter());
        messageConverters.add(new ProtobufJsonFormatHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

}
