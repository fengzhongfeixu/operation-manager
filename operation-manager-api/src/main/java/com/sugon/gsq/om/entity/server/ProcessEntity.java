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
@ApiModel(value = "服务下子进程列表实体", description = "服务管理-进程实体")
public class ProcessEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "进程名称", required = true)
    private String name;

    @ApiModelProperty(value = "是否同步配置文件", required = true)
    private boolean status;

    @ApiModelProperty(value = "存活数量", required = true)
    private Integer normal;

    @ApiModelProperty(value = "总数量", required = true)
    private Integer total;

    @ApiModelProperty(value = "超链接", required = true)
    private String url;

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

}