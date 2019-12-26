package com.cleo.mqtester.runner;

import com.cleo.mqtester.MQ.MQ;
import com.cleo.mqtester.audit.TimerAudit;
import com.cleo.mqtester.config.MQConfig;
import com.google.common.base.Strings;

public class MQGetterThread implements Runnable {
    private MQ mq;
    private MessageIdQueue messageIdQueue;
    private MQConfig mqConfig;
    private TimerAudit timerAudit;

    public MQGetterThread(MQ mq,MQConfig mqConfig){
        this.mq = mq;
        this.mqConfig=mqConfig;
        messageIdQueue=MessageIdQueue.getInstance();
        timerAudit= TimerAudit.getInstance();
    }
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        try {
            System.out.println("Starting consumer thread");
            mq.connect();
            System.out.println("Getter: connected");
            mq.open(MQConfig.GET_MODE);
            int messageCount=0;
            while (mqConfig.getMessageCount()>messageCount) {
                String messageId ="";
                synchronized (messageIdQueue) {
                    messageId = messageIdQueue.poll();
                    if(messageId==null) {
                        messageIdQueue.wait();
                        messageId = messageIdQueue.poll();
                    }
                }
                if(!Strings.isNullOrEmpty(messageId)) {
                    long start = System.currentTimeMillis();
                    String message = mq.get(messageId);
                    long done = System.currentTimeMillis();
                    timerAudit.logGetTime(done-start);
                    System.out.println("Retrieved message id: "+messageId+", message count " + messageCount++);
                }
            }
            mq.close();
            mq.disconnect();
        }catch (Exception e){
            System.out.println("GetterThread: Failed due to exception "+e);
            e.printStackTrace();
        }
    }
}
