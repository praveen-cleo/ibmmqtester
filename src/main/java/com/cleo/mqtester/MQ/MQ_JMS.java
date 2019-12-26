package com.cleo.mqtester.MQ;

import com.cleo.mqtester.config.MQConfig;
import com.ibm.mq.MQException;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import javax.jms.*;
import java.io.IOException;
import java.util.UUID;

public class MQ_JMS implements MQ{
    private MQConfig mqConfig;
    private static JMSContext context;
    private Destination destination;
    private JMSProducer producer;
    private JMSConsumer consumer;
    private static boolean connected=false;


    public MQ_JMS(MQConfig mqConfig){
        this.mqConfig = mqConfig;
    }

    public void connect() throws Exception {
        synchronized (MQ_JMS.class){
            if(!connected){
                JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
                JmsConnectionFactory cf = ff.createConnectionFactory();

                // Set the properties
                cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, mqConfig.getHost());
                cf.setIntProperty(WMQConstants.WMQ_PORT, mqConfig.getPort());
                cf.setStringProperty(WMQConstants.WMQ_CHANNEL, mqConfig.getChannel());
                cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
                cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, mqConfig.getQmgr());
                cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "IBMMQ tester");
                cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
                cf.setStringProperty(WMQConstants.USERID, mqConfig.getUsername());
                cf.setStringProperty(WMQConstants.PASSWORD, mqConfig.getPassword());

                // Create JMS objects
                context = cf.createContext();
                connected=true;
            }
        }

    }

    public String put(String message) throws Exception {

        TextMessage textMessage = context.createTextMessage(message);
        String messageId= UUID.randomUUID().toString();
        textMessage.setJMSMessageID(messageId);
        producer.send(destination, message);
        return messageId;
    }

    public String get(String messageID) throws Exception {
        String message = consumer.receiveBody(String.class,15000);
        return message;
    }

    public void open(int mode) throws MQException {
        if(mqConfig.isTopic_operation()){

        }else {
            destination = context.createQueue("queue:///" + mqConfig.getQueue_name());
        }
        switch (mode){
            case MQConfig.PUT_MODE: producer = context.createProducer();
            break;
            case MQConfig.GET_MODE: consumer = context.createConsumer(destination);
        }

    }

    public void close() throws MQException {

    }

    public void disconnect() throws Exception {

    }
}
