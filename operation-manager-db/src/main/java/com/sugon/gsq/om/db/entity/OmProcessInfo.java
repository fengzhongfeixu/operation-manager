package com.sugon.gsq.om.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "om_process_info")
public class OmProcessInfo {
    /**
     * uuid
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 进程名称
     */
    private String process;

    /**
     * 所属服务名称
     */
    private String service;

    /**
     * 所属节点名称
     */
    private String hostname;

    /**
     * 进程id
     */
    private String pid;

    /**
     * 进程在jps中的名字
     */
    private String pname;

    /**
     * 进程是否运行
     */
    private String status;

    /**
     * 进程是否与配置文件同步
     */
    private String same;

}