package com.sugon.gsq.om.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "om_sys_user")
public class OmSysUser {
    /**
     * 用户名
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 角色名
     */
    private String role;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 更新时间
     */
    private Date updatetime;

}