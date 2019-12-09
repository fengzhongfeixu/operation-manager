package com.sugon.gsq.om.controller;

import com.battcn.boot.swagger.model.DataType;
import com.battcn.boot.swagger.model.ParamType;
import com.sugon.gsq.om.common.constant.Constant;
import com.sugon.gsq.om.db.entity.OmMonitorMessage;
import com.sugon.gsq.om.entity.monitor.SoNodesHealthEntity;
import com.sugon.gsq.om.entity.monitor.SoServerInforEntity;
import com.sugon.gsq.om.entity.server.ServerEntity;
import com.sugon.gsq.om.model.ResponseModel;
import com.sugon.gsq.om.services.MonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
 * ClassName: MonitorController
 * Author: Administrator
 * Date: 2019/8/28 19:52
 */
@Slf4j
@RestController
@RequestMapping("/monitor")
@Api(tags = "RELEASE-1.0.5", description = "指标监控", value = "指标监控")
public class MonitorController {

    private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<>();

    @Autowired
    private MonitorService monitorService;

    @GetMapping(path = "/server-overview")
    @ApiOperation(value = "服务进程监控", notes = "采集各个服务进程的基本信息")
    public ResponseModel serverOverview() {
        return new ResponseModel<List<ServerEntity>>()
                .setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData(monitorService.getServersMessage());
    }

    @GetMapping(path = "/node-health-overview")
    @ApiOperation(value = "集群主机状态监控", notes = "检测集群主机是否正常运行")
    public ResponseModel nodeHealthOverview() {
        return new ResponseModel<SoNodesHealthEntity>()
                .setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData(monitorService.getNodesHealthMessage());
    }

    @GetMapping(path = "/index-total-overview/{index}")
    @ApiOperation(value = "指标获取", notes = "集群各项指标获取的入口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "指标名称", required = true, dataType = DataType.STRING, paramType = ParamType.PATH),
            @ApiImplicitParam(name = "starttime", value = "起始时间", required = true, dataType = DataType.STRING, paramType = ParamType.QUERY),
            @ApiImplicitParam(name = "endtime", value = "终止时间", required = true, dataType = DataType.STRING, paramType = ParamType.QUERY)
    })
    public ResponseModel monitorOverview(@PathVariable String index,String starttime,String endtime) {
        List<OmMonitorMessage> omMonitorMessages;
        try {
            SimpleDateFormat sdf = threadLocal.get();
            if (sdf == null){
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            }
            Date start = sdf.parse(starttime);
            Date end = sdf.parse(endtime);
            omMonitorMessages = monitorService.getMonitorChart(index,start,end);
            for(OmMonitorMessage omHealthStatus : omMonitorMessages){
                omHealthStatus.setTime(
                        sdf.parse(sdf.format(omHealthStatus.getTime()))
                );
            }
        } catch (ParseException e) {
            return new ResponseModel<String>()
                    .setCode(Constant.HTTP_STATUS_PARAM_ERROR)
                    .setMessage(Constant.HTTP_MESSAGE_PARAM_ERROR);
        }
        return new ResponseModel<List<OmMonitorMessage>>()
                .setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData(omMonitorMessages);
    }

}
