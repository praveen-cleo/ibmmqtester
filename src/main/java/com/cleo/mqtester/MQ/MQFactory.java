package com.cleo.mqtester.MQ;

import com.cleo.mqtester.config.MQConfig;

public class MQFactory {
    public static MQ getMQClient(MQConfig mqConfig){
        MQ mq=null;
        if(mqConfig.isThroughJMS()){
            mq= new MQ_JMS(mqConfig);
        }else{
            mq = new MQ_NativeJar(mqConfig );

        }
        return mq;

    }
}
