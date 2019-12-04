package com.sugon.gsq.om.common.rpc.server;

import com.sugon.gsq.om.common.annotations.ServiceName;
import com.sugon.gsq.om.common.service.MsgService;

@ServiceName(name = "MsgService")
public class MsgServiceImpl implements MsgService {

    public String send(String msg) {
        System.out.println("Server get msg of client: " + msg);
        return "Hello, I'm server:"+msg;
    }

}
