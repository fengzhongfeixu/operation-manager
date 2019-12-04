package com.sugon.gsq.om.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "om_operation_event")
public class OmOperationEvent {
    /**
     * 事件id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 告警级别
     */
    private String level;

    /**
     * 所属服务
     */
    private String service;

    /**
     * 告警日期
     */
    private Date createtime;

    /**
     * 是否过期
     */
    private Boolean outdate;

    /**
     * 事件描述
     */
    private String description;

}