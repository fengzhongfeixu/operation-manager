package com.sugon.gsq.om.entity.server;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/*
 * ClassName: ServerEntity
 * Author: Administrator
 * Date: 2019/9/26 10:21
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "配置文件展示实体类", description = "服务管理-配置文件基本信息")
public class ConfigEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据库中的标识", required = true)
    private String flag;

    @ApiModelProperty(value = "文件名称", required = true)
    private String filename;

    @ApiModelProperty(value = "配置文件描述信息", required = true)
    private String describe;

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

}