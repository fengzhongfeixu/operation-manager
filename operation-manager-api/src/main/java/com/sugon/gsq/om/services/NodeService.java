package com.sugon.gsq.om.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sugon.gsq.om.constant.Constant;
import com.sugon.gsq.om.constant.UrlMapping;
import com.sugon.gsq.om.db.entity.OmMonitorMessage;
import com.sugon.gsq.om.db.entity.OmNodeMessage;
import com.sugon.gsq.om.db.entity.OmOperationEvent;
import com.sugon.gsq.om.db.mapper.OmMonitorMessageMapper;
import com.sugon.gsq.om.db.mapper.OmNodeMessageMapper;
import com.sugon.gsq.om.db.mapper.OmOperationEventMapper;
import com.sugon.gsq.om.entity.node.NodeEntity;
import com.sugon.gsq.om.tools.ApiUtil;
import com.sugon.gsq.om.tools.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.List;
import java.util.Map;

/*
 * ClassName: SoService
 * Author: Administrator
 * Date: 2019/9/9 20:29
 */
@Service
public class NodeService {

    @Autowired
    OmNodeMessageMapper nodeMessageMapper;

    @Autowired
    OmMonitorMessageMapper monitorMessageMapper;

    public PageInfo getNodeList(int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        Weekend<OmNodeMessage> weekend = Weekend.of(OmNodeMessage.class);
        WeekendCriteria<OmNodeMessage, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("role","agent");
        List<OmNodeMessage> nodes = nodeMessageMapper.selectByExample(weekend);
        for(OmNodeMessage node : nodes){
            NodeEntity nodeEntity = new NodeEntity();
            Map<String,String> hardwareInfo = HttpUtil.getMessage(String.format(UrlMapping.REQUEST_MESSAGE,node.getIp(),node.getPort()));
            if(hardwareInfo.keySet().contains("error")){
               node.setStatus("weak");
            } else {
               node.setStatus("health");
               node.setCpuusage(hardwareInfo.get("monitor_cpu_usage"));
               node.setDiskusage(hardwareInfo.get("monitor_disk_usage"));
               node.setDiskused(hardwareInfo.get("monitor_disk_used"));
               node.setMemoryusage(hardwareInfo.get("monitor_memory_usage"));
               node.setMemoryused(hardwareInfo.get("monitor_memory_used"));
               node.setNetread(hardwareInfo.get("monitor_network_read"));
               node.setNetwrite(hardwareInfo.get("monitor_network_write"));
            }
        }
        return new PageInfo<>(nodes);
    }

}
