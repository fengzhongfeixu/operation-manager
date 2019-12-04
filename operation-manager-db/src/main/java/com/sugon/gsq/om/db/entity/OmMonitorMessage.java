package com.sugon.gsq.om.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "om_monitor_message")
public class OmMonitorMessage {
    /**
     * 节点名称
     */
    private String hostname;

    /**
     * 指标名称
     */
    private String project;

    /**
     * 指标数值
     */
    private Double number;

    /**
     * 指标单位
     */
    private String unit;

    /**
     * 记录时间
     */
    private Date time;

}