package com.sugon.gsq.om.common.rpc.server;

import com.sugon.gsq.om.common.modes.RequestRpc;
import com.sugon.gsq.om.common.modes.ResponseRpc;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ServerService.submit(new Runnable() {
            @Override
            public void run() {
                RequestRpc requestRpc = (RequestRpc)msg;
                ResponseRpc responseRpc = handle(requestRpc);
                responseRpc.setRequestId(requestRpc.getRequestId());
                ctx.writeAndFlush(responseRpc).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        //System.out.println("Server operationComplete");
                    }
                });
            }
        });

        /*.addListener(ChannelFutureListener.CLOSE)*/
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/*       ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) //flush掉所有写回的数据
                .addListener(ChannelFutureListener.CLOSE); //当flush完成后关闭channel*/
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        //System.out.println("ChannelInboundHandlerAdapter-----channelWritabilityChanged");
        //ctx.fireChannelWritabilityChanged();
    }

    private ResponseRpc handle(RequestRpc requestRpc){
        ResponseRpc responseRpc = new ResponseRpc();
        Object object = ServerService.getService(requestRpc.getServiceName());
        if(object == null){
            responseRpc.setException(new RuntimeException("Not service:"+requestRpc.
                    getServiceName()));
            return responseRpc;
        }

        try {
            Class<?> serviceClass = object.getClass();
            Method method = serviceClass.getMethod(requestRpc.getMethodName(),
                    requestRpc.getParameterTypes());
            method.setAccessible(true);
            Object[] parameters = requestRpc.getParameters();
            responseRpc.setResult(method.invoke(object, parameters));
        } catch (Exception e){
            responseRpc.setResult(e);
        }
        return responseRpc;
    }


}
