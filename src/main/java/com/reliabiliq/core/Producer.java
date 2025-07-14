package com.reliabiliq.core;
import com.reliabiliq.core.Message;
import java.util.Random;

public class Producer implements Runnable{
    private final String producerId;
    private final Topic topic;
    private final Random random=new Random();
    public Producer(String producerId,Topic topic){
        this.producerId=producerId;
        this.topic=topic;
    }

    @Override
    public void run(){
        int count=0;
        System.out.println("Producer here");
        while(true){

            String key="key"+random.nextInt(10);
            String payload="message-"+producerId+"-"+count++;
            System.out.println("Producer:"+producerId);
            Message msg=new Message(key,payload);
            topic.publish(msg);
            try{
                Thread.sleep(random.nextInt(200));
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
