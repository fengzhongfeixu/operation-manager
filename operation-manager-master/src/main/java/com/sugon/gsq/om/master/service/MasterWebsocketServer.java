package com.sugon.gsq.om.master.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/*
 * ClassName: MasterWebsocketServer
 * Author: Administrator
 * Date: 2019/9/25 16:17
 */
@Service
public class MasterWebsocketServer {

    @Autowired
    private SimpMessagingTemplate wsTemplate;

    public void sendMessage(String endpoint, String message){
        wsTemplate.convertAndSend("/topic/" + endpoint, message);
    }

}
