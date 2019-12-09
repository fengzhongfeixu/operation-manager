package com.sugon.gsq.om.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.sugon.gsq.om.common.constant.Orders;
import com.sugon.gsq.om.common.constant.UrlMapping;
import com.sugon.gsq.om.common.utils.HttpUtil;
import com.sugon.gsq.om.db.entity.OmNodeMessage;
import com.sugon.gsq.om.db.entity.OmOperationLog;
import com.sugon.gsq.om.master.service.MasterBlueprintService;
import com.sugon.gsq.om.master.service.MasterLogService;
import com.sugon.gsq.om.master.service.MasterNodeService;
import com.sugon.gsq.om.common.constant.Constant;
import com.sugon.gsq.om.master.service.MasterWebsocketServer;
import com.sugon.gsq.om.model.*;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


/*
 * ClassName: MasterController
 * Author: Administrator
 * Date: 2019/8/28 19:52
 */
@Slf4j
@RestController
@RequestMapping("/master")
@Api(tags = "RELEASE-2.2.0", description = "管理应用接口", value = "服务器管理者")
public class MasterController {

    @Autowired
    MasterBlueprintService blueprintService;

    @Autowired
    MasterNodeService nodeService;

    @Autowired
    MasterWebsocketServer websocketServer;

    @Autowired
    MasterLogService masterLogService;

