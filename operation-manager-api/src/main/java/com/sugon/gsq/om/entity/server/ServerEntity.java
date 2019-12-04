package com.sugon.gsq.om.entity.server;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Map;

/*
 * ClassName: ServerEntity
 * Author: Administrator
 * Date: 2019/9/26 10:21
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "大数据平台服务列表实体", description = "服务管理-列表实体")
public class ServerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "服务名称", required = true)
    private String name;

    @ApiModelProperty(value = "服务状态", required = true)
    private boolean status;

    @ApiModelProperty(value = "健康状态", required = true)
    private String health;

    @ApiModelProperty(value = "配置状态", required = true)
    private boolean config;

    @ApiModelProperty(value = "角色数", required = true)
    private Map<String,Integer> role;

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

}