package com.sugon.gsq.om.controller;

import com.battcn.boot.swagger.model.DataType;
import com.battcn.boot.swagger.model.ParamType;
import com.sugon.gsq.om.constant.Constant;
import com.sugon.gsq.om.db.entity.OmSysUser;
import com.sugon.gsq.om.db.mapper.OmSysUserMapper;
import com.sugon.gsq.om.model.ResponseModel;
import com.sugon.gsq.om.tools.MD5Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * ClassName: LoginController
 * Author: Administrator
 * Date: 2019/8/28 19:52
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "RELEASE-4.0.5", description = "用户管理", value = "用户管理")
public class LoginController {

    @Autowired
    OmSysUserMapper sysUserMapper;

    @PostMapping(path = "/login")
    @ApiOperation(value = "登录接口", notes = "登录入口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = DataType.STRING, paramType = ParamType.QUERY, defaultValue = "admin"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = DataType.STRING, paramType = ParamType.QUERY, defaultValue = "admin1234@sugon")
    })
    public ResponseModel login(String username,String password, HttpServletRequest request) {
        OmSysUser dbUser = sysUserMapper.selectByPrimaryKey(username);
        if(dbUser == null){
            return new ResponseModel<String>().setCode(Constant.HTTP_STATUS_NOUSER)
                    .setMessage(Constant.HTTP_MESSAGE_ERROR);
        } else {
            if(MD5Util.getSaltverifyMD5(password,dbUser.getPassword())) {
                request.getSession().setAttribute("user", dbUser);
                return new ResponseModel<String>().setCode(Constant.HTTP_STATUS_SUCCESS)
                        .setMessage(Constant.HTTP_MESSAGE_SUCCESS);
            }else {
                return new ResponseModel<String>().setCode(Constant.HTTP_STATUS_NOUSER)
                        .setMessage(Constant.HTTP_MESSAGE_ERROR);
            }
        }
    }

    @ResponseBody
    @ApiOperation(value = "获取当前用户接口", notes = "获取当前登录的用户信息")
    @RequestMapping(path = "/current-user", method = RequestMethod.GET)
    public ResponseModel currentser(HttpServletRequest request){
        return  new ResponseModel<OmSysUser>().setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData((OmSysUser) request.getSession().getAttribute("user"));
    }

    @ResponseBody
    @ApiOperation(value = "注销接口", notes = "注销当前登录的用户")
    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public ResponseModel logout(HttpServletRequest request){
        request.getSession().invalidate();
        return new ResponseModel<OmSysUser>().setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_CANCELLATIONSUCCESS);
    }

}
