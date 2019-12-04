package com.sugon.gsq.om.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "om_blueprint_process")
public class OmBlueprintProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 主机名
     */
    private String hostname;

    /**
     * 进程名
     */
    private String processname;

    /**
     * 所属蓝图的版本
     */
    private String version;

}