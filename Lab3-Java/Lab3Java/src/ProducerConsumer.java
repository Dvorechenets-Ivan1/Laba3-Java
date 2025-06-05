import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumer {
    int STORAGE_SIZE = 60;
    private static final int PRODUCTS_COUNT = 1000;
    private static final int PRODUCER_COUNT = 80;
    private static final int CONSUMER_COUNT = 60;

    private final Semaphore empty = new Semaphore(STORAGE_SIZE);
    private final Semaphore full = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1);

    private final Queue<Integer> buffer = new LinkedList<>();

    class Producer extends Thread {
        private int toProduce;

        public Producer(int toProduce) {
            this.toProduce = toProduce;
        }

        public void run() {
            for (int i = 0; i < toProduce; i++) {
                try {
                    empty.acquire();
                    mutex.acquire();

                    buffer.add(i);
                    System.out.println("Producer " + this.getId() + " produced " + i);

                    mutex.release();
                    full.release();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Consumer extends Thread {
        private int toConsume;

        public Consumer(int toConsume) {
            this.toConsume = toConsume;
        }

        public void run() {
            for (int i = 0; i < toConsume; i++) {
                try {
                    full.acquire();
                    mutex.acquire();

                    int item = buffer.poll();
                    System.out.println("Consumer " + this.getId() + " consumed " + item);

                    mutex.release();
                    empty.release();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ProducerConsumer pc = new ProducerConsumer();

        int perProducer = PRODUCTS_COUNT / PRODUCER_COUNT; 
        int perConsumer = PRODUCTS_COUNT / CONSUMER_COUNT; 

        Producer[] producers = new Producer[PRODUCER_COUNT];
        Consumer[] consumers = new Consumer[CONSUMER_COUNT];

        for (int i = 0; i < PRODUCER_COUNT; i++) {
            producers[i] = pc.new Producer(perProducer);
            producers[i].start();
        }

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            consumers[i] = pc.new Consumer(perConsumer);
            consumers[i].start();
        }

        for (Producer p : producers) p.join();
        for (Consumer c : consumers) c.join();

        System.out.println("Завдання виконано.");
    }
}
