package com.sugon.gsq.om.common.rpc.client;

import com.sugon.gsq.om.common.annotations.ServiceName;
import com.sugon.gsq.om.common.modes.RequestRpc;
import com.sugon.gsq.om.common.utils.StringUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyHelperTool {

    public volatile static ClientHelper client;

    public ProxyHelperTool() {
        client = ClientHelper.getClientHelper();
    }

    public <T> T create(final Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    //@Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getDeclaringClass().getAnnotation(ServiceName.class) == null) {
                            throw new RuntimeException("Annotation(ServiceName) is null.");
                        }
                        RequestRpc requestRpc = new RequestRpc();
                        requestRpc.setMethodName(method.getName());
                        requestRpc.setServiceName(method.getDeclaringClass().getAnnotation(ServiceName.class).name());
                        requestRpc.setParameters(args);
                        requestRpc.setParameterTypes(method.getParameterTypes());
                        requestRpc.setRequestId(StringUtil.getUiid());
                        ClientHandler.waitingRPC.put(requestRpc.getRequestId(),requestRpc);
                        ProxyHelperTool.client.send(requestRpc);
                        synchronized(requestRpc){
                            requestRpc.wait();
                        }
                        Object object = requestRpc.getResult();
                        ClientHandler.waitingRPC.remove(requestRpc.getRequestId());
                        return object;
                    }
                }
        );
    }
}
