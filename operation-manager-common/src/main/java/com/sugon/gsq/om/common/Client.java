package com.sugon.gsq.om.common;

import com.sugon.gsq.om.common.rpc.client.ClientHelper;
import com.sugon.gsq.om.common.rpc.client.ProxyHelperTool;
import com.sugon.gsq.om.common.service.MsgService;

import java.util.concurrent.CountDownLatch;

/*
 * ClassName: Client
 * Author: Administrator
 * Date: 2019/9/23 13:33
 */
public class Client {

    public static ProxyHelperTool proxyHelperTool = new ProxyHelperTool();

    public static void main(String[] args) throws Exception {
        int threadNumber = 5;
        CountDownLatch countDownLatch = new CountDownLatch(threadNumber);
        for(int i=0;i<threadNumber;i++){
            new Thread(){
                @Override
                public void run() {
                    MsgService msgService = proxyHelperTool.create(MsgService.class);
                    String reslut = msgService.send(Thread.currentThread().getName());
                    System.out.println("Client("+Thread.currentThread().getName()+") get mag : "  + reslut);
                    countDownLatch.countDown();
                }
            }.start();
        }
        countDownLatch.await();
        ClientHelper.getClientHelper().close();
    }

}
