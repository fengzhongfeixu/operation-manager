package com.sugon.gsq.om.proxy.service;

import com.sugon.gsq.om.db.entity.OmBlueprintConf;
import com.sugon.gsq.om.db.mapper.OmBlueprintConfMapper;
import com.sugon.gsq.om.model.ServerModel;
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
public class HttpProxyService {

    @Autowired
    OmBlueprintConfMapper omBlueprintConfMapper;

    public List<ServerModel> getHDFSNodes(){
        Weekend<OmBlueprintConf> weekend = Weekend.of(OmBlueprintConf.class);
        WeekendCriteria<OmBlueprintConf, Object> criteria = weekend.weekendCriteria();
        criteria.andLike("k","dfs.namenode.http-address%");
        List<OmBlueprintConf> configs = omBlueprintConfMapper.selectByExample(weekend);
        List<ServerModel> result = new ArrayList<>();
        for(OmBlueprintConf config : configs){
            String[] hp = config.getV().split(":");
            result.add(new ServerModel().setHostname(hp[0]).setPort(hp[1]));
        }
        return result;
    }

    public List<ServerModel> getResourceManagerNodes(){
        Weekend<OmBlueprintConf> weekend = Weekend.of(OmBlueprintConf.class);
        WeekendCriteria<OmBlueprintConf, Object> criteria = weekend.weekendCriteria();
        criteria.andLike("k","yarn.resourcemanager.webapp.address%");
        List<OmBlueprintConf> configs = omBlueprintConfMapper.selectByExample(weekend);
        List<ServerModel> result = new ArrayList<>();
        for(OmBlueprintConf config : configs){
            String[] hp = config.getV().split(":");
            result.add(new ServerModel().setHostname(hp[0]).setPort(hp[1]));
        }
        return result;
    }

}
