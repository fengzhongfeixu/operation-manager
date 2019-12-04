package com.sugon.gsq.om.common.protocols;

import com.sugon.gsq.om.common.modes.Constants;
import com.sugon.gsq.om.common.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    private boolean hadReadHeard = false;
    private byte[] dataHeardBuffer = new byte[4];
    private Class<?> requestResponseRpc;

    public RpcDecoder(Class<?> requestResponseRpc) {
        this.requestResponseRpc = requestResponseRpc;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //hadReadHeard避免多次判断头信息
        if (!hadReadHeard) {
            while (true) {
                //这里保证至少读到一个头信息，也可以读到一个头和数据长度在做处理
                if (in.readableBytes() < 4) {
                    return;
                }
                in.markReaderIndex();
                in.readBytes(dataHeardBuffer);
                //System.out.println(Constants.SERVIE_HEARD.getBytes().length);
                String s = new String(dataHeardBuffer);
                if (s.equals(Constants.SERVIE_HEARD)) {
                    //System.out.println(Constants.SERVIE_HEARD);
                    hadReadHeard = true;
                    break;
                } else {
                    in.resetReaderIndex();
                    in.readByte();
                }
            }
        }

        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        hadReadHeard = false;
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        out.add(SerializationUtil.deserialize(data, requestResponseRpc));
    }
}
