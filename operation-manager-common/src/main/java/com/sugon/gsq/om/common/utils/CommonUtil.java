package com.sugon.gsq.om.common.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/*
 * ClassName: CommonUtil
 * Author: Administrator
 * Date: 2019/9/9 21:17
 */
public class CommonUtil {

    private CommonUtil(){}

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    public static void copyFile(String src, String dest) throws IOException {
        FileUtils.copyFile(new File(src),new File(dest));
    }

    public static String getLocalhost() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

}
