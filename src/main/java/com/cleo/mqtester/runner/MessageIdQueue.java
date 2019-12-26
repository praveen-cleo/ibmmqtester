package com.cleo.mqtester.runner;

import java.util.ArrayList;
import java.util.List;

public class MessageIdQueue {
    private List<String> messageIdList ;
    private int offset = 0;
    private static MessageIdQueue messageIdQueue;
    public static MessageIdQueue getInstance(){
        if(messageIdQueue==null) {
            synchronized (MessageIdQueue.class) {
                if (messageIdQueue == null) {
                    messageIdQueue = new MessageIdQueue();
                }
            }
        }
        return messageIdQueue;
    }

    private MessageIdQueue(){
        messageIdList=new ArrayList<>();

    }

    public  void push(String messageId){
        this.messageIdList.add(messageId);
    }

    public String poll(){
        if(messageIdList.size()>offset) {
            return this.messageIdList.get(offset++);
        }
        return null;
    }
}
