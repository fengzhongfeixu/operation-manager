package com.sugon.gsq.om.proxy.entity;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;

import java.util.List;

public class FullResponse {

    private HttpResponse response;

    private List<HttpContent> contents;

    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public List<HttpContent> getContents() {
        return contents;
    }

    public void setContents(List<HttpContent> contents) {
        this.contents = contents;
    }
}
