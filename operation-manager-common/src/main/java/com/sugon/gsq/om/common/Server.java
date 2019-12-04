package com.sugon.gsq.om.common;

import com.sugon.gsq.om.db.entity.OmOperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/*
 * ClassName: Server
 * Author: Administrator
 * Date: 2019/9/23 13:33
 */
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Object omOperationLog = Class.forName("com.sugon.gsq.om.db.entity.OmOperationLog").newInstance();
        System.out.println(((OmOperationLog)omOperationLog).getStatus());
        ClassLoader.getSystemClassLoader().loadClass("dfdf");
    }
}
