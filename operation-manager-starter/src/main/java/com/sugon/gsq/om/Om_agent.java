package com.sugon.gsq.om;

import com.sugon.gsq.om.master.service.MasterNodeService;
import com.sugon.gsq.om.constant.Constant;
import com.sugon.gsq.om.db.entity.OmNodeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.spring.annotation.MapperScan;

import java.net.InetAddress;
import java.util.Properties;

@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "com.sugon.gsq.om.db.mapper")
public class Om_agent implements CommandLineRunner {

    public static ConfigurableApplicationContext context;

    public static Environment environment;

    @Autowired
    MasterNodeService nodeService;

    @Autowired
    public void setEnvironment(Environment environment) {
        Om_agent.environment = environment;
    }

    public static void main(String[] args) {
        try {
            if(args[0].equals(Constant.AGENT)){
                //获取当前进程角色
                Constant.ROLE = Constant.AGENT;
                context = SpringApplication.run(Om_agent.class, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.close();
        }
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if(args[0].equals(Constant.MASTER)) return;
        //赋予hostname全局变量
        Constant.LOCALHOST = InetAddress.getLocalHost().getHostName();
        //注册本机到元数据库,证明该机器是集群中的一个节点
        String port = environment.getProperty("server.agent.port");
        String ip = InetAddress.getLocalHost().getHostAddress();
        Properties props = System.getProperties();
        //覆盖重复的进程
        nodeService.delete(Constant.LOCALHOST,Constant.AGENT);
        //开始注册
        nodeService.register(new OmNodeMessage()
                .setHostname(Constant.LOCALHOST)
                .setIp(ip)
                .setPort(port)
                .setRole(Constant.ROLE)
                .setPid(props.getProperty("PID"))
                .setUsername(props.getProperty("user.name"))
                .setName(props.getProperty("java.rmi.server.hostname"))
                .setSystem(props.getProperty("os.name"))
                .setVersion(props.getProperty("os.version"))
        );
    }

}
