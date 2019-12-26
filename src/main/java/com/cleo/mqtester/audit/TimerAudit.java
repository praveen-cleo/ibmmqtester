package com.cleo.mqtester.audit;

import java.util.ArrayList;
import java.util.List;

public class TimerAudit {
    private List<Long> consumserTimer;
    private List<Long> producerTimer;
    private static TimerAudit instance;

    public static TimerAudit getInstance(){
        if(instance==null){
            synchronized (TimerAudit.class){
                if(instance==null){
                    instance=new TimerAudit();
                }
            }
        }
        return instance;
    }

    private TimerAudit(){
        consumserTimer = new ArrayList<>();
        producerTimer=new ArrayList<>();
    }

    public synchronized void logPutTime(long milliSec){
        producerTimer.add(milliSec);
    }

    public synchronized void logGetTime(long milliSec){
        consumserTimer.add(milliSec);
    }

    public long getTotalPutTime(){
        long sum = producerTimer.stream().reduce(0L,(i,j)->i+j);
        return sum;
    }

    public long getTotalGetTime(){
        long sum = consumserTimer.stream().reduce(0L,(i,j)->i+j);
        return sum;
    }

    public double getAveragePutTime(){
        long sum = producerTimer.stream().reduce(0L,(i,j)->i+j);
        if(sum==0){
            return 0.0;
        }
        return sum/producerTimer.size();
    }

    public double getAverageGetTime(){
        long sum = consumserTimer.stream().reduce(0L,(i,j)->i+j);
        if(sum==0){
            return 0.0;
        }
        return sum/consumserTimer.size();
    }
}
