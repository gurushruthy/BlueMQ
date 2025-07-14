package com.reliabiliq.core;
import com.reliabiliq.core.Message;
import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
    private final BlockingQueue<Message> queue;
    private final int partitionId;

    public Consumer(BlockingQueue<Message> queue, int partitionId){
        this.queue=queue;
        this.partitionId=partitionId;
    }

    @Override
    public void run(){
        System.out.println(">>> Consumer for partition " + partitionId + " started.");
        while(true){
            try{
                Message message=queue.take();
                System.out.printf(
                        "[Consumer-Partition-%d] <- Message from key=%s: %s\n",
                        partitionId,
                        message.getKey(),
                        message.getPayload()
                );
                Thread.sleep(100);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
