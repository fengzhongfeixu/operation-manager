package com.sugon.gsq.om.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sugon.gsq.om.db.entity.OmOperationEvent;
import com.sugon.gsq.om.db.mapper.OmOperationEventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.List;

/*
 * ClassName: SoService
 * Author: Administrator
 * Date: 2019/9/9 20:29
 */
@Service
public class EventService {

    @Autowired
    OmOperationEventMapper operationEventMapper;

    public PageInfo getEventList(String level,int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        Weekend<OmOperationEvent> weekend = Weekend.of(OmOperationEvent.class);
        WeekendCriteria<OmOperationEvent, Object> criteria = weekend.weekendCriteria();
        if(level == null || level.equals("")){
            criteria.andEqualTo("level","一般");
            criteria.orEqualTo("level","警告");
        } else {
            criteria.andEqualTo("level",level);
        }
        List<OmOperationEvent> list = operationEventMapper.selectByExample(weekend);
        return new PageInfo<>(list);
    }

    public PageInfo getWarnList(String level,int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        Weekend<OmOperationEvent> weekend = Weekend.of(OmOperationEvent.class);
        WeekendCriteria<OmOperationEvent, Object> criteria = weekend.weekendCriteria();
        if(level == null || level.equals("")){
            criteria.andEqualTo("level","严重");
            criteria.orEqualTo("level","致命");
        } else {
            criteria.andEqualTo("level",level);
        }
        List<OmOperationEvent> list = operationEventMapper.selectByExample(weekend);
        return new PageInfo<>(list);
    }

    public String cleanEvent(int id){
        return operationEventMapper.deleteByPrimaryKey(id)==1?"success":"fault";
    }

}
