package com.sugon.gsq.om.agent.service;

import com.sugon.gsq.om.common.constant.Constant;
import com.sugon.gsq.om.db.entity.OmOperationEvent;
import com.sugon.gsq.om.db.entity.OmProcessInfo;
import com.sugon.gsq.om.db.mapper.OmOperationEventMapper;
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
public class AgentEventService {

    @Autowired
    OmOperationEventMapper operationEventMapper;

    public int saveEvent(OmOperationEvent operationEvent){
        return operationEventMapper.insert(operationEvent);
    }

    public int cleanMessage(OmOperationEvent operationEvent){
        return operationEventMapper.updateByPrimaryKey(operationEvent.setOutdate(true));
    }

}
