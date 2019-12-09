package com.sugon.gsq.om.agent.controller;

import com.sugon.gsq.om.agent.service.AgentBlueprintService;
import com.sugon.gsq.om.agent.service.AgentEventService;
import com.sugon.gsq.om.agent.service.AgentProcessService;
import com.sugon.gsq.om.agent.service.AgentService;
import com.sugon.gsq.om.common.constant.Orders;
import com.sugon.gsq.om.common.utils.CommonUtil;
import com.sugon.gsq.om.db.entity.OmProcessInfo;
import com.sugon.gsq.om.model.CommandModel;
import com.sugon.gsq.om.model.NoticeModel;
import com.sugon.gsq.om.db.entity.OmBlueprintConf;
import com.sugon.gsq.om.common.constant.Constant;
import com.sugon.gsq.om.model.PairModel;
import com.sugon.gsq.om.tools.ApiUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * ClassName: AgentController
 * Author: Administrator
 * Date: 2019/8/28 19:52
 */
@Slf4j
@RestController
@RequestMapping("/agent")
@Api(tags = "RELEASE-2.1.0", description = "代理应用接口", value = "服务器代理")
public class AgentController {

    @Autowired
    AgentBlueprintService blueprintService;

    @Autowired
    AgentService agentService;

    @Autowired
    AgentProcessService processService;

    @Autowired
    AgentEventService eventService;

