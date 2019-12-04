package com.sugon.gsq.om.entity.monitor;

import com.alibaba.fastjson.JSONObject;
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
@ApiModel(value = "集群主机监控状态统计实体", description = "系统概览-集群主机监控状态统计")
public class SoNodesHealthEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public SoNodesHealthEntity(double good,double fault,double unknown){
        this.good = change(good);
        this.fault = change(fault);
        this.unknown = change(unknown);
    }

    @ApiModelProperty(value = "良好", required = true)
    private String good;

    @ApiModelProperty(value = "故障", required = true)
    private String fault;

    @ApiModelProperty(value = "未知", required = true)
    private String unknown;

    private String change(double input){
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumFractionDigits(5);
        return nf.format(input);
    }

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

}
