package com.sugon.gsq.om.task;

import com.sugon.gsq.om.common.constant.Constant;
import com.sugon.gsq.om.common.utils.CfgUtil;
import com.sugon.gsq.om.common.utils.CommonUtil;
import com.sugon.gsq.om.common.utils.XmlUtil;
import com.sugon.gsq.om.db.entity.OmConfigInfo;
import com.sugon.gsq.om.db.entity.OmMonitorMessage;
import com.sugon.gsq.om.db.entity.OmOperationEvent;
import com.sugon.gsq.om.db.entity.OmProcessInfo;
import com.sugon.gsq.om.db.mapper.OmConfigInfoMapper;
import com.sugon.gsq.om.db.mapper.OmMonitorMessageMapper;
import com.sugon.gsq.om.db.mapper.OmOperationEventMapper;
import com.sugon.gsq.om.db.mapper.OmProcessInfoMapper;
import com.sugon.gsq.om.tools.ApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * ClassName: Report
 * Author: Administrator
 * Date: 2019/11/12 11:47
 */
@Component
public class Report {

    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Autowired
    private OmMonitorMessageMapper monitorMessageMapper;

    @Autowired
    private OmProcessInfoMapper processInfoMapper;

    @Autowired
    private OmOperationEventMapper operationEventMapper;

    @Autowired
    private OmConfigInfoMapper configInfoMapper;

