package com.sugon.gsq.om.controller;

import com.battcn.boot.swagger.model.DataType;
import com.battcn.boot.swagger.model.ParamType;
import com.sugon.gsq.om.common.constant.Constant;
import com.sugon.gsq.om.db.entity.OmConfigInfo;
import com.sugon.gsq.om.entity.monitor.SoServerInforEntity;
import com.sugon.gsq.om.entity.server.ConfigEntity;
import com.sugon.gsq.om.entity.server.ProcessEntity;
import com.sugon.gsq.om.entity.server.ServerEntity;
import com.sugon.gsq.om.model.PairModel;
import com.sugon.gsq.om.model.ResponseModel;
import com.sugon.gsq.om.services.MonitorService;
import com.sugon.gsq.om.services.ServicesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * ClassName: ServicesController
 * Author: Administrator
 * Date: 2019/8/28 19:52
 */
@Slf4j
@RestController
@RequestMapping("/service")
@Api(tags = "RELEASE-1.0.0", description = "服务管理", value = "服务管理")
public class ServicesController {

    private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<>();

    @Autowired
    private ServicesService servicesService;

    @GetMapping(path = "/server-list")
    @ApiOperation(value = "服务列表", notes = "采集各个服务的基本信息")
    public ResponseModel serverList() {
        return new ResponseModel<List<ServerEntity>>()
                .setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData(servicesService.getServersMessage());
    }

    @GetMapping(path = "/process-list/{name}")
    @ApiOperation(value = "进程列表", notes = "采集各个服务下子进程的基本信息")
    @ApiImplicitParam(name = "name", value = "服务名称", required = true, dataType = DataType.STRING, paramType = ParamType.PATH)
    public ResponseModel processList(@PathVariable String name) {
        return new ResponseModel<List<ProcessEntity>>()
                .setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData(servicesService.getProcessByName(name));
    }

    @GetMapping(path = "/config-list/{name}")
    @ApiOperation(value = "服务配置文件列表", notes = "配置文件列表")
    @ApiImplicitParam(name = "name", value = "服务名称", required = true, dataType = DataType.STRING, paramType = ParamType.PATH)
    public ResponseModel configList(@PathVariable String name) {
        return new ResponseModel<List<ConfigEntity>>()
                .setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData(servicesService.getConfigByService(name));
    }

    @GetMapping(path = "/config-content/{flag}")
    @ApiOperation(value = "配置文件内容", notes = "配置文件内容k-v列表")
    @ApiImplicitParam(name = "flag", value = "配置文件在数据库中的key", required = true, dataType = DataType.STRING, paramType = ParamType.PATH)
    public ResponseModel configContent(@PathVariable String flag) {
        return new ResponseModel<List<OmConfigInfo>>()
                .setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData(servicesService.getConfigContent(flag));
    }

    @PostMapping(path = "/config-update")
    @ApiOperation(value = "修改配置文件内容", notes = "修改相关配置k-v")
    public ResponseModel updateConfig(@RequestBody List<OmConfigInfo> configs) {
        return new ResponseModel<String>()
                .setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData(servicesService.updateConfig(configs));
    }

    @PostMapping(path = "/ex-command")
    @ApiOperation(value = "服务或进程命令调用", notes = "执行启动、停止、重启命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "service", value = "服务名称", required = false, dataType = DataType.STRING, paramType = ParamType.QUERY, defaultValue = ""),
            @ApiImplicitParam(name = "process", value = "进程名称", required = false, dataType = DataType.STRING, paramType = ParamType.QUERY, defaultValue = ""),
            @ApiImplicitParam(name = "order", value = "命令标识", required = true, dataType = DataType.STRING, paramType = ParamType.QUERY, defaultValue = "")
    })
    public ResponseModel excommand(String service,String process,String order) {
        ResponseModel<String> responseModel = null;
        if(service==null || service.equals("")){
            responseModel = new ResponseModel<String>()
                                .setCode(Constant.HTTP_STATUS_SUCCESS)
                                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                                .setData(servicesService.excommandProcess(process,order));
        } else {
            responseModel = new ResponseModel<String>()
                    .setCode(Constant.HTTP_STATUS_SUCCESS)
                    .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                    .setData(servicesService.excommandService(service,order));
        }
        return responseModel;
    }

}
