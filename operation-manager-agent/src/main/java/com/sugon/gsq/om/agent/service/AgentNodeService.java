package com.sugon.gsq.om.agent.service;

import com.sugon.gsq.om.db.mapper.OmNodeMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * ClassName: NodeService
 * Author: Administrator
 * Date: 2019/9/9 16:16
 */
@Service
public class AgentNodeService {

    @Autowired
    OmNodeMessageMapper omNodeMessageMapper;

}
