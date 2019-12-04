package com.sugon.gsq.om.agent;

import com.google.common.collect.Lists;
import com.sugon.gsq.om.agent.controller.AgentController;
import com.sugon.gsq.om.task.Report;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * ClassName: Starter
 * Author: Administrator
 * Date: 2019/8/29 15:22
 */
public class Starter {

    public static void main(String[] args) throws Exception {
//        AgentController ac = new AgentController();
//        ClassLoader classLoader = AgentController.class.getClassLoader();
//        Report report = (Report)classLoader.loadClass("com.sugon.gsq.om.task.Report").newInstance();
//        report.reportSystemMessage();

        Thread thread01 = new Thread(() -> System.out.println("djf"));
        List list = Lists.newArrayList();
        list.forEach(x -> {});
        Test<Am> test = new Test();
        test.Test(() -> {});
    }

    private static class Test<T extends Am> {
        public T data;
        public void Test (T t) {
            this.data = t;
        }
    }

    private interface Am{
        void eat();
    }

}
