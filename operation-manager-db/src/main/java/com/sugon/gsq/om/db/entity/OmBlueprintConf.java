package com.sugon.gsq.om.db.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "om_blueprint_conf")
public class OmBlueprintConf {
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
     * 所属蓝图的版本号
     */
    private String version;

    /**
     * 配置项值
     */
    private String v;

}