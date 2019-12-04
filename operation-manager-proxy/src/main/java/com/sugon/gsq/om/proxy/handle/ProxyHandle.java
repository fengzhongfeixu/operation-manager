package com.sugon.gsq.om.proxy.handle;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

public interface ProxyHandle {

    void handle(Channel proxyRequestChannel, HttpRequest request);

}
