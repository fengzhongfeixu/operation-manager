package com.sugon.gsq.om.common.utils;

import com.sugon.gsq.om.common.annotations.ServiceName;

public class GetServiceNameHelper {
    public static String getServiceName(Object object) {
        if (object == null) {
            throw new RuntimeException("getName: object is null");
        }
        ServiceName serviceName = object.getClass().getAnnotation(ServiceName.class);
        if (serviceName == null) {
            throw new RuntimeException("getName: serviceName is null, please add Annotation(@ServiceName)");
        }
        return serviceName.name();
    }
}
