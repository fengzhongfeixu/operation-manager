package com.sugon.gsq.om;

import com.sugon.gsq.om.master.service.MasterNodeService;
import com.sugon.gsq.om.common.constant.Constant;
import com.sugon.gsq.om.db.entity.OmNodeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.spring.annotation.MapperScan;

import java.net.InetAddress;
import java.util.Properties;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "com.sugon.gsq.om.db.mapper")
public class Om_master implements CommandLineRunner {

    public static ConfigurableApplicationContext context;

    public static Environment environment;

    @Autowired
    MasterNodeService nodeService;

    @Autowired
    public void setEnvironment(Environment environment) {
        Om_master.environment = environment;
    }

    public static void main(String[] args) {
        try {
            if(args[0].equals(Constant.MASTER)){
                //获取当前进程角色
                Constant.ROLE = Constant.MASTER;
                context = SpringApplication.run(Om_master.class, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.close();
        }
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if(args[0].equals(Constant.AGENT)) return;
        //赋予hostname全局变量
        Constant.LOCALHOST = InetAddress.getLocalHost().getHostName();
        //注册本机到元数据库,证明该机器是集群中的一个节点
        String port = environment.getProperty("server.master.port");
        String ip = InetAddress.getLocalHost().getHostAddress();
        Properties props = System.getProperties();
        //覆盖重复的进程
        nodeService.delete(Constant.LOCALHOST,Constant.MASTER);
        //不允许存在2个以上主进程
        if(nodeService.isExistMaster())
            throw new RuntimeException("The master process already exists too many !");
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
