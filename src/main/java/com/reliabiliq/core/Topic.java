package com.reliabiliq.core;
import com.reliabiliq.core.Message;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Topic {
    private final String name;
    private final int partitionCount;
    private final List<BlockingQueue<Message>> partitions;

    public Topic(String name,int partitionCount){
        this.name=name;
        this.partitionCount=partitionCount;
        this.partitions=new ArrayList<>(partitionCount);
        for (int i=0;i<partitionCount;i++) {
            partitions.add(new LinkedBlockingQueue<>());
        }
    }
    public void publish(Message message){
        int partition=getPartitionIndex(message.getKey());
        try{
            getPartitionQueue(partition).put(message);
            System.out.println("[Topic: " + name + "] Message routed to partition " + partition + ": " + message.getPayload());
       }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
    public int getPartitionIndex(String key){
        return Math.abs(key.hashCode())%partitionCount;
    }

    public BlockingQueue<Message> getPartitionQueue(int partitionIndex){
        return partitions.get(partitionIndex);
    }
    public int getPartitionCount(){
        return partitionCount;
    }
    public String getName(){
        return name;
    }
}
