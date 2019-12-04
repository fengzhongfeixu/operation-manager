package com.sugon.gsq.om.entity.node;

import com.alibaba.fastjson.JSONObject;
import com.sugon.gsq.om.db.entity.OmNodeMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.text.NumberFormat;

/*
 * ClassName: SoServerInforEntity
 * Author: Administrator
 * Date: 2019/9/26 10:21
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "节点列表展示实体", description = "主机管理-列表")
public class NodeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 域名
     */
    private String hostname;

    /**
     * IP
     */
    private String ip;

    /**
     * 网络读取速率
     */
    private String netRead;

    /**
     * 网络写入速率
     */
    private String netWrite;

    /**
     * 健康状态
     */
    private String status;

    /**
     * 磁盘使用率
     */
    private String diskUsage;

    /**
     * 磁盘使用情况
     */
    private String diskInfo;

    /**
     * 磁盘使用率
     */
    private String memoryUsage;

    /**
     * 磁盘使用情况
     */
    private String memoryInfo;

    /**
     * cpu使用率
     */
    private String cpuUsage;

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

}