    @PostMapping("/submitBlueprint.do")
    public ResponseModel submitBlueprint(@RequestBody BlueprintModel blueprint) {
        ResponseModel<String> result = new ResponseModel();
        result.setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.BLUEPRINT_SUBMIT_SUCCESS);
        //异步处理蓝图
        new Thread(){
            @Override
            public void run() {
                try {
                    handleBlueprint(blueprint);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return result;
    }

    @GetMapping("/getOperationLogs.do")
    public List<OmOperationLog> getOperationLogs(){
        return masterLogService.getOperationLogs();
    }

    private void handleBlueprint(BlueprintModel blueprint) {
        blueprintService.saveBlueprint(blueprint);
        //发送消息让从节点获取各自配置
        for(OmNodeMessage slave : nodeService.getAllSlaves()){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,slave.getIp(),slave.getPort()),
                    slave.getHostname(),
                    Orders.GET_CONFIGS);
        }
        //启动zookeeper进程
        List<String> znodeHostNames = blueprintService.getNodesByProcessName(Constant.ZOOKEEPER_SERVER_PROCESS);
        List<OmNodeMessage> znodes = nodeService.getNodesByProcessName(znodeHostNames);
        for(OmNodeMessage znode : znodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,znode.getIp(),znode.getPort()),
                    znode.getHostname(),
                    Orders.START_ZOOKEEPER_SERVER);
        }
        //启动journalnode进程
        List<String> journalnodeHostNames = blueprintService.getNodesByProcessName(Constant.HDFS_JOURNALNODE_PROCESS);
        List<OmNodeMessage> journalnodes = nodeService.getNodesByProcessName(journalnodeHostNames);
        for(OmNodeMessage journalnode : journalnodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,journalnode.getIp(),journalnode.getPort()),
                    journalnode.getHostname(),
                    Orders.START_HDFS_JOURNALNODE);
        }
        //启动namenode进程2种情况：非HA(需要启动secondarynamenode)；HA(需要主节点格式化以及热备节点格式化)
        List<String> namenodeHostNames = blueprintService.getNodesByProcessName(Constant.HDFS_NAMENODE_PROCESS);
        List<String> secondarynamenodeHostNames = blueprintService.getNodesByProcessName(Constant.HDFS_SECONDARYNAMENODE_PROCESS);
        List<OmNodeMessage> namenodes = nodeService.getNodesByProcessName(namenodeHostNames);
        List<OmNodeMessage> secondarynamenodes = nodeService.getNodesByProcessName(secondarynamenodeHostNames);
        //判断是否是HA
        if(namenodes.size() > 1){
            //随机选取一个namenode节点格式化为active
            OmNodeMessage avticeNamenode = namenodes.get(0);
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,avticeNamenode.getIp(),avticeNamenode.getPort()),
                    avticeNamenode.getHostname(),
                    Orders.FORMAT_HDFS_NAMENODE_ACTIVE);
            //除active节点外的namenode开始格式化
            namenodes.remove(avticeNamenode);
            //格式化其它的standbynamenode
            for(OmNodeMessage standbynamenode : namenodes){
                sendOrder(String.format(UrlMapping.REQUEST_NOTICE,standbynamenode.getIp(),standbynamenode.getPort()),
                        standbynamenode.getHostname(),
                        Orders.FORMAT_HDFS_NAMENODE_STANDBY);
            }
        } else {
            //格式化namenode并启动
            for(OmNodeMessage namenode : namenodes){
                sendOrder(String.format(UrlMapping.REQUEST_NOTICE,namenode.getIp(),namenode.getPort()),
                        namenode.getHostname(),
                        Orders.FORMAT_HDFS_NAMENODE);
            }
            //启动secondarynamenode
            for(OmNodeMessage secondarynamenode : secondarynamenodes){
                sendOrder(String.format(UrlMapping.REQUEST_NOTICE,secondarynamenode.getIp(),secondarynamenode.getPort()),
                        secondarynamenode.getHostname(),
                        Orders.START_HDFS_SECONDARYNAMENODE);
            }
        }
        //启动datanode进程
        List<String> datanodeHostNames = blueprintService.getNodesByProcessName(Constant.HDFS_DATANODE_PROCESS);
        List<OmNodeMessage> datanodes = nodeService.getNodesByProcessName(datanodeHostNames);
        for(OmNodeMessage datanode : datanodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,datanode.getIp(),datanode.getPort()),
                    datanode.getHostname(),
                    Orders.START_HDFS_DATANODE);
        }
        //启动historyServer进程
        List<String> historyHostNames = blueprintService.getNodesByProcessName(Constant.MAPRED_HISTORYSERVER_PROCESS);
        List<OmNodeMessage> historynodes = nodeService.getNodesByProcessName(historyHostNames);
        for(OmNodeMessage history : historynodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,history.getIp(),history.getPort()),
                    history.getHostname(),
                    Orders.START_MAPRED_HISTORY);
        }
        //启动resourcemanager进程
        List<String> resourcemanagerHostNames = blueprintService.getNodesByProcessName(Constant.YARN_RESOURCEMANAGER_PROCESS);
        List<OmNodeMessage> rnodes = nodeService.getNodesByProcessName(resourcemanagerHostNames);
        for(OmNodeMessage rnode : rnodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,rnode.getIp(),rnode.getPort()),
                    rnode.getHostname(),
                    Orders.START_YARN_RESOURCEMANAGER);
        }
        //启动nodemanager进程
        List<String> nodemanagerHostNames = blueprintService.getNodesByProcessName(Constant.YARN_NODEMANAGER_PROCESS);
        List<OmNodeMessage> nnodes = nodeService.getNodesByProcessName(nodemanagerHostNames);
        for(OmNodeMessage nnode : nnodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,nnode.getIp(),nnode.getPort()),
                    nnode.getHostname(),
                    Orders.START_YARN_NODEMANAGER);
        }
        //启动timelineserver进程
        List<String> timelineserverHostNames = blueprintService.getNodesByProcessName(Constant.YARN_TIMELINESERVER_PROCESS);
        List<OmNodeMessage> timelineservernodes = nodeService.getNodesByProcessName(timelineserverHostNames);
        for(OmNodeMessage timelineservernode : timelineservernodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,timelineservernode.getIp(),timelineservernode.getPort()),
                    timelineservernode.getHostname(),
                    Orders.START_YARN_TIMELINESERVER);
        }
        //初始化hive元数据
        List<String> hiveMetastores = blueprintService.getNodesByProcessName(Constant.HIVE_METASTORE_PROCESS);
        List<OmNodeMessage> metastorenodes = nodeService.getNodesByProcessName(hiveMetastores);
        for(OmNodeMessage metastorenode : metastorenodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,metastorenode.getIp(),metastorenode.getPort()),
                    metastorenode.getHostname(),
                    Orders.INIT_HIVE_METASTORE);
        }
        //启动hive元数据服务
        for(OmNodeMessage metastorenode : metastorenodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,metastorenode.getIp(),metastorenode.getPort()),
                    metastorenode.getHostname(),
                    Orders.START_HIVE_METASTORE);
        }
        //启动hive的jdbc服务
        List<String> hiveServer2s = blueprintService.getNodesByProcessName(Constant.HIVE_HIVESERVER2_PROCESS);
        List<OmNodeMessage> server2nodes = nodeService.getNodesByProcessName(hiveServer2s);
        for(OmNodeMessage server2node : server2nodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,server2node.getIp(),server2node.getPort()),
                    server2node.getHostname(),
                    Orders.START_HIVE_HIVESERVER2);
        }
        //启动hbase的master服务
        List<String> hbaseMasters = blueprintService.getNodesByProcessName(Constant.HBASE_MASTER_PROCESS);
        List<OmNodeMessage> hbaseMasternodes = nodeService.getNodesByProcessName(hbaseMasters);
        for(OmNodeMessage  hbaseMasternode : hbaseMasternodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,hbaseMasternode.getIp(),hbaseMasternode.getPort()),
                    hbaseMasternode.getHostname(),
                    Orders.START_HBASE_MASTER);
        }
        //启动hbase的regionserver服务
        List<String> hbaseRegions = blueprintService.getNodesByProcessName(Constant.HBASE_REGIONSERVER_PROCESS);
        List<OmNodeMessage> hbaseRegionnodes = nodeService.getNodesByProcessName(hbaseRegions);
        for(OmNodeMessage  hbaseRegionnode : hbaseRegionnodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,hbaseRegionnode.getIp(),hbaseRegionnode.getPort()),
                    hbaseRegionnode.getHostname(),
                    Orders.START_HBASE_REGIONSERVER);
        }
        //启动kafka
        List<String> kafkaBrokers = blueprintService.getNodesByProcessName(Constant.KAFKA_BROKER_PROCESS);
        List<OmNodeMessage> kafkaBrokernodes = nodeService.getNodesByProcessName(kafkaBrokers);
        for(OmNodeMessage  kafkaBrokernode : kafkaBrokernodes){
            sendOrder(String.format(UrlMapping.REQUEST_NOTICE,kafkaBrokernode.getIp(),kafkaBrokernode.getPort()),
                    kafkaBrokernode.getHostname(),
                    Orders.START_KAFKA_BROKER);
        }
        //最终的日志输出
        masterLogService.insertLog(new OmOperationLog()
                .setHostname(Constant.LOCALHOST)
                .setStatus(Constant.STATUS_SUCCESS)
                .setCode("200")
                .setOperation("启动所有Hadoop相关流程")
                .setContent("已完成蓝图中所有进程的启动，具体信息请查阅日志!"));
    }

    private void sendOrder(String url, String hostname,String order){
        boolean flag = false;
        //5次失败视为下发命令失败
        for(int i=0;i<5;i++){
            Map<String, Object> response = HttpUtil.sendPost(url,
                    new NoticeModel()
                            .setTitle(order)
                            .toString());
            //获取请求状态
            Integer httpCode = (Integer) response.get("code");
            OmOperationLog omOperationLog = new OmOperationLog()
                    .setCode(httpCode.toString())
                    .setHostname(hostname);
            //解析response信息并入库
            if(httpCode == 200){
                PairModel pairModel = JSONObject.parseObject(response.get("content").toString(), PairModel.class);
                omOperationLog
                        .setStatus(Constant.STATUS_SUCCESS)
                        .setOperation(pairModel.getKey())
                        .setContent(pairModel.getKey());
                masterLogService.insertLog(omOperationLog);
                flag = true;
                break;
            } else {
                omOperationLog
                        .setStatus(Constant.STATUS_FAULT)
                        .setOperation("http请求失败")
                        .setContent(response.get("content").toString());
                masterLogService.insertLog(omOperationLog);
            }

        }
        if(!flag){
            masterLogService.insertLog(new OmOperationLog()
                    .setHostname(hostname)
                    .setStatus(Constant.STATUS_FAULT)
                    .setCode("500")
                    .setOperation(order)
                    .setContent("Five try was all failure !"));
        }
    }

}
