package com.reliabiliq.core;

public class Main {
    public static void main(String[] args){
        Topic ordersTopic=new Topic("Orders",4);
        Thread producer1=new Thread(new Producer("P1",ordersTopic));
        Thread producer2=new Thread(new Producer("P2",ordersTopic));

        producer1.start();
        producer2.start();

        Thread consumer1=new Thread(new Consumer(ordersTopic.getPartitionQueue(0),0));
        Thread consumer2=new Thread(new Consumer(ordersTopic.getPartitionQueue(1),1));
        consumer1.start();
        consumer2.start();
    }
}