    //上报监控信息
    @Scheduled(cron = "0 */1 * * * ?")
    public void reportSystemMessage() throws ParseException {
        //master节点不参与数据上报
        if(Constant.ROLE.equals(Constant.MASTER)){
            return;
        }
        String now = format.format(new Date());
        List<OmMonitorMessage> omHealthStatuses = new LinkedList<>();
        OmMonitorMessage networkRead = new OmMonitorMessage()
                .setHostname(Constant.LOCALHOST)
                .setProject(Constant.MONITOR_NETWORK_READ)
                .setTime(format.parse(now))
                .setNumber(Double.parseDouble(ApiUtil.networkRead().split(" ")[0]))
                .setUnit(ApiUtil.networkRead().split(" ")[1]);
        omHealthStatuses.add(networkRead);
        OmMonitorMessage networkWrite = new OmMonitorMessage()
                .setHostname(Constant.LOCALHOST)
                .setProject(Constant.MONITOR_NETWORK_WRITE)
                .setTime(format.parse(now))
                .setNumber(Double.parseDouble(ApiUtil.networkWrite().split(" ")[0]))
                .setUnit(ApiUtil.networkWrite().split(" ")[1]);
        omHealthStatuses.add(networkWrite);
        OmMonitorMessage diskUsage = new OmMonitorMessage()
                .setHostname(Constant.LOCALHOST)
                .setProject(Constant.MONITOR_DISK_USAGE)
                .setTime(format.parse(now))
                .setNumber(Double.parseDouble(ApiUtil.diskUsage().split(" ")[0]))
                .setUnit(ApiUtil.diskUsage().split(" ")[1]);
        omHealthStatuses.add(diskUsage);
        OmMonitorMessage diskUsed = new OmMonitorMessage()
                .setHostname(Constant.LOCALHOST)
                .setProject(Constant.MONITOR_DISK_USED)
                .setTime(format.parse(now))
                .setNumber(Double.parseDouble(ApiUtil.diskUsed().split(" ")[0]))
                .setUnit(ApiUtil.diskUsed().split(" ")[1]);
        omHealthStatuses.add(diskUsed);
        OmMonitorMessage memoryUsage = new OmMonitorMessage()
                .setHostname(Constant.LOCALHOST)
                .setProject(Constant.MONITOR_MEMORY_USAGE)
                .setTime(format.parse(now))
                .setNumber(Double.parseDouble(ApiUtil.memoryUsage().split(" ")[0]))
                .setUnit(ApiUtil.memoryUsage().split(" ")[1]);
        omHealthStatuses.add(memoryUsage);
        OmMonitorMessage memoryUsed = new OmMonitorMessage()
                .setHostname(Constant.LOCALHOST)
                .setProject(Constant.MONITOR_MEMORY_USED)
                .setTime(format.parse(now))
                .setNumber(Double.parseDouble(ApiUtil.memoryUsed().split(" ")[0]))
                .setUnit(ApiUtil.memoryUsed().split(" ")[1]);
        omHealthStatuses.add(memoryUsed);
        OmMonitorMessage cpuUsage = new OmMonitorMessage()
                .setHostname(Constant.LOCALHOST)
                .setProject(Constant.MONITOR_CPU_USAGE)
                .setTime(format.parse(now))
                .setNumber(Double.parseDouble(ApiUtil.cpuUsage().split(" ")[0]))
                .setUnit(ApiUtil.cpuUsage().split(" ")[1]);
        omHealthStatuses.add(cpuUsage);
        for(OmMonitorMessage omHealthStatus : omHealthStatuses){
            monitorMessageMapper.insert(omHealthStatus);
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void deleteMessage(){
        if(Constant.ROLE.equals(Constant.AGENT)){
            return;
        }
        //计算前一个月
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        String mon = format.format(m);
        //删除前一个月数据
        Weekend<OmMonitorMessage> weekend = Weekend.of(OmMonitorMessage.class);
        WeekendCriteria<OmMonitorMessage, Object> criteria = weekend.weekendCriteria();
        criteria.andGreaterThan("time", mon);
        Integer num = monitorMessageMapper.deleteByExample(weekend);
    }

    //检查hadoop进程
    @Scheduled(cron = "0 */1 * * * ?")
    public void processHealthCheck() throws IOException, InterruptedException {
        //master节点不需要监听进程
        if(Constant.ROLE.equals(Constant.MASTER)){
            return;
        }
        Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
        WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("hostname", Constant.LOCALHOST);
        List<OmProcessInfo> processes = processInfoMapper.selectByExample(weekend);
        for(OmProcessInfo process : processes){
            //判断进程是否死亡
            if(process.getStatus().equals("running")){
                //查看pid存不存在
                String pid = ApiUtil.getProcessInfoByName(process.getPname());
                if(pid == null){
                    processInfoMapper.updateByPrimaryKey(process.setStatus("stop"));
                    // 记录事件
                    if(process.getPname().equals("namenode") || process.getPname().equals("resourceManager") ||
                            process.getPname().equals("metastore") || process.getPname().equals("hiveserver2") ||
                            process.getPname().equals("HMaster")){
                        operationEventMapper.insert(new OmOperationEvent()
                                .setCreatetime(new Date())
                                .setDescription(process.getService() + "服务不可用!")
                                .setLevel("4")
                                .setOutdate(false)
                                .setService(process.getService()));
                    } else {
                        operationEventMapper.insert(new OmOperationEvent()
                                .setCreatetime(new Date())
                                .setDescription(process.getProcess() + "进程已停止!")
                                .setLevel("3")
                                .setOutdate(false)
                                .setService(process.getService()));
                    }
                } else {
                    processInfoMapper.updateByPrimaryKey(process.setPid(pid));
                }
            } else {
                //死亡状态下的进程是否已经开启
                String pid = ApiUtil.getProcessInfoByName(process.getPname());
                if(pid != null){
                    processInfoMapper.updateByPrimaryKey(process.setPid(pid).setStatus("running"));
                    // 记录事件
                    operationEventMapper.insert(new OmOperationEvent()
                            .setCreatetime(new Date())
                            .setDescription(process.getProcess() + "进程重新启动")
                            .setLevel("1")
                            .setOutdate(false)
                            .setService(process.getService()));
                }
            }
        }
    }

    //同步配置文件
    @Scheduled(cron = "0 */1 * * * ?")
    public void SynchronizeConfig() throws IOException {
        //master节点不进城配置文件同步
        if(Constant.ROLE.equals(Constant.MASTER)){
            return;
        }
        Weekend<OmConfigInfo> weekend = Weekend.of(OmConfigInfo.class);
        WeekendCriteria<OmConfigInfo, Object> criteria = weekend.weekendCriteria();
        criteria.orEqualTo("scope",Constant.LOCALHOST);
        criteria.orEqualTo("scope","public");
        List<OmConfigInfo> configs = configInfoMapper.selectByExample(weekend);
        //开始同步配置文件
        Map<String,String> zoo = new HashMap<>();
        Map<String,String> core = new HashMap<>();
        Map<String,String> hdfs = new HashMap<>();
        Map<String,String> yarn = new HashMap<>();
        Map<String,String> mapred = new HashMap<>();
        Map<String,String> hive = new HashMap<>();
        Map<String,String> spark_hive = new HashMap<>();
        Map<String,String> hbase = new HashMap<>();
        Map<String,String> kafka = new HashMap<>();
        for(OmConfigInfo config : configs){
            if(config.getBelong().equals("zoo")){
                zoo.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("core-site")){
                core.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("hdfs-site")){
                hdfs.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("yarn-site")){
                yarn.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("mapred-site")){
                mapred.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("hive-site")){
                hive.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("spark-hive-site")){
                spark_hive.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("hbase-site")){
                hbase.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("kafka-server")){
                kafka.put(config.getK(),config.getV());
            }
        }
        CfgUtil.updateCfg(zoo,
                Constant.ZOOKEEPER_HOME + File.separator + "conf" + File.separator + "zoo.cfg");
        XmlUtil.updateXml(core,
                Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "core-site.xml");
        XmlUtil.updateXml(hdfs,
                Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "hdfs-site.xml");
        XmlUtil.updateXml(yarn,
                Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "yarn-site.xml");
        XmlUtil.updateXml(mapred,
                Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "mapred-site.xml");
        XmlUtil.updateXml(hive,
                Constant.HIVE_HOME + File.separator + "conf" + File.separator + "hive-site.xml");
        XmlUtil.updateXml(spark_hive,
                Constant.SPARK_HOME+ File.separator + "conf" + File.separator + "hive-site.xml");
        //复制spark所需的hadoop配置文件
        CommonUtil.copyFile(Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "core-site.xml"
                ,Constant.SPARK_HOME + File.separator + "conf" + File.separator + "core-site.xml");
        CommonUtil.copyFile(Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "hdfs-site.xml"
                ,Constant.SPARK_HOME + File.separator + "conf" + File.separator + "hdfs-site.xml");
        XmlUtil.updateXml(hbase,
                Constant.HBASE_HOME + File.separator + "conf" + File.separator + "hbase-site.xml");
        //复制hbase所需的hadoop配置文件
        CommonUtil.copyFile(Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "core-site.xml"
                ,Constant.HBASE_HOME + File.separator + "conf" + File.separator + "core-site.xml");
        CommonUtil.copyFile(Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "hdfs-site.xml"
                ,Constant.HBASE_HOME + File.separator + "conf" + File.separator + "hdfs-site.xml");
        //配置kafka
        CfgUtil.updateCfg(kafka,
                Constant.KAFKA_HOME + File.separator + "config" + File.separator + "server.properties");
    }

}