    @PostMapping("/notice.do")
    public PairModel notice(@RequestBody NoticeModel notice) throws IOException, InterruptedException {
        String title = notice.getTitle();
        PairModel result = new PairModel();
        if(title.equals(Orders.GET_CONFIGS)){
            //获取当前节点需要的蓝图配置
            List<OmBlueprintConf> configs = blueprintService.getConfigs();
            //输出配置文件
            agentService.updateXmlByConfigs(configs);
            //生成Hadoop系统需要的workers文件
            if(isOrNot("HDFS")){
                List<String> datanodes = blueprintService.getNodesByProcessName(Constant.HDFS_DATANODE_PROCESS);
                agentService.initHDFS(datanodes);
            }
            //生成Hbase组件需要的regionservers配置文件
            if(isOrNot("HBASE")){
                //生成从节点列表文件和备用主列表文件
                List<String> regionserverNodes = blueprintService.getNodesByProcessName(Constant.HBASE_REGIONSERVER_PROCESS);
                List<String> backupMasterNodes = blueprintService.getNodesByProcessName(Constant.HBASE_MASTER_PROCESS);
                agentService.initHbase(regionserverNodes, backupMasterNodes);
            }
            //生成zookeeper需要的myid文件
            List<String> znodes = blueprintService.getNodesByProcessName(Constant.ZOOKEEPER_SERVER_PROCESS);
            if(znodes.contains(Constant.LOCALHOST)){
                agentService.initZookeeper();
            }
            result.setKey("完成了Hadoop生态环境的配置")
                    .setValue("Hadoop init success !");
        } else if (title.equals(Orders.START_ZOOKEEPER_SERVER)){
            String message = agentService.startZookeeper();
            result.setKey("启动了zookeeper进程")
                    .setValue(message);
            String pid = ApiUtil.getProcessInfoByName("QuorumPeerMain");
            int flag = pid != null ?
                    saveProcess("zookeeper","ZKserver",pid,"QuorumPeerMain","running")
                    :saveProcess("zookeeper","ZKserver",pid,"QuorumPeerMain","stop");
        } else if(title.equals(Orders.START_HDFS_JOURNALNODE)){
            String message = agentService.startJournalnode();
            result.setKey("启动了journalnode进程")
                    .setValue(message);
            String pid = ApiUtil.getProcessInfoByName("JournalNode");
            int flag = pid != null ?
                    saveProcess("HDFS","journalnode",pid,"JournalNode","running")
                    :saveProcess("HDFS","journalnode",pid,"JournalNode","stop");
        } else if (title.equals(Orders.FORMAT_HDFS_NAMENODE)){
            String message = agentService.formatNamenode();
            result.setKey("完成namenode格式化并启动")
                    .setValue(message);
            String pid = ApiUtil.getProcessInfoByName("NameNode");
            int flag = pid != null ?
                    saveProcess("HDFS","namenode",pid,"NameNode","running")
                    :saveProcess("HDFS","namenode",pid,"NameNode","stop");
        } else if (title.equals(Orders.FORMAT_HDFS_NAMENODE_ACTIVE)){
            String message = agentService.formatActiveNamenode();
            result.setKey("被选举为activeNamenode,且完成格式化并启动")
                    .setValue(message);
            //zkfc记录
            String zkfcPid = ApiUtil.getProcessInfoByName("DFSZKFailoverController");
            int flag = zkfcPid != null ?
                    saveProcess("HDFS","zkfc",zkfcPid,"DFSZKFailoverController","running")
                    :saveProcess("HDFS","zkfc",zkfcPid,"DFSZKFailoverController","stop");
            //namenode记录
            String pid = ApiUtil.getProcessInfoByName("NameNode");
            int flag_ = pid != null ?
                    saveProcess("HDFS","namenode",pid,"NameNode","running")
                    :saveProcess("HDFS","namenode",pid,"NameNode","stop");
        } else if (title.equals(Orders.FORMAT_HDFS_NAMENODE_STANDBY)){
            String message = agentService.formatStandbyNamenode();
            result.setKey("被储备为standbyNamenode,且完成格式化并启动")
                    .setValue(message);
            //zkfc记录
            String zkfcPid = ApiUtil.getProcessInfoByName("DFSZKFailoverController");
            int flag = zkfcPid != null ?
                    saveProcess("HDFS","zkfc",zkfcPid,"DFSZKFailoverController","running")
                    :saveProcess("HDFS","zkfc",zkfcPid,"DFSZKFailoverController","stop");
            //namenode记录
            String pid = ApiUtil.getProcessInfoByName("NameNode");
            int flag_ = pid != null ?
                    saveProcess("HDFS","namenode",pid,"NameNode","running")
                    :saveProcess("HDFS","namenode",pid,"NameNode","stop");
        } else if (title.equals(Orders.START_HDFS_SECONDARYNAMENODE)){
            String message = agentService.startSecondarynamenode();
            result.setKey("完成Secondarynamenode格式化并启动")
                    .setValue(message);
            String pid = ApiUtil.getProcessInfoByName("SecondaryNamenode");
            int flag = pid != null ?
                    saveProcess("HDFS","secondaryNamenode",pid,"SecondaryNamenode","running")
                    :saveProcess("HDFS","secondaryNamenode",pid,"SecondaryNamenode","stop");
        } else if (title.equals(Orders.START_HDFS_DATANODE)){
            String message = agentService.startDatanodes();
            result.setKey("完成datanode格式化并启动")
                    .setValue(message);
            String pid = ApiUtil.getProcessInfoByName("DataNode");
            int flag = pid != null ?
                    saveProcess("HDFS","datanode",pid,"DataNode","running")
                    :saveProcess("HDFS","datanode",pid,"DataNode","stop");
        } else if (title.equals(Orders.START_MAPRED_HISTORY)){
            String message = agentService.startHistoryserver();
            result.setKey("启动了historyserver")
                    .setValue(message);
            String pid = ApiUtil.getProcessInfoByName("JobHistoryServer");
            int flag = pid != null ?
                    saveProcess("MapReduce","jobHistoryServer",pid,"JobHistoryServer","running")
                    :saveProcess("MapReduce","jobHistoryServer",pid,"JobHistoryServer","stop");
        } else if (title.equals(Orders.START_YARN_RESOURCEMANAGER)){
            String message = agentService.startResourcemanager();
            result.setKey("启动了resourcemanager")
                    .setValue(message);
            String pid = ApiUtil.getProcessInfoByName("ResourceManager");
            int flag = pid != null ?
                    saveProcess("Yarn","resourceManager",pid,"ResourceManager","running")
                    :saveProcess("Yarn","resourceManager",pid,"ResourceManager","stop");
        } else if (title.equals(Orders.START_YARN_NODEMANAGER)){
            String message = agentService.startNodemanager();
            result.setKey("启动了nodemanager")
                    .setValue(message);
            String pid = ApiUtil.getProcessInfoByName("NodeManager");
            int flag = pid != null ?
                    saveProcess("Yarn","nodeManager",pid,"NodeManager","running")
                    :saveProcess("Yarn","nodeManager",pid,"NodeManager","stop");
        } else if (title.equals(Orders.START_YARN_TIMELINESERVER)){
            String message = agentService.startTimelineserver();
            result.setKey("启动了timelineserver")
                    .setValue(message);
            String pid = ApiUtil.getProcessInfoByName("ApplicationHistoryServer");
            int flag = pid != null ?
                    saveProcess("Yarn","timelineserver",pid,"ApplicationHistoryServer","running")
                    :saveProcess("Yarn","timelineserver",pid,"ApplicationHistoryServer","stop");
        } else if (title.equals(Orders.INIT_HIVE_METASTORE)){
            String message = agentService.initHiveMetastore();
            result.setKey("初始化了hive的元数据服务")
                    .setValue(message);
        } else if (title.equals(Orders.START_HIVE_METASTORE)){
            String message = agentService.startHiveMetastore();
            result.setKey("启动了hive的元数据服务")
                    .setValue(message);
            //hive服务有延迟
            Thread.sleep(2000);
            String pid = ApiUtil.getProcessInfoByName("HiveMetaStore");
            int flag = pid != null ?
                    saveProcess("hive","metastore",pid,"HiveMetaStore","running")
                    :saveProcess("hive","metastore",pid,"HiveMetaStore","stop");
            //没想到什么好的方法
            Thread.sleep(7000);
        } else if (title.equals(Orders.START_HIVE_HIVESERVER2)){
            String message = agentService.startHiveServer2();
            result.setKey("启动了hive的jdbc服务")
                    .setValue(message);
            //hive服务有延迟
            Thread.sleep(2000);
            String pid = ApiUtil.getProcessInfoByName("HiveServer2");
            int flag = pid != null ?
                    saveProcess("hive","hiveserver2",pid,"HiveServer2","running")
                    :saveProcess("hive","hiveserver2",pid,"HiveServer2","stop");
        } else if (title.equals(Orders.START_HBASE_MASTER)){
            String message = agentService.startHbaseMaster();
            result.setKey("启动了Hbase的master服务")
                    .setValue(message);
            String pid = ApiUtil.getProcessInfoByName("HMaster");
            int flag = pid != null ?
                    saveProcess("Hbase","HMaster",pid,"HMaster","running")
                    :saveProcess("Hbase","HMaster",pid,"HMaster","stop");
        } else if (title.equals(Orders.START_HBASE_REGIONSERVER)){
            String message = agentService.startRegionserver();
            result.setKey("启动了Hbase的regionserver服务")
                    .setValue(message);
            String pid = ApiUtil.getProcessInfoByName("HRegionServer");
            int flag = pid != null ?
                    saveProcess("Hbase","HRegionServer",pid,"HRegionServer","running")
                    :saveProcess("Hbase","HRegionServer",pid,"HRegionServer","stop");
        } else if (title.equals(Orders.START_KAFKA_BROKER)){
            String message = agentService.startBroker();
            result.setKey("启动了kakfa的broker服务")
                    .setValue(message);
            String pid = ApiUtil.getProcessInfoByName("Kafka");
            int flag = pid != null ?
                    saveProcess("Kafka","broker",pid,"Kafka","running")
                    :saveProcess("Kafka","broker",pid,"Kafka","stop");
        }
        return result;
    }

