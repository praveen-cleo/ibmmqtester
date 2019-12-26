package com.cleo.mqtester.MQ;

import com.ibm.mq.MQException;

import java.io.IOException;

public interface MQ {

    void connect() throws Exception;
    String put(String message) throws  Exception;
    String get(String messageID) throws Exception;
    void open(int mode) throws MQException;
    void close() throws MQException;
    void disconnect() throws Exception;
}
