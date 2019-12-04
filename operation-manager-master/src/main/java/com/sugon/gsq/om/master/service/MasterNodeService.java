package com.sugon.gsq.om.master.service;

import com.sugon.gsq.om.db.entity.OmNodeMessage;
import com.sugon.gsq.om.db.mapper.OmNodeMessageMapper;
import com.sugon.gsq.om.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.ArrayList;
import java.util.List;

/*
 * ClassName: NodeService
 * Author: Administrator
 * Date: 2019/9/9 16:16
 */
@Service
public class MasterNodeService {

    @Autowired
    OmNodeMessageMapper omNodeMessageMapper;

    public Boolean register(OmNodeMessage onm){
        return omNodeMessageMapper.insert(onm)==1;
    }

    public Boolean delete(String hostname,String role){
        Weekend<OmNodeMessage> weekend = Weekend.of(OmNodeMessage.class);
        WeekendCriteria<OmNodeMessage, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("hostname",hostname);
        criteria.andEqualTo("role",role);
        return omNodeMessageMapper.deleteByExample(weekend)==1;
    }

    public boolean isExistMaster(){
        Weekend<OmNodeMessage> weekend = Weekend.of(OmNodeMessage.class);
        WeekendCriteria<OmNodeMessage, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("role","master");
        return omNodeMessageMapper.selectByExample(weekend).size() > 1;
    }

    public List<OmNodeMessage> getAllSlaves(){
        Weekend<OmNodeMessage> weekend = Weekend.of(OmNodeMessage.class);
        WeekendCriteria<OmNodeMessage, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("role","agent");
        return omNodeMessageMapper.selectByExample(weekend);
    }

    public List<OmNodeMessage> getNodesByProcessName(List<String> hostnames){
        Weekend<OmNodeMessage> weekend = Weekend.of(OmNodeMessage.class);
        WeekendCriteria<OmNodeMessage, Object> criteria = weekend.weekendCriteria();
        criteria.andIn("hostname",hostnames);
        criteria.andNotEqualTo("role", Constant.MASTER);
        return hostnames.size()==0?new ArrayList<>():omNodeMessageMapper.selectByExample(weekend);
    }

}
