package com.sugon.gsq.om.services;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sugon.gsq.om.common.constant.Constant;
import com.sugon.gsq.om.common.constant.UrlMapping;
import com.sugon.gsq.om.common.utils.CfgUtil;
import com.sugon.gsq.om.common.utils.CommonUtil;
import com.sugon.gsq.om.common.utils.HttpUtil;
import com.sugon.gsq.om.common.utils.XmlUtil;
import com.sugon.gsq.om.db.entity.*;
import com.sugon.gsq.om.db.mapper.*;
import com.sugon.gsq.om.entity.server.ConfigEntity;
import com.sugon.gsq.om.entity.server.ProcessEntity;
import com.sugon.gsq.om.entity.server.ServerEntity;
import com.sugon.gsq.om.model.NoticeModel;
import com.sugon.gsq.om.model.PairModel;
import com.sugon.gsq.om.tools.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.io.File;
import java.io.IOException;
import java.util.*;

/*
 * ClassName: ServicesService
 * Author: Administrator
 * Date: 2019/9/9 20:29
 */
@Service
public class ServicesService {

    @Autowired
    OmProcessInfoMapper processInfoMapper;

    @Autowired
    OmConfigInfoMapper configInfoMapper;

    @Autowired
    OmNodeMessageMapper nodeMessageMapper;

    @Autowired
    OmOperationLogMapper operationLogMapper;

    @Autowired
    OmOperationEventMapper operationEventMapper;

    public List<ServerEntity> getServersMessage(){
        List<OmProcessInfo> omProcessInfos = processInfoMapper.selectAll();
        List<ServerEntity> serverEntities = Lists.newLinkedList();
        loop:
        for(OmProcessInfo omProcessInfo : omProcessInfos){
            String appName = omProcessInfo.getService();
            String processName = omProcessInfo.getProcess();
            //进程运行状态不考虑
            for(ServerEntity serverEntity : serverEntities){
                if(serverEntity.getName().equals(appName)){
                    if(serverEntity.getRole().containsKey(processName)){
                        Integer newNum = serverEntity.getRole().get(processName) + 1;
                        serverEntity.getRole().put(processName,newNum);
                    } else {
                        serverEntity.getRole().put(processName,1);
                    }
                    continue loop;
                }
            }
            //没有该Hadoop组件
            Map<String,Integer> processes = new HashMap<>();
            processes.put(processName,1);
            serverEntities.add(
                    new ServerEntity()
                            .setName(appName)
                            .setStatus(startOrNot(appName))
                            .setHealth(health(appName))
                            .setConfig(syOrNot(appName))
                            .setRole(processes)
            );
        }
        return serverEntities;
    }

    public List<ProcessEntity> getProcessByName(String serviceName){
        Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
        WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("service",serviceName);
        List<OmProcessInfo> processes = processInfoMapper.selectByExample(weekend);
        List<ProcessEntity> result = Lists.newLinkedList();
        for(OmProcessInfo process : processes){
            boolean flag = true;
            String processname = process.getProcess();
            for(ProcessEntity pe : result){
                if(pe.getName().equals(processname)){
                    if(process.getStatus().equals("running")){
                        pe.setNormal(pe.getNormal() + 1);
                    }
                    pe.setTotal(pe.getTotal() + 1);
                    pe.setStatus(process.getSame().equals("yes"));
                    flag = false;
                    break;
                }
            }
            if(flag){
                ProcessEntity processEntity = new ProcessEntity()
                        .setName(processname)
                        .setStatus(process.getSame().equals("yes"))
                        .setTotal(1)
                        .setUrl("www.sugon.com");
                //判断是否运行中
                if(process.getStatus().equals("running")){
                    processEntity.setNormal(1);
                } else {
                    processEntity.setNormal(0);
                }
                result.add(processEntity);
            }
        }
        return result;
    }

