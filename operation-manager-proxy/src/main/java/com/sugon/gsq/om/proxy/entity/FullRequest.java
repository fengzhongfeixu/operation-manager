package com.sugon.gsq.om.proxy.entity;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;

import java.util.List;

public class FullRequest {

    private HttpRequest request;

    private List<HttpContent> contents;

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public List<HttpContent> getContents() {
        return contents;
    }

    public void setContents(List<HttpContent> contents) {
        this.contents = contents;
    }
}
