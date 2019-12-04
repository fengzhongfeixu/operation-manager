package com.sugon.gsq.om.proxy.netty;

import com.google.gson.Gson;
import com.sugon.gsq.om.proxy.core.ProxyRequestClientChannelMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpsProxyClientChannelInboundHandle extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(HttpsProxyClientChannelInboundHandle.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof byte[]) {
                log.debug("https代理远程服务器返回数据bytes:{}", msg);
                ProxyRequestClientChannelMap.getRequestChannel(ctx.channel()).writeAndFlush(msg);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("远程服务关闭了连接;host={}", new Gson().toJson(ctx.channel().remoteAddress()));
        ProxyRequestClientChannelMap.closeByProxyClient(ctx.channel());
    }
}