    public List<ConfigEntity> getConfigByService(String serviceName){
        List<ConfigEntity> configsList = Lists.newLinkedList();
        if(serviceName.equals("zookeeper")){
            configsList.add(new ConfigEntity()
                    .setFilename("zoo.cfg")
                    .setFlag("zoo")
                    .setDescribe("zk主服务配置文件"));
        } else if(serviceName.equals("HDFS")){
            configsList.add(new ConfigEntity()
                    .setFilename("core-site.xml")
                    .setFlag("core-site")
                    .setDescribe("hdfs基础配置文件"));
            configsList.add(new ConfigEntity()
                    .setFilename("core-site.xml")
                    .setFlag("hdfs-site")
                    .setDescribe("hdfs扩展配置文件"));
        } else if(serviceName.equals("MapReduce")){
            configsList.add(new ConfigEntity()
                    .setFilename("mapred-site.xml")
                    .setFlag("mapred-site")
                    .setDescribe("mapreduce配置文件"));
        } else if(serviceName.equals("Yarn")){
            configsList.add(new ConfigEntity()
                    .setFilename("yarn-site.xml")
                    .setFlag("yarn-site")
                    .setDescribe("Yarn主配置文件"));
        } else if(serviceName.equals("hive")){
            configsList.add(new ConfigEntity()
                    .setFilename("hive-site.xml")
                    .setFlag("hive-site")
                    .setDescribe("hive主配置文件"));
        } else if(serviceName.equals("Spark")){
            configsList.add(new ConfigEntity()
                    .setFilename("hive-site.xml")
                    .setFlag("spark-hive-site")
                    .setDescribe("spark连接hive配置文件"));
        } else if(serviceName.equals("Hbase")){
            configsList.add(new ConfigEntity()
                    .setFilename("hbase-site.xml")
                    .setFlag("hbase-site")
                    .setDescribe("Hbase主配置文件"));
        } else if(serviceName.equals("Kafka")){
            configsList.add(new ConfigEntity()
                    .setFilename("server.properties")
                    .setFlag("kafka-server")
                    .setDescribe("Kafka消息队列主配置文件"));
        }
        return configsList;
    }

    public List<OmConfigInfo> getConfigContent(String flag){
        Weekend<OmConfigInfo> weekend = Weekend.of(OmConfigInfo.class);
        WeekendCriteria<OmConfigInfo, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("belong",flag);
        criteria.andEqualTo("scope","public");
        List<OmConfigInfo> configInfoList = configInfoMapper.selectByExample(weekend);
        return configInfoList;
    }

    @Transactional
    public String updateConfig(List<OmConfigInfo> configs){
        Set<String> updateServices = Sets.newHashSet();
        for(OmConfigInfo config : configs){
            if(config.getId() == null || config.getId().equals("")){
                //新增配置(需要k，v，belong参数)
                configInfoMapper.insert(config
                        .setId(ApiUtil.getUUID())
                        .setVersion(1)
                        .setScope("public"));
            } else {
                if(config.getV() == null || config.getV().equals("")){
                    //删除配置项
                    configInfoMapper.deleteByPrimaryKey(config.getId());
                } else {
                    //修改原有配置
                    OmConfigInfo configInfo = configInfoMapper.selectByPrimaryKey(config.getId());
                    if(configInfo != null){
                        configInfo.setV(config.getV());
                        configInfo.setVersion(configInfo.getVersion() + 1);
                        configInfoMapper.updateByPrimaryKey(configInfo);
                    }
                }
            }
            //修改配置文件同步状态
            changeSame(config);
            updateServices.add(config.getBelong());
        }
        updateServices.forEach(entity -> {
            //记录事件信息
            operationEventMapper.insert(new OmOperationEvent()
                    .setCreatetime(new Date())
                    .setDescription("修改" + entity + "配置文件")
                    .setLevel("警告")
                    .setOutdate(false)
                    .setService(translation(entity)));
        });
        return Constant.STATUS_SUCCESS;
    }

    public String excommandProcess(String process, String order){
        Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
        WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("process",process);
        List<OmProcessInfo> processes = processInfoMapper.selectByExample(weekend);
        String seriveName = null;
        for(OmProcessInfo processInfo : processes){
            seriveName = processInfo.getService();
            Weekend<OmNodeMessage> weekend_ = Weekend.of(OmNodeMessage.class);
            WeekendCriteria<OmNodeMessage, Object> criteria_ = weekend_.weekendCriteria();
            criteria_.andEqualTo("hostname",processInfo.getHostname());
            criteria_.andEqualTo("role","agent");
            List<OmNodeMessage> nodeMessages = nodeMessageMapper.selectByExample(weekend_);
            OmNodeMessage slave = nodeMessages.get(0);
            //向agent传达命令
            Map<String, Object> response = HttpUtil.sendPost(String.format(UrlMapping.REQUEST_COMMAND,slave.getIp(),slave.getPort())
                    ,new NoticeModel().setTitle(order).setMessage(process).toString());
            OmOperationLog operationLog = null;
            if((Integer)response.get("code") == 200){
                PairModel agentResponse = JSONObject.parseObject(response.get("content").toString(), PairModel.class);
                operationLog = new OmOperationLog()
                        .setCode(response.get("code").toString())
                        .setStatus(Constant.STATUS_SUCCESS)
                        .setHostname(processInfo.getHostname())
                        .setOperation(agentResponse.getKey())
                        .setContent(agentResponse.getValue());
            } else {
                operationLog = new OmOperationLog()
                        .setCode(response.get("code").toString())
                        .setStatus(Constant.STATUS_FAULT)
                        .setHostname(processInfo.getHostname())
                        .setOperation(process + "进程" + order + "操作失败")
                        .setContent("fault");
            }
            operationLogMapper.insert(operationLog);
            //修改配置文件同步状态
            processInfoMapper.updateByPrimaryKey(processInfo.setSame("yes"));
        }
        operationEventMapper.insert(new OmOperationEvent().setCreatetime(new Date())
                .setDescription(translation(order) + "了" + process + "进程")
                .setLevel("警告")
                .setOutdate(false)
                .setService(seriveName));
        return Constant.STATUS_SUCCESS;
    }

