package com.cleo.mqtester.MQ;

import com.cleo.mqtester.config.MQConfig;
import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;

import java.util.UUID;

public class MQ_NativeJar implements MQ {
    private static MQQueueManager mqmgr;
    private MQQueue mqQueue;
    private MQTopic mqTopic;

    private static MQConfig mqConfig;
    public static boolean connected = false;

    static int BUF_SIZE = 4096;

    MQ_NativeJar(MQConfig mqConfig){
        this.mqConfig = mqConfig;
    }

    public  void connect() throws MQException {
        synchronized (MQ_NativeJar.class) {
            if (!connected) {
                // Set up the MQSeries environment
                System.out.println("Connecting to MQ");
                MQEnvironment.hostname = mqConfig.getHost();
                MQEnvironment.port = mqConfig.getPort();
                MQEnvironment.channel = mqConfig.getChannel();
                if (mqConfig.getCcsid() > 0) {
                    MQEnvironment.CCSID = mqConfig.getCcsid();
                }
                //MQEnvironment.enableTracing(4 , traceStream);
                MQEnvironment.userID = mqConfig.getUsername();
                MQEnvironment.password=mqConfig.getPassword();
                MQEnvironment.properties.put(CMQC.TRANSPORT_PROPERTY, CMQC.TRANSPORT_MQSERIES_CLIENT);
                mqmgr = new MQQueueManager(mqConfig.getQmgr());
                connected = true;
            }
        }
    }

    public void disconnect() throws Exception{
        synchronized (MQ_NativeJar.class) {
            if(connected==true) {
                mqmgr.close();
                connected = false;
            }
        }
    }

    public void open(int mode) throws MQException {
        int openOption=-1;
        switch (mode) {
            case MQConfig.PUT_MODE: openOption = CMQC.MQOO_OUTPUT |
                    CMQC.MQOO_INQUIRE |
                    //CMQC.MQOO_SET_IDENTITY_CONTEXT |
                    CMQC.MQOO_FAIL_IF_QUIESCING;
            break;
            case MQConfig.GET_MODE: openOption = CMQC.MQOO_INPUT_EXCLUSIVE |
                    CMQC.MQOO_INQUIRE |
                    CMQC.MQOO_FAIL_IF_QUIESCING;
            break;
            default: throw new RuntimeException("Illegal Operation");
        }
        if(mqConfig.isTopic_operation()){
            //mqTopic = mqmgr.accessTopic()

        }else {
            // Now specify the queue that we want to open using
            // the options that were just defined
            if (mqQueue != null && mqQueue.isOpen()) {
                mqQueue.close();
            }
            mqQueue=mqmgr.accessQueue(mqConfig.getQueue_name(),openOption);
        }

    }

    public void close() throws MQException {
        if(mqConfig.isTopic_operation()){

        }else {
            if(mqQueue!=null && mqQueue.isOpen()){
                mqQueue.close();
            }
        }
    }

    public String put(String message) throws Exception {
        String messageId = UUID.randomUUID().toString();
        if(mqConfig.isTopic_operation()){

        }else{
            MQMessage mqMessage = new MQMessage();
            mqMessage.persistence = CMQC.MQPER_PERSISTENT;
            mqMessage.format = CMQC.MQFMT_STRING;

            mqMessage.messageId = messageId.getBytes();
            if(mqConfig.getCcsid()>0){
                mqMessage.characterSet = mqConfig.getCcsid();
            }
            mqMessage.writeString(message);
            MQPutMessageOptions pmo = new MQPutMessageOptions();
            mqQueue.put(mqMessage);

        }
        return messageId;

    }

    public String get(String messageID) throws Exception {
        if(mqConfig.isTopic_operation()){
        return null;
        }else {
            MQGetMessageOptions gmo = new MQGetMessageOptions();
            gmo.matchOptions = CMQC.MQMO_MATCH_MSG_ID;
            gmo.options = CMQC.MQGMO_NO_WAIT  ;
            MQMessage retrievedMessage = new MQMessage();
            byte[] convertedMessageId = messageID.getBytes(); // convert msgId from Hex back to bytes
            retrievedMessage.messageId = convertedMessageId;
            mqQueue.get(retrievedMessage,gmo);
            byte[] bytes = new byte[retrievedMessage.getMessageLength()];
            retrievedMessage.readFully(bytes);
            return new String(bytes);
        }
    }
}
