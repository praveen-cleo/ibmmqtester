package com.cleo.mqtester.runner;

import com.cleo.mqtester.MQ.MQ;
import com.cleo.mqtester.audit.TimerAudit;
import com.cleo.mqtester.config.MQConfig;

import java.util.Random;

public class MQPutterThread implements Runnable{
    private  MQ mq;
    private MQConfig mqConfig;
    private MessageIdQueue messageIdQueue;
    private TimerAudit timerAudit;
    public MQPutterThread(MQ mq, MQConfig mqConfig){
        this.mq = mq;
        this.mqConfig= mqConfig;
        this.messageIdQueue = MessageIdQueue.getInstance();
        timerAudit = TimerAudit.getInstance();
    }

    public void run(){
        try {
            System.out.println("Starting producer thread");
            mq.connect();
            System.out.println("Putter connected");
            mq.open(MQConfig.PUT_MODE);
            int messageCount = 0;
            while(mqConfig.getMessageCount() > messageCount++) {
                String messageId = mq.put(generateMessage(mqConfig.getTest_message_size_bytes()));
                synchronized (messageIdQueue) {
                    long start = System.currentTimeMillis();
                    messageIdQueue.push(messageId);
                    long done = System.currentTimeMillis();
                    timerAudit.logPutTime(done-start);
                    messageIdQueue.notify();
                }
                System.out.println("Pushed message count "+messageCount);

            }
            mq.close();
            System.out.println("Closing connection");
            mq.disconnect();
        }catch (Exception e)
        {
            System.out.println("PutterThread: Failed due to exception "+e);
            e.printStackTrace();
        }
    }

    private String generateMessage(int messageSize){
        byte[] bytes = new byte[messageSize];
        new Random().nextBytes(bytes);
        String randomMessage = new String(bytes);
        return randomMessage;

    }


}
