import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class BlueMQ {
    private final MessageQueue queue;
    private final AtomicInteger producerCount;
    private final AtomicInteger consumerCount;
    
    public BlueMQ(int queueSize) {
        this.queue = new MessageQueue(queueSize);
        this.producerCount = new AtomicInteger(0);
        this.consumerCount = new AtomicInteger(0);
    }
    
    // Message Queue to hold messages in memory (supports batching)
    static class MessageQueue {
        private final Message[] buffer;
        private int head;
        private int tail;
        private final int size;
        private final ReentrantLock lock = new ReentrantLock();
        
        public MessageQueue(int size) {
            this.size = size;
            this.buffer = new Message[size];
            this.head = 0;
            this.tail = 0;
        }

        // Enqueue message
        public boolean enqueue(Message message) {
            lock.lock();
            try {
                if ((tail + 1) % size == head) { // Queue is full
                    return false;
                }
                buffer[tail] = message;
                tail = (tail + 1) % size;
                return true;
            } finally {
                lock.unlock();
            }
        }

        // Dequeue message
        public Message dequeue() {
            lock.lock();
            try {
                if (head == tail) { // Queue is empty
                    return null;
                }
                Message message = buffer[head];
                head = (head + 1) % size;
                return message;
            } finally {
                lock.unlock();
            }
        }

        // Fetch multiple messages in batch
        public Message[] dequeueBatch(int batchSize) {
            Message[] messages = new Message[batchSize];
            int count = 0;
            lock.lock();
            try {
                while (count < batchSize && head != tail) {
                    messages[count++] = dequeue();
                }
            } finally {
                lock.unlock();
            }
            return messages;
        }
    }

    // Message class to represent a message object
    static class Message {
        private final String content;

        public Message(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

    // Producer to produce messages and push to the queue
    static class Producer implements Runnable {
        private final MessageQueue queue;
        private final int messageCount;
        private final AtomicInteger producerCount;

        public Producer(MessageQueue queue, int messageCount, AtomicInteger producerCount) {
            this.queue = queue;
            this.messageCount = messageCount;
            this.producerCount = producerCount;
        }

        @Override
        public void run() {
            for (int i = 0; i < messageCount; i++) {
                Message message = new Message("Message " + (i + 1));
                while (!queue.enqueue(message)) {
                    // Wait or handle backpressure
                }
                producerCount.incrementAndGet();
            }
        }
    }

    // Consumer to process messages from the queue in batches
    static class Consumer implements Runnable {
        private final MessageQueue queue;
        private final AtomicInteger consumerCount;

        public Consumer(MessageQueue queue, AtomicInteger consumerCount) {
            this.queue = queue;
            this.consumerCount = consumerCount;
        }

        @Override
        public void run() {
            while (true) {
                Message[] batch = queue.dequeueBatch(10); // Processing 10 messages at once
                for (Message message : batch) {
                    if (message != null) {
                        System.out.println("Consumed: " + message.getContent());
                        consumerCount.incrementAndGet();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BlueMQ blueMQ = new BlueMQ(100); // Create a queue with a size of 100

        // Create a producer and a consumer thread
        Thread producerThread = new Thread(new Producer(blueMQ.queue, 1000, blueMQ.producerCount));
        Thread consumerThread = new Thread(new Consumer(blueMQ.queue, blueMQ.consumerCount));

        // Start the producer and consumer threads
        producerThread.start();
        consumerThread.start();

        // Wait for threads to finish (for demonstration purposes)
        producerThread.join();
        consumerThread.join();
    }
}
