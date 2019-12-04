package com.sugon.gsq.om.common.modes;

public class RequestRpc {
    private String requestId = "1213";
    private String serviceName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object result;

    public String getRequestId() {
        return requestId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        synchronized(this){
            this.result = result;
            notify();
        }

    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    private Object[] parameters;

}
