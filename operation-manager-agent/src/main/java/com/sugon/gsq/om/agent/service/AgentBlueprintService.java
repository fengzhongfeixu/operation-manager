package com.sugon.gsq.om.agent.service;

import com.sugon.gsq.om.db.entity.OmBlueprintConf;
import com.sugon.gsq.om.db.entity.OmBlueprintProcess;
import com.sugon.gsq.om.db.mapper.OmBlueprintConfMapper;
import com.sugon.gsq.om.db.mapper.OmBlueprintProcessMapper;
import com.sugon.gsq.om.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.ArrayList;
import java.util.List;

/*
 * ClassName: BlueprintService
 * Author: Administrator
 * Date: 2019/9/9 20:29
 */
@Service
public class AgentBlueprintService {

    @Autowired
    OmBlueprintConfMapper omBlueprintConfMapper;

    @Autowired
    OmBlueprintProcessMapper omBlueprintProcessMapper;

    public List<OmBlueprintConf> getConfigs(){
        Weekend<OmBlueprintConf> weekend = Weekend.of(OmBlueprintConf.class);
        WeekendCriteria<OmBlueprintConf, Object> criteria = weekend.weekendCriteria();
        criteria.orEqualTo("scope",Constant.LOCALHOST);
        criteria.orEqualTo("scope","public");
        List<OmBlueprintConf> configs = omBlueprintConfMapper.selectByExample(weekend);
        return configs;
    }

    public List<String> getNodesByProcessName(String processName){
        Weekend<OmBlueprintProcess> weekend = Weekend.of(OmBlueprintProcess.class);
        WeekendCriteria<OmBlueprintProcess, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("processname", processName);
        List<String> result = new ArrayList<>();
        for(OmBlueprintProcess omBlueprintProcess : omBlueprintProcessMapper.selectByExample(weekend)){
            result.add(omBlueprintProcess.getHostname());
        }
        return result;
    }

    public List<String> getProcessByHostname(){
        Weekend<OmBlueprintProcess> weekend = Weekend.of(OmBlueprintProcess.class);
        WeekendCriteria<OmBlueprintProcess, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("hostname", Constant.LOCALHOST);
        List<String> result = new ArrayList<>();
        for(OmBlueprintProcess omBlueprintProcess : omBlueprintProcessMapper.selectByExample(weekend)){
            result.add(omBlueprintProcess.getProcessname());
        }
        return result;
    }

}
