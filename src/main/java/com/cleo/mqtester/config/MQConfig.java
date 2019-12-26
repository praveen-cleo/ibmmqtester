package com.cleo.mqtester.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MQConfig {
    public  static final int PUT_MODE=1,GET_MODE=2;
    private String host;
    private int port;
    private String username;
    private String password;
    private String qmgr;
    private String channel;
    private int thread_count;
    private int test_message_size_bytes;
    private boolean throughJMS;
    private int ccsid;
    private String topic_name;
    private String queue_name;
    private boolean topic_operation;
    private int messageCount;
    private boolean concurrent;

    public boolean isConcurrent() {
        return concurrent;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public String getTopic_name() {
        return topic_name;
    }

    public boolean isTopic_operation() {
        return topic_operation;
    }

    public String getQueue_name() {
        return queue_name;
    }

    public int getCcsid() {
        return ccsid;
    }

    private static MQConfig mqConfig;

    public static MQConfig getInstance(String configFilePath) throws IOException {
        if(mqConfig==null) {
            String config="";
            if(Strings.isNullOrEmpty(configFilePath)){
                config=Resources.toString(MQConfig.class.getResource("mq.json"), Charsets.UTF_8);
            }else {
                config=Resources.toString(new File(configFilePath).toURI().toURL(),Charsets.UTF_8);
            }
            mqConfig = new ObjectMapper().readValue(config, MQConfig.class);
        }
        return mqConfig;
    }

    private MQConfig(){

    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getQmgr() {
        return qmgr;
    }

    public String getChannel() {
        return channel;
    }

    public int getThread_count() {
        return thread_count;
    }

    public int getTest_message_size_bytes() {
        return test_message_size_bytes;
    }

    public boolean isThroughJMS() {
        return throughJMS;
    }
}
