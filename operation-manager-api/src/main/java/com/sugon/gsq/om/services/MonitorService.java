package com.sugon.gsq.om.services;

import com.google.common.collect.Lists;
import com.sugon.gsq.om.constant.Constant;
import com.sugon.gsq.om.db.entity.OmBlueprintProcess;
import com.sugon.gsq.om.db.entity.OmMonitorMessage;
import com.sugon.gsq.om.db.entity.OmProcessInfo;
import com.sugon.gsq.om.db.mapper.OmBlueprintProcessMapper;
import com.sugon.gsq.om.db.mapper.OmConfigInfoMapper;
import com.sugon.gsq.om.db.mapper.OmMonitorMessageMapper;
import com.sugon.gsq.om.db.mapper.OmProcessInfoMapper;
import com.sugon.gsq.om.entity.monitor.SoNodesHealthEntity;
import com.sugon.gsq.om.entity.monitor.SoServerInforEntity;
import com.sugon.gsq.om.entity.server.ServerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.*;

/*
 * ClassName: SoService
 * Author: Administrator
 * Date: 2019/9/9 20:29
 */
@Service
public class MonitorService {

    @Autowired
    OmBlueprintProcessMapper omBlueprintProcessMapper;

    @Autowired
    OmMonitorMessageMapper monitorMessageMapper;

    @Autowired
    ServicesService servicesService;

    public List<ServerEntity> getServersMessage(){
        return servicesService.getServersMessage();
    }

    public SoNodesHealthEntity getNodesHealthMessage(){
        return new SoNodesHealthEntity(0.8,0.1,0.1);
    }

    public List<OmMonitorMessage> getMonitorChart(String index, Date startTime, Date endTime){
        Weekend<OmMonitorMessage> weekend = Weekend.of(OmMonitorMessage.class);
        WeekendCriteria<OmMonitorMessage, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("project",index);
        criteria.andGreaterThan("time",startTime);
        criteria.andLessThan("time",endTime);
        return monitorMessageMapper.selectByExample(weekend);
    }

}
