package com.sugon.gsq.om.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "om_node_message")
public class OmNodeMessage {
    /**
     * 节点静态域名
     */
    private String hostname;

    /**
     * 节点ip
     */
    private String ip;

    /**
     * 节点服务端口
     */
    private String port;

    /**
     * 节点角色
     */
    private String role;

    /**
     * 进程号
     */
    private String pid;

    /**
     * 启动进程的用户
     */
    private String username;

    /**
     * 节点主机名
     */
    private String name;

    /**
     * 节点操作系统
     */
    private String system;

    /**
     * 节点操作系统版本
     */
    private String version;

    /**
     * 网络读取速率
     */
    private String netread;

    /**
     * 网络写入速率
     */
    private String netwrite;

    /**
     * 磁盘使用率
     */
    private String diskusage;

    /**
     * 磁盘使用量
     */
    private String diskused;

    /**
     * 内存使用率
     */
    private String memoryusage;

    /**
     * 内存使用量
     */
    private String memoryused;

    /**
     * cpu使用率
     */
    private String cpuusage;

    /**
     * 健康状态
     */
    private String status;

}