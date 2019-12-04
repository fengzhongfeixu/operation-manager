package com.sugon.gsq.om.constant;

/*
 * ClassName: UrlMapping
 * Author: Administrator
 * Date: 2019/8/29 11:00
 */
public class UrlMapping {

    //master与agent
    public static final String REQUEST_NOTICE = "http://%s:%s/agent/notice.do";

    public static final String REQUEST_STATUS = "http://%s:%s/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus";

    public static final String REQUEST_COMMAND = "http://%s:%s/agent/command.do";

    public static final String REQUEST_MESSAGE = "http://%s:%s/agent/hardwareInfo.do";

}
