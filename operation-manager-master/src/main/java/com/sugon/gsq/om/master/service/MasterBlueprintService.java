package com.sugon.gsq.om.master.service;

import com.sugon.gsq.om.common.utils.CommonUtil;
import com.sugon.gsq.om.db.entity.OmBlueprintConf;
import com.sugon.gsq.om.db.entity.OmBlueprintProcess;
import com.sugon.gsq.om.db.entity.OmConfigInfo;
import com.sugon.gsq.om.db.entity.OmOperationLog;
import com.sugon.gsq.om.db.mapper.OmBlueprintConfMapper;
import com.sugon.gsq.om.db.mapper.OmBlueprintProcessMapper;
import com.sugon.gsq.om.db.mapper.OmConfigInfoMapper;
import com.sugon.gsq.om.db.mapper.OmOperationLogMapper;
import com.sugon.gsq.om.model.BlueprintModel;
import com.sugon.gsq.om.model.ServerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * ClassName: BlueprintService
 * Author: Administrator
 * Date: 2019/9/9 20:29
 */
@Service
public class MasterBlueprintService {

    @Autowired
    OmBlueprintConfMapper omBlueprintConfMapper;

    @Autowired
    OmBlueprintProcessMapper omBlueprintProcessMapper;

    @Autowired
    OmOperationLogMapper omOperationLogMapper;

    @Autowired
    OmConfigInfoMapper omConfigInfoMapper;

    public boolean insertConf(OmBlueprintConf omBlueprintConf){
        return omBlueprintConfMapper.insert(omBlueprintConf)==1;
    }

    public boolean insertProcess(OmBlueprintProcess omBlueprintProcess){
        return omBlueprintProcessMapper.insert(omBlueprintProcess)==1;
    }

    public boolean insertRunConf(OmConfigInfo configInfo){
        return omConfigInfoMapper.insert(configInfo)==1;
    }

    @Transactional
    public void saveBlueprint(BlueprintModel blueprint){
        //删除同版本的蓝图
        deleteBlueprintByVersion(blueprint.getVersion());
        //获取公共配置项
        List<BlueprintModel.Configuration> publicConf = blueprint.getConfigurations();
        //获取私有配置项
        List<BlueprintModel.Process> privateConf = blueprint.getHosts();
        //公共配置项入库
        for(BlueprintModel.Configuration configuration : publicConf){
            String configName = configuration.getKey();
            Map<String,String> configValues = configuration.getValue();
            for(String key : configValues.keySet()){
                //蓝图信息插入
                insertConf(new OmBlueprintConf()
                        .setId(CommonUtil.getUUID())
                        .setK(key)
                        .setV(configValues.get(key))
                        .setBelong(configName)
                        .setScope("public")
                        .setVersion(blueprint.getVersion())
                );
                //运行配置插入
                insertRunConf(new OmConfigInfo()
                        .setId(CommonUtil.getUUID())
                        .setK(key)
                        .setV(configValues.get(key))
                        .setBelong(configName)
                        .setScope("public")
                        .setVersion(1));
            }
        }
        //节点对应进程清单入库
        for(BlueprintModel.Process process : privateConf){
            for(String processName : process.getComponents()){
                insertProcess(new OmBlueprintProcess()
                        .setId(CommonUtil.getUUID())
                        .setHostname(process.getName())
                        .setProcessname(processName)
                        .setVersion(blueprint.getVersion())
                );
            }
            //私有配置和进程入库
            for(BlueprintModel.Configuration configuration : process.getConfigurations()){
                String configName = configuration.getKey();
                Map<String,String> configValues = configuration.getValue();
                for(String key : configValues.keySet()){
                    //插入蓝图配置
                    insertConf(new OmBlueprintConf()
                            .setId(CommonUtil.getUUID())
                            .setK(key)
                            .setV(configValues.get(key))
                            .setBelong(configName)
                            .setScope(process.getName())
                            .setVersion(blueprint.getVersion()));
                    //运行配置插入
                    insertRunConf(new OmConfigInfo()
                            .setId(CommonUtil.getUUID())
                            .setK(key)
                            .setV(configValues.get(key))
                            .setBelong(configName)
                            .setScope(process.getName())
                            .setVersion(1));
                }
            }
        }
    }

    @Transactional
    public void deleteBlueprintByVersion(String version){
        //删除某个版本下的蓝图配置
        Weekend<OmBlueprintConf> weekend = Weekend.of(OmBlueprintConf.class);
        WeekendCriteria<OmBlueprintConf, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo("version",version);
        omBlueprintConfMapper.deleteByExample(weekend);
        //删除某个版本下的蓝图进程拓扑图
        Weekend<OmBlueprintProcess> weekend_ = Weekend.of(OmBlueprintProcess.class);
        WeekendCriteria<OmBlueprintProcess, Object> criteria_ = weekend_.weekendCriteria();
        criteria_.andEqualTo("version",version);
        omBlueprintProcessMapper.deleteByExample(weekend_);
        //删除所有残留的日志
        Weekend<OmOperationLog> weekend__ = Weekend.of(OmOperationLog.class);
        omOperationLogMapper.deleteByExample(weekend__);
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

}
