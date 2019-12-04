package com.sugon.gsq.om.entity.monitor;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/*
 * ClassName: SoServerInforEntity
 * Author: Administrator
 * Date: 2019/9/26 10:21
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "服务进程实体", description = "系统概览-服务概览")
public class SoServerInforEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "服务名称", required = true)
    private String servername;

    @ApiModelProperty(value = "健康状态", required = true)
    private String status;

    @ApiModelProperty(value = "角色数", required = true)
    private Integer num;

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

}
