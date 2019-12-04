package com.sugon.gsq.om.config;

import com.sugon.gsq.om.constant.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

/*
 * ClassName: TomcatConfig
 * Author: Administrator
 * Date: 2019/9/17 18:13
 */
@Configuration
public class TomcatConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Value("${server.agent.port}")
    private Integer agent_port;

    @Value("${server.master.port}")
    private Integer master_port;

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        if(Constant.ROLE.equals(Constant.MASTER)){
            factory.setPort(master_port);
        } else if (Constant.ROLE.equals(Constant.AGENT)){
            factory.setPort(agent_port);
        }
    }
}
