package com.sugon.gsq.om.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "om_config_info")
public class OmConfigInfo {
    /**
     * uuid
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 配置项键
     */
    private String k;

    /**
     * 所属配置文件名称
     */
    private String belong;

    /**
     * 配置项作用域(public全局；hostname私有)
     */
    private String scope;

    /**
     * 配置项修改的次数
     */
    private Integer version;

    /**
     * 配置项值
     */
    private String v;

}