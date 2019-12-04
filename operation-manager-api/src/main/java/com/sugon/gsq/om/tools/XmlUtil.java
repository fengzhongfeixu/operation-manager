package com.sugon.gsq.om.tools;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * ClassName: XmlUtil
 * Author: Administrator
 * Date: 2019/9/10 13:46
 */
public class XmlUtil {

    private XmlUtil(){}

    public static void updateXml(Map<String,String> config, String filePath) throws IOException {
        //解析map成为xml文件
        Document document = DocumentHelper.createDocument();
        //添加与Hadoop的安装包配置文件一样的头信息
        Map<String, String> inMap = new HashMap();
        inMap.put("type", "text/xsl");
        inMap.put("href", "configuration.xsl");
        document.addProcessingInstruction("xml-stylesheet", inMap);
        //创建xml文件内容
        Element root = document.addElement("configuration");
        for(String key : config.keySet()){
            Element property = root.addElement("property");
            property.addElement("name").addText(key);
            property.addElement("value").addText(config.get(key));
        }
        //创建xml文件输出格式
        OutputFormat format = OutputFormat.createPrettyPrint();  //转换成字符串
        format.setIndent("  ");
        format.setEncoding("UTF-8");
        //输出xml文件到指定路径
        FileWriter out = new FileWriter(filePath);
        XMLWriter writer = new XMLWriter(out, format);
        writer.write(document);
        writer.close();
        out.close();
    }

}
