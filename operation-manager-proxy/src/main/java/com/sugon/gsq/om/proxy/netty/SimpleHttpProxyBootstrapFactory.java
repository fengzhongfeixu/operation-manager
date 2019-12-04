package com.sugon.gsq.om.proxy.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleHttpProxyBootstrapFactory {

    private final static Logger log = LoggerFactory.getLogger(SimpleHttpProxyBootstrapFactory.class);

    private final static EventLoopGroup workerGroup = new NioEventLoopGroup();

    public static Bootstrap httpsBootstrap() {
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new ByteArrayEncoder())
                        .addLast(new ByteArrayDecoder())
                        .addLast(new HttpsProxyClientChannelInboundHandle());
            }
        });
        return b;
    }

    public static Bootstrap httpBootstrap() {
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                ch.pipeline().addLast("response",new HttpResponseDecoder());
                // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                ch.pipeline().addLast("request",new HttpRequestEncoder());
                ch.pipeline().addLast("handle",new HttpProxyClientChannelInboundHandle());
            }
        });
        return b;
    }

}
