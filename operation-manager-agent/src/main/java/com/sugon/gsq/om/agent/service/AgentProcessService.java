package com.sugon.gsq.om.agent.service;

import com.sugon.gsq.om.constant.Constant;
import com.sugon.gsq.om.db.entity.OmProcessInfo;
import com.sugon.gsq.om.db.mapper.OmNodeMessageMapper;
import com.sugon.gsq.om.db.mapper.OmProcessInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.List;

/*
 * ClassName: NodeService
 * Author: Administrator
 * Date: 2019/9/9 16:16
 */
@Service
public class AgentProcessService {

    @Autowired
    OmProcessInfoMapper processInfoMapper;

    public int saveProcess(OmProcessInfo processInfo){
        return processInfoMapper.insert(processInfo);
    }

    public String getPidByProcessName(String processName){
        Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
        WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("process",processName);
        criteria.andEqualTo("hostname", Constant.LOCALHOST);
        List<OmProcessInfo> processes = processInfoMapper.selectByExample(weekend);
        OmProcessInfo processInfo = processes.get(0);
        return processInfo==null?null:processInfo.getPid();
    }

    public String getServiceByProcessName(String processName){
        Weekend<OmProcessInfo> weekend = Weekend.of(OmProcessInfo.class);
        WeekendCriteria<OmProcessInfo, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("process",processName);
        List<OmProcessInfo> processes = processInfoMapper.selectByExample(weekend);
        OmProcessInfo processInfo = processes.get(0);
        return processInfo==null?null:processInfo.getService();
    }

}
