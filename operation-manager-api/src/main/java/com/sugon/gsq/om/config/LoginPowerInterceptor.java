package com.sugon.gsq.om.config;

import com.sugon.gsq.om.constant.Constant;
import com.sugon.gsq.om.db.entity.OmSysUser;
import com.sugon.gsq.om.db.mapper.OmSysUserMapper;
import com.sugon.gsq.om.model.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;

/*
 * ClassName: MasterPowerInterceptor
 * Author: Administrator
 * Date: 2019/8/28 20:02
 */
public class LoginPowerInterceptor implements HandlerInterceptor {

    @Autowired
    OmSysUserMapper sysUserMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        if (o instanceof HandlerMethod){
            OmSysUser user = (OmSysUser) request.getSession().getAttribute("user");
            if(user == null){
                PrintWriter printWriter = response.getWriter();
                ResponseModel result = new ResponseModel();
                result.setCode(Constant.HTTP_STATUS_NOTLOGIN)
                        .setMessage("You haven't signed in yet");
                printWriter.write(result.toString());
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {

    }

}
