package com.sugon.gsq.om.tools;

import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import com.sugon.gsq.om.constant.Constant;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.io.*;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/*
 * ClassName: ApiUtil
 * Author: Administrator
 * Date: 2019/11/15 14:35
 */
public class ApiUtil {

    private static SystemInfo systemInfo = new SystemInfo();

    private static final long BCG = 1024*1024*1024;  //B转G

    private static final long BCM = 1024*1024;   //B转M

    private static final long BCK = 1024;   //B转KB

    private ApiUtil(){
        // 获取主机信息
        SystemInfo systemInfo = new SystemInfo();
        // 获取操作系统信息
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        operatingSystem.getNetworkParams().getHostName();
        operatingSystem.getFamily();
        operatingSystem.getVersion().getVersion();
        operatingSystem.getVersion().getBuildNumber();
        operatingSystem.getBitness();
        operatingSystem.getProcessCount();
        operatingSystem.getThreadCount();
    }

    /**
     * 获取所有网络接口的读取速率
     * @return
     */
    public static String networkRead(){
        NetworkIF[] networkIFs = systemInfo.getHardware().getNetworkIFs();
        double totalSpeed = 0;
        double readSpeed = 0;
        double writeSpeed = 0;
        for(NetworkIF net : networkIFs){
            totalSpeed += net.getSpeed();
            writeSpeed += net.getBytesSent();
            readSpeed += net.getBytesRecv();
        }
        double readUsage = readSpeed / (writeSpeed + readSpeed);
        Double result = totalSpeed * readUsage;
        return FormatUtil.formatBytes(result.longValue())+"/s";
    }

    /**
     * 获取所有网络接口的写入速率
     * @return
     */
    public static String networkWrite(){
        NetworkIF[] networkIFs = systemInfo.getHardware().getNetworkIFs();
        double totalSpeed = 0;
        double readSpeed = 0;
        double writeSpeed = 0;
        for(NetworkIF net : networkIFs){
            totalSpeed += net.getSpeed();
            writeSpeed += net.getBytesSent();
            readSpeed += net.getBytesRecv();
        }
        double writeUsage = writeSpeed / (writeSpeed + readSpeed);
        Double result = totalSpeed * writeUsage;
        return FormatUtil.formatBytes(result.longValue())+"/s";
    }

    /**
     * 获取磁盘使用量与总量的百分比
     * @return
     */
    public static String diskUsage(){
        double result = 0;
        FileSystem fileSystem = systemInfo.getOperatingSystem().getFileSystem();
        OSFileStore[] fsArray = fileSystem.getFileStores();
        long used = 0,total = 0;
        for (OSFileStore fs : fsArray) {
            long freeTemp = fs.getUsableSpace();
            long totalTemp = fs.getTotalSpace();
            long usedTemp = totalTemp - freeTemp;
            used += usedTemp;
            total += totalTemp;
        }
        if(total != 0 && used != 0){
            result = NumberUtil.div(used, total, 4);
        }
        return new BigDecimal(result * Double.parseDouble("100"))
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue() + " %";
    }

    /**
     * 获取单位为GB的已使用磁盘容量
     * @return
     */
    public static String diskUsed(){
        FileSystem fileSystem = systemInfo.getOperatingSystem().getFileSystem();
        OSFileStore[] fsArray = fileSystem.getFileStores();
        long used = 0;
        for (OSFileStore fs : fsArray) {
            long freeTemp = fs.getUsableSpace();
            long totalTemp = fs.getTotalSpace();
            long usedTemp = totalTemp - freeTemp;
            used += usedTemp;
        }
        return FormatUtil.formatBytes(used);
    }

    /**
     * 获取内存使用量与总量的百分比
     * @return
     */
    public static String memoryUsage(){
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        GlobalMemory memory = hal.getMemory();
        long available = memory.getAvailable();
        long total = memory.getTotal();
        double used = NumberUtil.div(total - available, total, 4);
        return new BigDecimal(used * Double.parseDouble("100"))
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue() + " %";
    }

    /**
     * 获取单位为GB的已使用内存容量
     * @return
     */
    public static String memoryUsed(){
        //内存使用大小
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        GlobalMemory memory = hal.getMemory();
        long available = memory.getAvailable();
        long total = memory.getTotal();
        return FormatUtil.formatBytes(total - available);
    }

    /**
     * 获取cpu此时的使用率
     * @return
     */
    public static String cpuUsage(){
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();
        double useRate = processor.getSystemCpuLoadBetweenTicks();
        double result = NumberUtil.round(useRate, 4).doubleValue();
        return new BigDecimal(result * Double.parseDouble("100"))
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue() + " %";
    }

