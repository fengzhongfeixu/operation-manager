package com.sugon.gsq.om.common.service;

import com.sugon.gsq.om.common.annotations.ServiceName;

@ServiceName(name = "MsgService")
public interface MsgService {
    String send(String msg);
}
