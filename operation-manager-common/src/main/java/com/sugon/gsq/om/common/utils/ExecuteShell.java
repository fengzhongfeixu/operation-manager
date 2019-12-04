package com.sugon.gsq.om.common.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
 * ClassName: ExecuteShell
 * Author: Administrator
 * Date: 2019/9/2 12:28
 */
public class ExecuteShell {

    private ExecuteShell(){}

    public static String executeCommand(String... commands)
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

}
