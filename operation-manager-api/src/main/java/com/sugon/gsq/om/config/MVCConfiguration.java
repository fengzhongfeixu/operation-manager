package com.sugon.gsq.om.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MVCConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截的管理器
        InterceptorRegistration login = registry.addInterceptor(new LoginPowerInterceptor());
        InterceptorRegistration master = registry.addInterceptor(new MasterPowerInterceptor());
        InterceptorRegistration agent = registry.addInterceptor(new AgentPowerInterceptor());
        login.addPathPatterns("/**");
        login.excludePathPatterns("/user/login","/user/logout","/agent/**"
                ,"/swagger-ui.html","/swagger-resources/**","/v2/**","/master/**");
        master.addPathPatterns("/master/**");
        agent.addPathPatterns("/agent/**");
    }

}
