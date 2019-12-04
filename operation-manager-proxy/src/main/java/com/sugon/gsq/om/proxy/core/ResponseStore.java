package com.sugon.gsq.om.proxy.core;

import com.google.common.collect.Maps;
import com.sugon.gsq.om.proxy.entity.FullResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Map;

public class ResponseStore {

    private final static Map<Channel, FullResponse> store = Maps.newConcurrentMap();

    public static void save(Channel channel, HttpObject httpObject) {
        if (!store.containsKey(channel)) {
            FullResponse response = new FullResponse();
            store.put(channel, response);
        }
        if (httpObject instanceof HttpResponse) {
            store.get(channel).setResponse((HttpResponse) httpObject);
        }
        if (httpObject instanceof HttpContent) {
            FullResponse response = store.get(channel);
            if (CollectionUtils.isEmpty(response.getContents())) {
                response.setContents(new ArrayList<>());
            }
            response.getContents().add((HttpContent) httpObject);
        }
    }

    public static DefaultFullHttpResponse getFull(Channel channel) {
        FullResponse request = store.get(channel);
        HttpResponse responseHeader = request.getResponse();
        ByteBuf byteBuf = Unpooled.buffer();
        if (CollectionUtils.isNotEmpty(request.getContents())) {
            request.getContents().forEach(i -> {
                if (i.content().readableBytes() > 0) {
                    byteBuf.writeBytes(i.content());
                }
                ReferenceCountUtil.release(i);
            });
        }
        DefaultFullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(responseHeader.protocolVersion(), responseHeader.status(), byteBuf, responseHeader.headers(), responseHeader.headers());
        store.remove(channel);
        //ReferenceCountUtil.release(msg);
        ReferenceCountUtil.release(request.getResponse());
        return fullHttpResponse;
    }

}
