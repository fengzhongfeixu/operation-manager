package com.sugon.gsq.om.agent.service;

import com.alibaba.fastjson.JSONArray;
import com.sugon.gsq.om.common.utils.*;
import com.sugon.gsq.om.db.entity.OmBlueprintConf;
import com.sugon.gsq.om.constant.Constant;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/*
 * ClassName: AgentService
 * Author: Administrator
 * Date: 2019/9/11 13:23
 */
@Service
public class AgentService {

    public void updateXmlByConfigs(List<OmBlueprintConf> configs) throws IOException {
        Map<String,String> zoo = new HashMap<>();
        Map<String,String> core = new HashMap<>();
        Map<String,String> hdfs = new HashMap<>();
        Map<String,String> yarn = new HashMap<>();
        Map<String,String> mapred = new HashMap<>();
        Map<String,String> hive = new HashMap<>();
        Map<String,String> spark_hive = new HashMap<>();
        Map<String,String> hbase = new HashMap<>();
        Map<String,String> kafka = new HashMap<>();
        for(OmBlueprintConf config : configs){
            if(config.getBelong().equals("zoo")){
                zoo.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("core-site")){
                core.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("hdfs-site")){
                hdfs.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("yarn-site")){
                yarn.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("mapred-site")){
                mapred.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("hive-site")){
                hive.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("spark-hive-site")){
                spark_hive.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("hbase-site")){
                hbase.put(config.getK(),config.getV());
            } else if(config.getBelong().equals("kafka-server")){
                kafka.put(config.getK(),config.getV());
            }
        }
        CfgUtil.updateCfg(zoo,
                Constant.ZOOKEEPER_HOME + File.separator + "conf" + File.separator + "zoo.cfg");
        XmlUtil.updateXml(core,
                Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "core-site.xml");
        XmlUtil.updateXml(hdfs,
                Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "hdfs-site.xml");
        XmlUtil.updateXml(yarn,
                Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "yarn-site.xml");
        XmlUtil.updateXml(mapred,
                Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "mapred-site.xml");
        XmlUtil.updateXml(hive,
                Constant.HIVE_HOME + File.separator + "conf" + File.separator + "hive-site.xml");
        XmlUtil.updateXml(spark_hive,
                Constant.SPARK_HOME+ File.separator + "conf" + File.separator + "hive-site.xml");
        //复制spark所需的hadoop配置文件
        CommonUtil.copyFile(Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "core-site.xml"
                ,Constant.SPARK_HOME + File.separator + "conf" + File.separator + "core-site.xml");
        CommonUtil.copyFile(Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "hdfs-site.xml"
                ,Constant.SPARK_HOME + File.separator + "conf" + File.separator + "hdfs-site.xml");
        XmlUtil.updateXml(hbase,
                Constant.HBASE_HOME + File.separator + "conf" + File.separator + "hbase-site.xml");
        //复制hbase所需的hadoop配置文件
        CommonUtil.copyFile(Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "core-site.xml"
                ,Constant.HBASE_HOME + File.separator + "conf" + File.separator + "core-site.xml");
        CommonUtil.copyFile(Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "hdfs-site.xml"
                ,Constant.HBASE_HOME + File.separator + "conf" + File.separator + "hdfs-site.xml");
        //配置kafka
        CfgUtil.updateCfg(kafka,
                Constant.KAFKA_HOME + File.separator + "config" + File.separator + "server.properties");
    }

