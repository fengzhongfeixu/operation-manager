package com.sugon.gsq.om.controller;

import com.battcn.boot.swagger.model.DataType;
import com.battcn.boot.swagger.model.ParamType;
import com.sugon.gsq.om.constant.Constant;
import com.sugon.gsq.om.db.entity.OmConfigInfo;
import com.sugon.gsq.om.entity.server.ConfigEntity;
import com.sugon.gsq.om.entity.server.ProcessEntity;
import com.sugon.gsq.om.entity.server.ServerEntity;
import com.sugon.gsq.om.model.ResponseModel;
import com.sugon.gsq.om.services.EventService;
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

/*
 * ClassName: ServicesController
 * Author: Administrator
 * Date: 2019/8/28 19:52
 */
@Slf4j
@RestController
@RequestMapping("/event")
@Api(tags = "RELEASE-3.4.0", description = "事件管理", value = "事件管理")
public class EventController {

    private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<>();

    @Autowired
    private EventService eventService;

    @GetMapping(path = "/event-list")
    @ApiOperation(value = "事件列表", notes = "展示集群运维操作记录的列表")
    public ResponseModel eventList(String level,@RequestParam() int pageNum, @RequestParam() int pageSize) {
        return new ResponseModel<>()
                .setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData(eventService.getEventList(level,pageNum,pageSize));
    }

    @GetMapping(path = "/warn-list")
    @ApiOperation(value = "告警列表", notes = "告警信息展示的列表")
    public ResponseModel processList(String level,@RequestParam() int pageNum, @RequestParam() int pageSize) {
        return new ResponseModel<>()
                .setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData(eventService.getWarnList(level,pageNum,pageSize));
    }

    @GetMapping(path = "/clean-event")
    @ApiOperation(value = "清除告警", notes = "删除告警信息")
    public ResponseModel cleanEvent(@RequestParam() int id) {
        return new ResponseModel<>()
                .setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData(eventService.cleanEvent(id));
    }

}
