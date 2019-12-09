package com.sugon.gsq.om.common.constant;

/*
 * ClassName: Orders
 * Author: Administrator
 * Date: 2019/9/11 13:37
 */
public class Orders {
    //主节点下发命令码表
    public static final String GET_CONFIGS = "gcf";
    public static final String START_ZOOKEEPER_SERVER = "szs";
    public static final String START_HDFS_JOURNALNODE = "shj";
    public static final String START_HDFS_SECONDARYNAMENODE = "shs";
    public static final String FORMAT_HDFS_NAMENODE = "fhn";
    public static final String FORMAT_HDFS_NAMENODE_ACTIVE = "fhna";
    public static final String FORMAT_HDFS_NAMENODE_STANDBY = "fhns";
    public static final String START_HDFS_DATANODE = "shd";
    public static final String START_MAPRED_HISTORY = "smh";
    public static final String START_YARN_RESOURCEMANAGER = "syr";
    public static final String START_YARN_NODEMANAGER = "syn";
    public static final String START_YARN_TIMELINESERVER = "syt";
    public static final String INIT_HIVE_METASTORE = "ihm";
    public static final String START_HIVE_METASTORE = "shm";
    public static final String START_HIVE_HIVESERVER2 = "shh";
    public static final String START_HBASE_MASTER = "shm01";
    public static final String START_HBASE_REGIONSERVER = "shr";
    public static final String START_KAFKA_BROKER = "skb";
}
