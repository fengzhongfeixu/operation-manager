package com.sugon.gsq.om.services;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sugon.gsq.om.common.constant.UrlMapping;
import com.sugon.gsq.om.common.utils.HttpUtil;
import com.sugon.gsq.om.db.entity.OmNodeMessage;
import com.sugon.gsq.om.db.mapper.OmMonitorMessageMapper;
import com.sugon.gsq.om.db.mapper.OmNodeMessageMapper;
import com.sugon.gsq.om.entity.node.NodeEntity;
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
            Map<String,Object> response = HttpUtil.sendPost(String.format(UrlMapping.REQUEST_MESSAGE,node.getIp(),node.getPort()), null);
            if((Integer)response.get("code") != 200){
               node.setStatus("weak");
            } else {
               Map<String, String> hardwareInfo = JSONObject.parseObject(response.get("content").toString(), Map.class);
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
