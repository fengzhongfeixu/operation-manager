package com.sugon.gsq.om.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/*
 * ClassName: BlueprintModel
 * Author: Administrator
 * Date: 2019/8/29 17:09
 */
@Data
@Accessors(chain = true)
public class BlueprintModel {
    //蓝图版本
    private String version;
    //节点私有配置
    private List<Process> hosts;
    //公共配置
    private List<Configuration> configurations;

    @Data
    @Accessors(chain = true)
    public static class Process{
        private String name;
        private List<String> components;
        private List<Configuration> configurations;
    }

    @Data
    @Accessors(chain = true)
    public static class Configuration{
        private String key;
        private Map<String,String> value;
    }
}