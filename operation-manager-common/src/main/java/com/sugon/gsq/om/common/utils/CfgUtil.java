package com.sugon.gsq.om.common.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/*
 * ClassName: CfgUtil
 * Author: Administrator
 * Date: 2019/9/10 15:45
 */
public class CfgUtil {

    private CfgUtil(){}

    public static void updateCfg(Map<String,String> config, String filePath) throws IOException {
        CustomProperties prop = new CustomProperties();
        for(String key : config.keySet()){
            prop.setProperty(key,config.get(key));
        }
        OutputStream fos = new FileOutputStream(filePath);
        prop.store(fos,"update config");
        fos.close();
    }

}
