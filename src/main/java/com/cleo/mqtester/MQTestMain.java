package com.cleo.mqtester;

import com.cleo.mqtester.MQ.MQ;
import com.cleo.mqtester.MQ.MQFactory;
import com.cleo.mqtester.audit.TimerAudit;
import com.cleo.mqtester.config.MQConfig;
import com.cleo.mqtester.runner.MQGetterThread;
import com.cleo.mqtester.runner.MQPutterThread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MQTestMain {

    private static MQConfig mqConfig;

    public static void main(String[] args) throws FileNotFoundException {
        String configFilePath="";
        if(args!=null && args.length>0){
            configFilePath= args[0];
            File configFile = new File(configFilePath);
            if(!configFile.exists()){
                throw new FileNotFoundException(String.format("Config file %s not found",configFilePath));
            }
        }
        TimerAudit timerAudit = TimerAudit.getInstance();
        try {
            mqConfig = MQConfig.getInstance(configFilePath);
            if(mqConfig.isConcurrent()) {
                MQ mqProducer = MQFactory.getMQClient(mqConfig);
                MQ mqConsumer = MQFactory.getMQClient(mqConfig);

                Thread producer = new Thread(new MQPutterThread(mqProducer, mqConfig));
                Thread consumer = new Thread(new MQGetterThread(mqConsumer, mqConfig));

                producer.start();
                consumer.start();

                producer.join();
                consumer.join();

            }else {
                producer(configFilePath);

                consumer(configFilePath);
            }

            System.out.println("Total PUT time: "+timerAudit.getTotalPutTime());
            System.out.println("Total GET time: "+timerAudit.getTotalGetTime());
            System.out.println("Average PUT time: "+timerAudit.getAveragePutTime());
            System.out.println("Average GET time: "+timerAudit.getAverageGetTime());

        } catch (IOException |InterruptedException e) {

        }
    }

    public static void producer (String configFilePath){

        try {
            mqConfig = MQConfig.getInstance(configFilePath);
            MQ mqProducer= MQFactory.getMQClient(mqConfig);
            MQPutterThread producer = new MQPutterThread(mqProducer,mqConfig);

            producer.run();

        } catch (Exception  e) {

        }
    }

    public static void consumer (String configFilePath){
        try {
            mqConfig = MQConfig.getInstance(configFilePath);
            MQ mqConsumer= MQFactory.getMQClient(mqConfig);

            MQGetterThread consumer = new MQGetterThread(mqConsumer,mqConfig);

            consumer.run();

        } catch (Exception e) {

        }
    }
}