    public String excommandService(String service, String order){
        Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
        WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("service",service);
        List<OmProcessInfo> processes = processInfoMapper.selectByExample(weekend);
        for(OmProcessInfo processInfo : processes){
            Weekend<OmNodeMessage> weekend_ = Weekend.of(OmNodeMessage.class);
            WeekendCriteria<OmNodeMessage, Object> criteria_ = weekend_.weekendCriteria();
            criteria_.andEqualTo("hostname",processInfo.getHostname());
            criteria_.andEqualTo("role","agent");
            List<OmNodeMessage> nodeMessages = nodeMessageMapper.selectByExample(weekend_);
            OmNodeMessage slave = nodeMessages.get(0);
            //向agent传达命令
            Map<String, Object> response = HttpUtil.sendPost(String.format(UrlMapping.REQUEST_COMMAND,slave.getIp(),slave.getPort())
                    ,new NoticeModel().setTitle(order).setMessage(processInfo.getProcess()).toString());
            OmOperationLog operationLog = null;
            if((Integer)response.get("code") == 200){
                PairModel agentResponse = JSONObject.parseObject(response.get("content").toString(), PairModel.class);
                operationLog = new OmOperationLog()
                        .setCode(response.get("code").toString())
                        .setStatus(Constant.STATUS_SUCCESS)
                        .setHostname(processInfo.getHostname())
                        .setOperation(agentResponse.getKey())
                        .setContent(agentResponse.getValue());
            } else {
                operationLog = new OmOperationLog()
                        .setCode(response.get("code").toString())
                        .setStatus(Constant.STATUS_FAULT)
                        .setHostname(processInfo.getHostname())
                        .setOperation(service + "服务" + order + "操作失败")
                        .setContent("fault");
            }
            operationLogMapper.insert(operationLog);
            //修改配置文件同步状态
            processInfoMapper.updateByPrimaryKey(processInfo.setSame("yes"));
        }
        operationEventMapper.insert(new OmOperationEvent().setCreatetime(new Date())
                .setDescription(translation(order) + "了" + service + "服务")
                .setLevel("警告")
                .setOutdate(false)
                .setService(service));
        return Constant.STATUS_SUCCESS;
    }

    private boolean startOrNot(String serviceName){
        boolean result = true;
        Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
        WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("service",serviceName);
        List<OmProcessInfo> processes = processInfoMapper.selectByExample(weekend);
        List<String> started = Lists.newLinkedList();
        for(OmProcessInfo process : processes){
            if(process.getStatus().equals("running")){
                started.add(process.getProcess());
            }
        }
        //主进程宕机视为服务未启动
        if(serviceName.equals("HDFS")){
            if(!started.contains("namenode")){
                result = false;
            }
        } else if(serviceName.equals("zookeeper")){
            if(health(serviceName).equals(Constant.PROCESS_HEALTH_ERROR)){
                result = false;
            }
        } else if(serviceName.equals("Yarn")){
            if(!started.contains("resourceManager")){
                result = false;
            }
        } else if(serviceName.equals("hive")){
            if(!started.contains("metastore")){
                result = false;
            }
        } else if(serviceName.equals("MapReduce")){
            if(!started.contains("jobHistoryServer")){
                result = false;
            }
        } else if(serviceName.equals("Hbase")){
            if(!started.contains("HMaster")){
                result = false;
            }
        } else if(serviceName.equals("Kafka")){
            if(health(serviceName).equals(Constant.PROCESS_HEALTH_ERROR)){
                result = false;
            }
        }
        return result;
    }

    private String health(String serviceName){
        String result = null;
        Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
        WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("service",serviceName);
        List<OmProcessInfo> total = processInfoMapper.selectByExample(weekend);
        List<OmProcessInfo> stoped = Lists.newLinkedList();
        for(OmProcessInfo process : total){
            if(process.getStatus().equals("stop")){
                stoped.add(process);
                break;
            }
        }
        //宕机 50% 以上视为不可用
        double usage = (double) (stoped.size() / total.size());
        if (usage == 0) {
            result = Constant.PROCESS_HEALTH_GOOD;
        } else if (usage < 0.5) {
            result = Constant.PROCESS_HEALTH_WARN;
        } else {
            result = Constant.PROCESS_HEALTH_ERROR;
        }
        return result;
    }

