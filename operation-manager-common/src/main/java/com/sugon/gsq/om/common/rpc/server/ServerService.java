package com.sugon.gsq.om.common.rpc.server;

import com.sugon.gsq.om.common.modes.Constants;
import com.sugon.gsq.om.common.modes.RequestRpc;
import com.sugon.gsq.om.common.protocols.RpcDecoder;
import com.sugon.gsq.om.common.protocols.RpcEncoder;
import com.sugon.gsq.om.common.service.MsgService;
import com.sugon.gsq.om.common.utils.GetServiceNameHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerService {

    private static ThreadPoolExecutor threadPoolExecutor;
    //map 本地的服务类
    private static Map<String, Object> handlerMap = new HashMap<>();

    public void start() {
        initMapServices();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new RpcDecoder(RequestRpc.class));
                    pipeline.addLast(new RpcEncoder());
                    pipeline.addLast(new ServerHandler());
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 启动 RPC 服务器
            ChannelFuture future = bootstrap.bind(Constants.HOST, Constants.PORT).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void submit(Runnable task) {
        if (threadPoolExecutor == null) {
            synchronized (ServerService.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(4, 4, 600L,
                            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
                }
            }
        }
        threadPoolExecutor.submit(task);
    }

    private void initMapServices() {
        MsgService msgService = new MsgServiceImpl();
        handlerMap.put(GetServiceNameHelper.getServiceName(msgService), msgService);
    }

    public static Object getService(String serviceName){
        return handlerMap.get(serviceName);
    }

}
