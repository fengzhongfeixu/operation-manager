package com.sugon.gsq.om.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "om_operation_log")
public class OmOperationLog {
    /**
     * 节点主机名
     */
    private String hostname;

    /**
     * 请求状态
     */
    private String status;

    /**
     * http请求状态码
     */
    private String code;

    /**
     * 执行的操作
     */
    private String operation;

    /**
     * 操作的返回内容
     */
    private String content;

}