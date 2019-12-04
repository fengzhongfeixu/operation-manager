package com.sugon.gsq.om.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

/*
 * ClassName: NoticeModel
 * Author: Administrator
 * Date: 2019/8/28 20:44
 */
@Data
@Accessors(chain = true)
public class ServerModel {

    private String hostname;

    private String port;

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }
}