    public void initZookeeper() throws IOException, InterruptedException {
        CustomProperties props = new CustomProperties();
        InputStream in = new BufferedInputStream(
                new FileInputStream(Constant.ZOOKEEPER_HOME
                        + File.separator + "conf" + File.separator + "zoo.cfg"));
        props.load(in);
        String dataDir = (String)props.get("dataDir");
        String dataLogDir = (String)props.get("dataLogDir");
        String serverId = "";
        Iterator it = props.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry=(Map.Entry)it.next();
            String value = (String)entry.getValue();
            if(value.split(":")[0].equals(Constant.LOCALHOST)){
                serverId = ((String)entry.getKey()).split("\\.")[1];
                break;
            }
        }
        List<String> commands = new ArrayList<>();
        commands.add("mkdir -p " + dataDir);
        commands.add("mkdir -p " + dataLogDir);
        commands.add("echo " + serverId +" > " + dataDir + File.separator + "myid");
        ExecuteShell.executeCommand(commands.toArray(new String[commands.size()]));
    }

    public void initHDFS(List<String> datanodes) throws IOException, InterruptedException {
        String command = "echo -e '" ;
        for(int i=0;i<datanodes.size();i++){
            if(i==datanodes.size()-1){
                command += datanodes.get(i) + "' ";
            } else {
                command += datanodes.get(i) + "\n";
            }
        }
        command += "> " + Constant.HADOOP_HOME + File.separator + "etc" + File.separator + "hadoop" + File.separator + "workers";
        ExecuteShell.executeCommand(command);
    }

    public void initHbase(List<String> regionserverNodes, List<String> backupMasterNodes) throws IOException, InterruptedException {
        // 从节点列表
        String command = "echo -e '" ;
        for(int i=0;i<regionserverNodes.size();i++){
            if(i==regionserverNodes.size()-1){
                command += regionserverNodes.get(i) + "' ";
            } else {
                command += regionserverNodes.get(i) + "\n";
            }
        }
        command += "> " + Constant.HBASE_HOME + File.separator + "conf" + File.separator + "regionservers";
        ExecuteShell.executeCommand(command);
        // 备用节点列表
        String command_ = "echo -e '" ;
        for(int i=0;i<backupMasterNodes.size();i++){
            if(i==backupMasterNodes.size()-1){
                command_ += backupMasterNodes.get(i) + "' ";
            } else {
                command_ += backupMasterNodes.get(i) + "\n";
            }
        }
        command_ += "> " + Constant.HBASE_HOME + File.separator + "conf" + File.separator + "backup-masters";
        ExecuteShell.executeCommand(command_);
    }

    public String startZookeeper() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand(Constant.ZOOKEEPER_HOME
                + File.separator + "bin" + File.separator + "zkServer.sh start");
    }

    public String startJournalnode() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "hdfs --daemon start journalnode");
    }

    public String formatNamenode() throws IOException, InterruptedException {
        String format = ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "hdfs namenode -format");
        String launcher = ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "hdfs --daemon start namenode");
        return format + "\r\n--------------------------\r\n" + launcher;
    }

    public String startSecondarynamenode() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "hdfs --daemon start secondarynamenode");
    }

    public String formatActiveNamenode() throws IOException, InterruptedException {
        String formatZk = ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "hdfs zkfc -formatZK");
        String formatNN = ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "hdfs namenode -format");
        String startNN = ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "hdfs --daemon start namenode");
        String startZkfc = ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "hdfs --daemon start zkfc");
        return formatZk + "\r\n--------------------------\r\n" + formatNN
                + "\r\n--------------------------\r\n" + startNN
                + "\r\n--------------------------\r\n" + startZkfc;
    }

    public String formatStandbyNamenode() throws IOException, InterruptedException {
        String formatStandby = ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "hdfs namenode -bootstrapStandby");
        String startStandby = ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "hdfs --daemon start namenode");
        String startZkfc = ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "hdfs --daemon start zkfc");
        return formatStandby + "\r\n--------------------------\r\n" + startStandby
                + "\r\n--------------------------\r\n" + startZkfc;
    }

    public String startDatanodes() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "hdfs --daemon start datanode");
    }

    public String startHistoryserver() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "mapred --daemon start historyserver");
    }

    public String startResourcemanager() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "yarn --daemon start resourcemanager");
    }

    public String startNodemanager() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "yarn --daemon start nodemanager");
    }

    public String startTimelineserver() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand(Constant.HADOOP_HOME
                + File.separator + "bin" + File.separator + "yarn --daemon start timelineserver");
    }

    public String initHiveMetastore() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand(Constant.HIVE_HOME
                + File.separator + "bin" + File.separator + "schematool -dbType mysql -initSchema");
    }

    public String startHiveMetastore() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand( "nohup " + Constant.HIVE_HOME
                + File.separator + "bin" + File.separator + "hive --service metastore > "
                + Constant.HIVE_HOME + File.separator + "logs" + File.separator + "metastore.log 2>&1" + " &");
    }

    public String startHiveServer2() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand( "nohup " + Constant.HIVE_HOME
                + File.separator + "bin" + File.separator + "hive --service hiveserver2 > "
                + Constant.HIVE_HOME + File.separator + "logs" + File.separator + "hiveserver2.log 2>&1" + " &");
    }

    public String startHbaseMaster() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand( Constant.HBASE_HOME
                + File.separator + "bin" + File.separator + "hbase-daemon.sh start master");
    }

    public String startRegionserver() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand( Constant.HBASE_HOME
                + File.separator + "bin" + File.separator + "hbase-daemon.sh start regionserver");
    }

    public String startBroker() throws IOException, InterruptedException {
        return ExecuteShell.executeCommand( "nohup " + Constant.KAFKA_HOME
                + File.separator + "bin" + File.separator + "kafka-server-start.sh "
                + Constant.KAFKA_HOME + File.separator + "config" + File.separator + "server.properties &");
    }

}
