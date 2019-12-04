package com.sugon.gsq.om.controller;

import com.sugon.gsq.om.constant.Constant;
import com.sugon.gsq.om.model.ResponseModel;
import com.sugon.gsq.om.services.EventService;
import com.sugon.gsq.om.services.NodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

/*
 * ClassName: ServicesController
 * Author: Administrator
 * Date: 2019/8/28 19:52
 */
@Slf4j
@RestController
@RequestMapping("/node")
@Api(tags = "RELEASE-9.0.0", description = "节点管理", value = "节点管理")
public class NodeController {

    private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<>();

    @Autowired
    private NodeService nodeService;

    @GetMapping(path = "/node-list")
    @ApiOperation(value = "节点列表", notes = "展示节点的基本信息")
    public ResponseModel eventList(@RequestParam() int pageNum, @RequestParam() int pageSize) {
        return new ResponseModel<>()
                .setCode(Constant.HTTP_STATUS_SUCCESS)
                .setMessage(Constant.HTTP_MESSAGE_GETSUCCESS)
                .setData(nodeService.getNodeList(pageNum,pageSize));
    }

}