    @PostMapping("/command.do")
    public PairModel command(@RequestBody CommandModel commandModel) throws IOException, InterruptedException {
        String command = commandModel.getCommand();
        String process = commandModel.getProcess();

        PairModel result = new PairModel();
        String pid = null;
        if(process.equals("metastore") || process.equals("hiveserver2")){
            pid = processService.getPidByProcessName(process);
        }
        ApiUtil.processCommand(command,process,pid);
        return result
                .setKey("完成了" + process + "进程的" + command + "操作")
                .setValue("The process does not have specific operational information output !");
    }

    @PostMapping("/hardwareInfo.do")
    public Map<String, String> hardwareInfo(){
        Map<String, String> result = new HashMap<>();
        String networkRead = ApiUtil.networkRead();
        String networkWrite = ApiUtil.networkWrite();
        String diskUsage = ApiUtil.diskUsage();
        String diskUsed = ApiUtil.diskUsed();
        String memoryUsage = ApiUtil.memoryUsage();
        String memoryUsed = ApiUtil.memoryUsed();
        String cpuUsage = ApiUtil.cpuUsage();
        result.put("monitor_network_read",networkRead);
        result.put("monitor_network_write",networkWrite);
        result.put("monitor_disk_usage",diskUsage);
        result.put("monitor_disk_used",diskUsed);
        result.put("monitor_memory_usage",memoryUsage);
        result.put("monitor_memory_used",memoryUsed);
        result.put("monitor_cpu_usage",cpuUsage);
        return result;
    }

    private boolean isOrNot(String name){
        AtomicBoolean flag = new AtomicBoolean(false);
        blueprintService.getProcessByHostname().forEach(x -> {
            if(x.split("_")[0].equals(name)){
                flag.set(true);
            }
        });
        return flag.get();
    }

    private int saveProcess(String service, String process, String pid, String pname, String status){
        return processService.saveProcess(
                new OmProcessInfo()
                        .setId(CommonUtil.getUUID())
                        .setHostname(Constant.LOCALHOST)
                        .setService(service)
                        .setProcess(process)
                        .setPid(pid)
                        .setPname(pname)
                        .setStatus(status)
                        .setSame("yes")
        );
    }

}
