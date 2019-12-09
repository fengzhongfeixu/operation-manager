package com.sugon.gsq.om.master.service;

import com.sugon.gsq.om.common.constant.Constant;
import com.sugon.gsq.om.db.entity.OmNodeMessage;
import com.sugon.gsq.om.db.entity.OmOperationLog;
import com.sugon.gsq.om.db.mapper.OmOperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.ArrayList;
import java.util.List;

/*
 * ClassName: MasterLogService
 * Author: Administrator
 * Date: 2019/9/27 20:44
 */
@Service
public class MasterLogService {

    @Autowired
    OmOperationLogMapper omOperationLogMapper;

    public boolean insertLog(OmOperationLog omOperationLog){
        return omOperationLogMapper.insert(omOperationLog)==1;
    }

    public List<OmOperationLog> getOperationLogs(){
        return omOperationLogMapper.selectAll();
    }

    public void deleteAllLogs(){
        Weekend<OmOperationLog> weekend = Weekend.of(OmOperationLog.class);
        omOperationLogMapper.deleteByExample(weekend);
    }

}
