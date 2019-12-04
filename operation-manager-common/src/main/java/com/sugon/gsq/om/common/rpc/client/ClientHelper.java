package com.sugon.gsq.om.common.rpc.client;

import com.sugon.gsq.om.common.modes.Constants;
import com.sugon.gsq.om.common.modes.RequestRpc;
import com.sugon.gsq.om.common.modes.ResponseRpc;
import com.sugon.gsq.om.common.protocols.RpcDecoder;
import com.sugon.gsq.om.common.protocols.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientHelper {
    private static ClientHelper clientHelper;
    private volatile boolean haveInit = false;
    private volatile EventLoopGroup group;
    private volatile Channel channel;
    private String host = Constants.HOST;
    private int port = Constants.PORT;
    private ClientHandler clientHandler = new ClientHandler();

    private ClientHelper() {
        this.initConnect();
    }

    public static ClientHelper getClientHelper() {
        if (clientHelper == null) {
            clientHelper = new ClientHelper();
        }
        return clientHelper;
    }

    public boolean send(RequestRpc requestRpc) {
        boolean result = false;
        try {
            //阻塞发送
            channel.writeAndFlush(requestRpc).sync();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void initConnect() {
        if (host == null || port == -1) {
            throw new RuntimeException("host or port can't  null");
        }
        group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new RpcEncoder());
                    pipeline.addLast(new RpcDecoder(ResponseRpc.class));
                    pipeline.addLast(clientHandler);
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            ChannelFuture future = bootstrap.connect(host, port).sync();
            channel = future.channel();
            haveInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //group.shutdownGracefully();
        }
    }

    public void close() {
        if(group!=null){
            group.shutdownGracefully();
        }
    }
}
