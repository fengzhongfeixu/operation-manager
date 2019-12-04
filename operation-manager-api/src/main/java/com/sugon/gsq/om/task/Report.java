package com.sugon.gsq.om.task;

import com.sugon.gsq.om.constant.Constant;
import com.sugon.gsq.om.db.entity.OmConfigInfo;
import com.sugon.gsq.om.db.entity.OmMonitorMessage;
import com.sugon.gsq.om.db.entity.OmOperationEvent;
import com.sugon.gsq.om.db.entity.OmProcessInfo;
import com.sugon.gsq.om.db.mapper.OmMonitorMessageMapper;
import com.sugon.gsq.om.db.mapper.OmOperationEventMapper;
import com.sugon.gsq.om.db.mapper.OmProcessInfoMapper;
import com.sugon.gsq.om.services.ServicesService;
import com.sugon.gsq.om.tools.ApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
    private ServicesService servicesService;

    @Autowired
    private OmOperationEventMapper operationEventMapper;

    //上报监控信息
    @Scheduled(cron = "0 0,15,30,45 * * * ?")
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
        List<OmConfigInfo> configs = servicesService.getConfigs();
        servicesService.updateConfigOnDisk(configs);
    }

}
