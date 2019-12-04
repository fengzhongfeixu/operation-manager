package com.sugon.gsq.om.proxy.netty;

import com.google.gson.Gson;
import com.sugon.gsq.om.proxy.core.ProxyRequestClientChannelMap;
import com.sugon.gsq.om.proxy.core.ResponseStore;
import com.sugon.gsq.om.proxy.entity.HostInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxyClientChannelInboundHandle extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(HttpProxyClientChannelInboundHandle.class);

    public final static AttributeKey<HostInfo> httpsRequestUrl = AttributeKey.newInstance("httpsRequestUrl");

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("远程服务关闭了连接;host={}", new Gson().toJson(ctx.channel().remoteAddress()));
        ProxyRequestClientChannelMap.closeByProxyClient(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof HttpObject) {
                log.debug("http代理远程服务器返回数据:{}", msg);
                ResponseStore.save(ctx.channel(), (HttpObject) msg);
                if (msg instanceof LastHttpContent) {
                    ProxyRequestClientChannelMap.getRequestChannel(ctx.channel()).writeAndFlush(ResponseStore.getFull(ctx.channel()));
                }

            }
        } finally {
            //ReferenceCountUtil.release(msg);
            // 不能在这里release掉 需要到getFull的地方释放
        }
    }

}
