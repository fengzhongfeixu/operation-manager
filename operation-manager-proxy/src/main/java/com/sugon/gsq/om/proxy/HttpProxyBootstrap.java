package com.sugon.gsq.om.proxy;

import com.sugon.gsq.om.proxy.netty.ProxyChannelInboundHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HttpProxyBootstrap {

    private ChannelFuture future;

    public HttpProxyBootstrap(String hostname, Integer httpPort, Integer proxyPort) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast("request", new HttpRequestDecoder())
                                    .addLast("response", new HttpResponseEncoder())
                                    .addLast("handle", new ProxyChannelInboundHandle(hostname, httpPort));

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 启动代理服务
            future = b.bind(proxyPort).sync();
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void stopProxy(){
        if(future != null){
            future.channel();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread test = new Thread(){
            @Override
            public void run() {
                try {
                    new HttpProxyBootstrap("master",9870,2090);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        test.start();
    }

}
