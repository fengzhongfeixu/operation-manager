package com.sugon.gsq.om.proxy.controller;

import com.alibaba.fastjson.JSON;
import com.sugon.gsq.om.common.utils.HttpUtil;
import com.sugon.gsq.om.constant.UrlMapping;
import com.sugon.gsq.om.model.ServerModel;
import com.sugon.gsq.om.proxy.service.HttpProxyService;
import com.sugon.gsq.om.proxy.utils.RestTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * ClassName: MasterController
 * Author: Administrator
 * Date: 2019/8/28 19:52
 */
@Controller
@RequestMapping("/")
public class ProxyController {

    @Autowired
    HttpProxyService httpProxyService;

    /*@RequestMapping(path = "/**", method = RequestMethod.GET)
    @ResponseBody
    public Object mirrorRestGET(HttpMethod method, HttpServletRequest request,
                                HttpServletResponse response) throws URISyntaxException, ServletException, IOException {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        RestTemplate restTemplate = null;
        restTemplate = RestTemplateFactory.Build("master", 9870);

        return doMirror(null, method, request, restTemplate);
    }*/

    private ServerModel getActiveHdfs() throws IOException {
        List<ServerModel> hdfsNodes = httpProxyService.getHDFSNodes();
        for(ServerModel hdfsNode : hdfsNodes){
            String object = HttpUtil.sendGet(String.format(UrlMapping.REQUEST_STATUS,
                    hdfsNode.getHostname(),hdfsNode.getPort()));
            String status = JSON.parseObject(object)
                    .getJSONArray("beans")
                    .getJSONObject(0)
                    .getString("State");
            if(status.equals("active")){
                return hdfsNode;
            }
        }
        return null;
    }

    private ResponseEntity<byte[]> doMirror(String body, HttpMethod method
            , HttpServletRequest request, RestTemplate restTemplate) throws URISyntaxException {
        URI uri = new URI("http"
                , null
                , request.getRequestURI()
                , request.getQueryString()
                , null);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                uri, method, body != null ? new HttpEntity<>(body) : null, byte[].class
        );
        return responseEntity;
    }

}
