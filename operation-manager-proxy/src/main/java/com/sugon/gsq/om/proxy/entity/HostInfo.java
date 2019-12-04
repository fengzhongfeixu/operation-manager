package com.sugon.gsq.om.proxy.entity;

public class HostInfo {

    private String host;

    private Integer port;

    private String ip;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "HostInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", ip='" + ip + '\'' +
                '}';
    }
}
