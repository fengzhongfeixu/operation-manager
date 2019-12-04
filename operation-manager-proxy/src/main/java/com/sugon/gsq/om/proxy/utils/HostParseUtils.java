package com.sugon.gsq.om.proxy.utils;

import com.anthonynsimon.url.URL;
import com.anthonynsimon.url.exceptions.MalformedURLException;
import com.google.gson.Gson;
import com.sugon.gsq.om.proxy.entity.HostInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostParseUtils {

    private final static Logger log = LoggerFactory.getLogger(HostParseUtils.class);

    private final static String https = "https://";

    private final static String http = "http://";

    public static HostInfo parseHost(String urlString) {

        try {

            int defaultPort = StringUtils.startsWith(urlString, https) ? 443 : 80;

            URL url = URL.parse(urlString);

            log.info("url={}",new Gson().toJson(url));

            HostInfo hostInfo = new HostInfo();
//            hostInfo.setHost(url.getHost());
//            hostInfo.setPort(url.getPort());
              hostInfo.setHost("master");
              hostInfo.setPort(9870);
            if (hostInfo.getPort() == null) {
                hostInfo.setPort(defaultPort);
            }

            int n = StringUtils.indexOf(hostInfo.getHost(), ":");
            if (n != -1) {
                String host = StringUtils.substring(hostInfo.getHost(), 0, n);
                hostInfo.setHost(host);
            }

            InetAddress address = InetAddress.getByName(hostInfo.getHost());
            hostInfo.setIp(address.getHostAddress());
            return hostInfo;
        } catch (MalformedURLException e) {
            log.info("解析url异常:{}", urlString, e);
        } catch (UnknownHostException e) {
            log.error("解析host异常:{}",urlString,e);
        }
        return null;
    }


}
