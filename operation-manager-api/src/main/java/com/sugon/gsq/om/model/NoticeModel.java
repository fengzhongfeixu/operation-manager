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
public class NoticeModel {

    private String title;

    private String message;

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }
}
