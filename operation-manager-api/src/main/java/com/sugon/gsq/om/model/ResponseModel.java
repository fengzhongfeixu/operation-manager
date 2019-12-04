package com.sugon.gsq.om.model;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

/*
 * ClassName: ResponseModel
 * Author: Administrator
 * Date: 2019/9/26 10:21
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "返回值", description = "通用API接口返回模板")
public class ResponseModel<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通用返回状态
     */
    @ApiModelProperty(value = "通用返回状态", required = true)
    private Integer code;
    /**
     * 通用返回信息
     */
    @ApiModelProperty(value = "通用返回信息", required = true)
    private String message;
    /**
     * 通用返回数据
     */
    @ApiModelProperty(value = "通用返回数据")
    private T data;

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

}
