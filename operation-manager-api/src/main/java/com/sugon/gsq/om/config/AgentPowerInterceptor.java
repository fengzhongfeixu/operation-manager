package com.sugon.gsq.om.config;

import com.sugon.gsq.om.common.constant.Constant;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/*
 * ClassName: MasterPowerInterceptor
 * Author: Administrator
 * Date: 2019/8/28 20:02
 */
public class AgentPowerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if(!Constant.ROLE.equals(Constant.AGENT)){
            PrintWriter printWriter = httpServletResponse.getWriter();
            printWriter.write("You are not an good passenger !");
            return false;
        }
        return true;
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
