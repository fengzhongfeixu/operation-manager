package com.sugon.gsq.om.common.constant;

/*
 * ClassName: Constant
 * Author: Administrator
 * Date: 2019/8/28 19:22
 */
public final class Constant {
    private Constant(){}
    //当前机器hostname
    public static String LOCALHOST;
    //当前节点身份
    public static String ROLE;
    //其它属性
    public static final String MASTER = "master";
    public static final String AGENT = "agent";
    public static final String ZOOKEEPER_HOME = "/opt/hadoop/zookeeper-3.4.6";
    public static final String HADOOP_HOME = "/opt/hadoop/hadoop-3.1.1";
    public static final String HIVE_HOME = "/opt/hadoop/hive-3.1.0";
    public static final String SPARK_HOME = "/opt/hadoop/spark-2.3.1";
    public static final String HBASE_HOME = "/opt/hadoop/hbase-2.1.7";
    public static final String KAFKA_HOME = "/opt/hadoop/kafka-1.1.1";
    //websocket推送信息的端点
    public static final String OM_INSTALL_VIEW = "stepShow";
    //进程名称
    public static final String ZOOKEEPER_SERVER_PROCESS = "ZOOKEEPER_SERVER";
    public static final String HDFS_NAMENODE_PROCESS = "HDFS_NAMENODE";
    public static final String HDFS_SECONDARYNAMENODE_PROCESS = "HDFS_SECONDARYNAMENODE";
    public static final String HDFS_DATANODE_PROCESS = "HDFS_DATANODE";
    public static final String HDFS_JOURNALNODE_PROCESS = "HDFS_JOURNALNODE";
    public static final String MAPRED_HISTORYSERVER_PROCESS = "MAPRED_HISTORYSERVER";
    public static final String YARN_RESOURCEMANAGER_PROCESS = "YARN_RESOURCEMANAGER";
    public static final String YARN_NODEMANAGER_PROCESS = "YARN_NODEMANAGER";
    public static final String YARN_TIMELINESERVER_PROCESS = "YARN_TIMELINESERVER";
    public static final String HIVE_METASTORE_PROCESS = "HIVE_METASTORE";
    public static final String HIVE_HIVESERVER2_PROCESS = "HIVE_HIVESERVER2";
    public static final String HBASE_MASTER_PROCESS = "HBASE_MASTER";
    public static final String HBASE_REGIONSERVER_PROCESS = "HBASE_REGIONSERVER";
    public static final String KAFKA_BROKER_PROCESS = "KAFKA_BROKER";
    //任务相关
    public static final Integer HTTP_STATUS_SUCCESS = 200;  //http请求成功
    public static final Integer HTTP_STATUS_PARAM_ERROR = 400;  //http请求成功
    public static final Integer HTTP_STATUS_ERROR = 500;    //http请求内部错误
    public static final Integer HTTP_STATUS_NOUSER = 410;   //用户或密码错误
    public static final Integer HTTP_STATUS_NOTLOGIN = 10000;   //没有登陆
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAULT = "fault";
    //消息
    public static final String HTTP_MESSAGE_ERROR = "用户名或密码错误";
    public static final String HTTP_MESSAGE_SUCCESS = "登录成功";
    public static final String HTTP_MESSAGE_GETSUCCESS = "获取成功";
    public static final String HTTP_MESSAGE_PARAM_ERROR = "参数错误";
    public static final String HTTP_MESSAGE_CANCELLATIONSUCCESS = "注销成功";
    public static final String BLUEPRINT_SUBMIT_SUCCESS = "蓝图提交成功，请及时查看运行日志";
    public static final String BLUEPRINT_SUBMIT_FAULT = "蓝图提交失败，请检查蓝图配置信息";
    //组件启动状态
    public static final String PROCESS_STATUS_START = "已启动";
    public static final String PROCESS_STATUS_STOP = "已停止";
    //组件健康状态
    public static final String PROCESS_HEALTH_GOOD = "良好";
    public static final String PROCESS_HEALTH_WARN = "警告";
    public static final String PROCESS_HEALTH_ERROR = "宕机";
    //组件配置项是否同步
    public static final String PROCESS_CONFIG_YES = "已同步";
    public static final String PROCESS_CONFIG_NO = "过期";
    //监控指标映射
    public static final String MONITOR_NETWORK_READ = "monitor_network_read";
    public static final String MONITOR_NETWORK_WRITE = "monitor_network_write";
    public static final String MONITOR_DISK_USAGE = "monitor_disk_usage";
    public static final String MONITOR_DISK_USED = "monitor_disk_used";
    public static final String MONITOR_MEMORY_USAGE = "monitor_memory_usage";
    public static final String MONITOR_MEMORY_USED = "monitor_memory_used";
    public static final String MONITOR_CPU_USAGE = "monitor_cpu_usage";
}
