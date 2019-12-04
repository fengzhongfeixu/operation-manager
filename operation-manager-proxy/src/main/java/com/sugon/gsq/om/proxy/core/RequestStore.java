package com.sugon.gsq.om.proxy.core;

import com.google.common.collect.Maps;
import com.sugon.gsq.om.proxy.entity.FullRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Map;

public class RequestStore {

    private final static Map<Channel, FullRequest> store = Maps.newConcurrentMap();

    public final static void save(Channel channel, HttpObject httpObject) {
        if (!store.containsKey(channel)) {
            FullRequest request = new FullRequest();
            store.put(channel, request);
        }
        if (httpObject instanceof HttpRequest) {
            store.get(channel).setRequest((HttpRequest) httpObject);
        }
        if (httpObject instanceof HttpContent) {
            FullRequest request = store.get(channel);
            if (CollectionUtils.isEmpty(request.getContents())) {
                request.setContents(new ArrayList<>());
            }
            request.getContents().add((HttpContent) httpObject);
        }
    }

    public final static DefaultFullHttpRequest getFull(Channel channel) {
        FullRequest request = store.get(channel);
        HttpRequest requestHeader = request.getRequest();
        ByteBuf byteBuf = Unpooled.buffer();
        if (CollectionUtils.isNotEmpty(request.getContents())) {
            request.getContents().forEach(i -> {
                if (i.content().readableBytes() > 0) {
                    byteBuf.writeBytes(i.content());
                }
                ReferenceCountUtil.release(i);
            });
        }
        DefaultFullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(requestHeader.protocolVersion(), requestHeader.method(), requestHeader.uri(), byteBuf, requestHeader.headers(), requestHeader.headers());
        store.remove(channel);
        //ReferenceCountUtil.release(msg);
        ReferenceCountUtil.release(request.getRequest());
        return fullHttpRequest;
    }

}