    private boolean syOrNot(String serviceName){
        boolean result = true;
        Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
        WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("service",serviceName);
        List<OmProcessInfo> processes = processInfoMapper.selectByExample(weekend);
        List<String> processList = Lists.newLinkedList();
        for(OmProcessInfo process : processes){
            processList.add(process.getSame());
        }
        if(processList.contains("no")){
            result = false;
        }
        return result;
    }

    private void changeSame(OmConfigInfo config){
        if(config.getBelong().equals("zoo")){
            Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
            WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
            criteria.andEqualTo("service","zookeeper");
            criteria.andEqualTo("same","yes");
            List<OmProcessInfo> processInfos = processInfoMapper.selectByExample(weekend);
            for(OmProcessInfo processInfo : processInfos){
                processInfoMapper.updateByPrimaryKey(processInfo.setSame("no"));
            }
        } else if (config.getBelong().equals("core-site") || config.getBelong().equals("hdfs-site")){
            Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
            WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
            criteria.andEqualTo("service","HDFS");
            criteria.andEqualTo("same","yes");
            List<OmProcessInfo> processInfos = processInfoMapper.selectByExample(weekend);
            for(OmProcessInfo processInfo : processInfos){
                processInfoMapper.updateByPrimaryKey(processInfo.setSame("no"));
            }
        } else if (config.getBelong().equals("mapred-site")){
            Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
            WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
            criteria.andEqualTo("service","MapReduce");
            criteria.andEqualTo("same","yes");
            List<OmProcessInfo> processInfos = processInfoMapper.selectByExample(weekend);
            for(OmProcessInfo processInfo : processInfos){
                processInfoMapper.updateByPrimaryKey(processInfo.setSame("no"));
            }
        } else if (config.getBelong().equals("yarn-site")){
            Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
            WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
            criteria.andEqualTo("service","Yarn");
            criteria.andEqualTo("same","yes");
            List<OmProcessInfo> processInfos = processInfoMapper.selectByExample(weekend);
            for(OmProcessInfo processInfo : processInfos){
                processInfoMapper.updateByPrimaryKey(processInfo.setSame("no"));
            }
        } else if (config.getBelong().equals("hive-site")){
            Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
            WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
            criteria.andEqualTo("service","hive");
            criteria.andEqualTo("same","yes");
            List<OmProcessInfo> processInfos = processInfoMapper.selectByExample(weekend);
            for(OmProcessInfo processInfo : processInfos){
                processInfoMapper.updateByPrimaryKey(processInfo.setSame("no"));
            }
        } else if (config.getBelong().equals("spark-hive-site")){
            Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
            WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
            criteria.andEqualTo("service","Spark");
            criteria.andEqualTo("same","yes");
            List<OmProcessInfo> processInfos = processInfoMapper.selectByExample(weekend);
            for(OmProcessInfo processInfo : processInfos){
                processInfoMapper.updateByPrimaryKey(processInfo.setSame("no"));
            }
        } else if (config.getBelong().equals("hbase-site")){
            Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
            WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
            criteria.andEqualTo("service","Hbase");
            criteria.andEqualTo("same","yes");
            List<OmProcessInfo> processInfos = processInfoMapper.selectByExample(weekend);
            for(OmProcessInfo processInfo : processInfos){
                processInfoMapper.updateByPrimaryKey(processInfo.setSame("no"));
            }
        } else if (config.getBelong().equals("kafka-server")){
            Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
            WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
            criteria.andEqualTo("service","Kafka");
            criteria.andEqualTo("same","yes");
            List<OmProcessInfo> processInfos = processInfoMapper.selectByExample(weekend);
            for(OmProcessInfo processInfo : processInfos){
                processInfoMapper.updateByPrimaryKey(processInfo.setSame("no"));
            }
        }
    }

    private String translation(String keyWord){
        String result = null;
        if(keyWord.equals("core-site") || keyWord.equals("hdfs-site")){
            result = "HDFS";
        } else if (keyWord.equals("zoo")) {
            result = "zookeeper";
        } else if (keyWord.equals("mapred-site")) {
            result = "MapReduce";
        } else if (keyWord.equals("yarn-site")) {
            result = "Yarn";
        } else if (keyWord.equals("hive-site")) {
            result = "hive";
        } else if (keyWord.equals("hbase-site")) {
            result = "Hbase";
        } else if (keyWord.equals("kafka-server")) {
            result = "Kafka";
        } else if (keyWord.equals("start")) {
            result = "启动";
        } else if (keyWord.equals("stop")) {
            result = "停止";
        } else if (keyWord.equals("restart")) {
            result = "重启";
        }
        return result;
    }

}