    /**
     * 判断本机是不是windows系统
     * @return
     */
    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    /**
     * 获取uuid
     * @return
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * 进程相关命令调用
     * @param order
     * @param process
     */
    public static void processCommand(String order,String process,String hivePid) throws IOException, InterruptedException {
        if(order.equals("restart")){
            if(process.equals("ZKserver")){
                executeCommand(Constant.ZOOKEEPER_HOME + File.separator + "bin" + File.separator + "zkServer.sh restart");
            } else if (process.equals("journalnode")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon stop journalnode");
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon start journalnode");
            } else if (process.equals("namenode")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon stop namenode");
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon start namenode");
            } else if (process.equals("zkfc")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon stop zkfc");
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon start zkfc");
            } else if (process.equals("secondaryNamenode")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon stop secondarynamenode");
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon start secondarynamenode");
            } else if (process.equals("datanode")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon stop datanode");
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon start datanode");
            } else if (process.equals("jobHistoryServer")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "mapred --daemon stop historyserver");
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "mapred --daemon start historyserver");
            } else if (process.equals("resourceManager")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "yarn --daemon stop resourcemanager");
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "yarn --daemon start resourcemanager");
            } else if (process.equals("nodeManager")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "yarn --daemon stop nodemanager");
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "yarn --daemon start nodemanager");
            } else if (process.equals("timelineserver")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "yarn --daemon stop timelineserver");
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "yarn --daemon start timelineserver");
            } else if (process.equals("metastore")){
                executeCommand("kill -9 " + hivePid);
                executeCommand("nohup " + Constant.HIVE_HOME
                        + File.separator + "bin" + File.separator + "hive --service metastore > "
                        + Constant.HIVE_HOME + File.separator + "logs" + File.separator + "metastore.log 2>&1" + " &");
            } else if (process.equals("hiveserver2")){
                executeCommand("kill -9 " + hivePid);
                executeCommand("nohup " + Constant.HIVE_HOME
                        + File.separator + "bin" + File.separator + "hive --service hiveserver2 > "
                        + Constant.HIVE_HOME + File.separator + "logs" + File.separator + "hiveserver2.log 2>&1" + " &");
            } else if (process.equals("HMaster")){
                executeCommand( Constant.HBASE_HOME + File.separator + "bin" + File.separator + "hbase-daemon.sh stop master");
                executeCommand(Constant.HBASE_HOME + File.separator + "bin" + File.separator + "hbase-daemon.sh start master");
            } else if (process.equals("HRegionServer")){
                executeCommand( Constant.HBASE_HOME + File.separator + "bin" + File.separator + "hbase-daemon.sh stop regionserver");
                executeCommand(Constant.HBASE_HOME + File.separator + "bin" + File.separator + "hbase-daemon.sh start regionserver");
            } else if (process.equals("broker")){
                executeCommand( "nohup " + Constant.KAFKA_HOME
                        + File.separator + "bin" + File.separator + "kafka-server-stop.sh "
                        + Constant.KAFKA_HOME + File.separator + "config" + File.separator + "server.properties &");
                executeCommand("nohup " + Constant.KAFKA_HOME
                        + File.separator + "bin" + File.separator + "kafka-server-start.sh "
                        + Constant.KAFKA_HOME + File.separator + "config" + File.separator + "server.properties &");
            }
        } else if (order.equals("start")){
            if(process.equals("ZKserver")){
                executeCommand(Constant.ZOOKEEPER_HOME + File.separator + "bin" + File.separator + "zkServer.sh start");
            } else if (process.equals("journalnode")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon start journalnode");
            } else if (process.equals("namenode")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon start namenode");
            } else if (process.equals("zkfc")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon start zkfc");
            } else if (process.equals("secondaryNamenode")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon start secondarynamenode");
            } else if (process.equals("datanode")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon start datanode");
            } else if (process.equals("jobHistoryServer")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "mapred --daemon start historyserver");
            } else if (process.equals("resourceManager")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "yarn --daemon start resourcemanager");
            } else if (process.equals("nodeManager")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "yarn --daemon start nodemanager");
            } else if (process.equals("timelineserver")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "yarn --daemon start timelineserver");
            } else if (process.equals("metastore")){
                executeCommand("nohup " + Constant.HIVE_HOME
                        + File.separator + "bin" + File.separator + "hive --service metastore > "
                        + Constant.HIVE_HOME + File.separator + "logs" + File.separator + "metastore.log 2>&1" + " &");
            } else if (process.equals("hiveserver2")){
                executeCommand("nohup " + Constant.HIVE_HOME
                        + File.separator + "bin" + File.separator + "hive --service hiveserver2 > "
                        + Constant.HIVE_HOME + File.separator + "logs" + File.separator + "hiveserver2.log 2>&1" + " &");
            } else if (process.equals("HMaster")){
                executeCommand(Constant.HBASE_HOME + File.separator + "bin" + File.separator + "hbase-daemon.sh start master");
            } else if (process.equals("HRegionServer")){
                executeCommand(Constant.HBASE_HOME + File.separator + "bin" + File.separator + "hbase-daemon.sh start regionserver");
            } else if (process.equals("broker")){
                executeCommand("nohup " + Constant.KAFKA_HOME
                        + File.separator + "bin" + File.separator + "kafka-server-start.sh "
                        + Constant.KAFKA_HOME + File.separator + "config" + File.separator + "server.properties &");
            }
        } else if (order.equals("stop")){
            if(process.equals("ZKserver")){
                executeCommand(Constant.ZOOKEEPER_HOME + File.separator + "bin" + File.separator + "zkServer.sh stop");
            } else if (process.equals("journalnode")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon stop journalnode");
            } else if (process.equals("namenode")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon stop namenode");
            } else if (process.equals("zkfc")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon stop zkfc");
            } else if (process.equals("secondaryNamenode")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon stop secondarynamenode");
            } else if (process.equals("datanode")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "hdfs --daemon stop datanode");
            } else if (process.equals("jobHistoryServer")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "mapred --daemon stop historyserver");
            } else if (process.equals("resourceManager")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "yarn --daemon stop resourcemanager");
            } else if (process.equals("nodeManager")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "yarn --daemon stop nodemanager");
            } else if (process.equals("timelineserver")){
                executeCommand(Constant.HADOOP_HOME + File.separator + "bin" + File.separator + "yarn --daemon stop timelineserver");
            } else if (process.equals("metastore")){
                executeCommand("kill -9 " + hivePid);
            } else if (process.equals("hiveserver2")){
                executeCommand("kill -9 " + hivePid);
            } else if (process.equals("HMaster")){
                executeCommand( Constant.HBASE_HOME + File.separator + "bin" + File.separator + "hbase-daemon.sh stop master");
            } else if (process.equals("HRegionServer")){
                executeCommand( Constant.HBASE_HOME + File.separator + "bin" + File.separator + "hbase-daemon.sh stop regionserver");
            } else if (process.equals("broker")){
                executeCommand( "nohup " + Constant.KAFKA_HOME
                        + File.separator + "bin" + File.separator + "kafka-server-stop.sh "
                        + Constant.KAFKA_HOME + File.separator + "config" + File.separator + "server.properties &");
            }
        }
    }

    /**
     * 根据jps中的应用名获取pid
     * @param processName
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static String getProcessInfoByName(String processName) throws IOException, InterruptedException {
        String pid = null;
        String jpsResult = executeCommand("jps");
        String[] processes = jpsResult.split("\n");
        //hive的俩个该死进程jps里全是RanJar
        if(processName.toUpperCase().equals("HIVEMETASTORE")
                || processName.toUpperCase().equals("HIVESERVER2")){
            for(String process : processes){
                String pid_ = process.split(" ")[0];
                String message = executeCommand("ps -ef | grep " + pid_);
                if(message.contains(processName)){
                    pid =  pid_;
                    break;
                }
            }
        } else {
            for(String process : processes){
                String pid_ = process.split(" ")[0];
                String pnames = process.split(" ")[1];
                String pname = pnames.substring(0,pnames.length()-1);
                if(pname.toUpperCase().equals(processName.toUpperCase())){
                    pid =  pid_;
                    break;
                }
            }
        }
        return pid;
    }

    /**
     * 输入运行命令获取Linux系统返回值
     * @param commands
     * @return
     */
    private static String executeCommand(String... commands)
            throws InterruptedException, IOException {
        StringBuffer rspList = new StringBuffer();
        Runtime run = Runtime.getRuntime();
        Process proc = run.exec("/bin/bash", null, null);
        BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
        for (String line : commands) {
            out.println(line);
        }
        out.println("exit");
        String rspLine = "";
        while ((rspLine = in.readLine()) != null) {
            rspList.append(rspLine)
                    .append("\r\n");
        }
        if(!rspList.toString().equals("")){
            rspList.deleteCharAt(rspList.toString().length() - 1);
        } else {
            rspList.append("The process does not have specific operational information output !");
        }
        proc.waitFor();
        in.close();
        out.close();
        proc.destroy();
        return rspList.toString();
    }

    public static void main(String[] args) throws InterruptedException {
//        System.out.println("network读速率："+networkRead());
//        System.out.println("network写速率："+networkWrite());
//        System.out.println("disk使用率："+diskUsage());
//        System.out.println("disk使用量："+diskUsed());
//        System.out.println("memory使用率："+memoryUsage());
//        System.out.println("memory使用量："+memoryUsed());
//        System.out.println("cpu使用率："+cpuUsage());
    }

}
