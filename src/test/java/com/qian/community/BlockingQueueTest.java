package com.qian.community;

import javax.swing.*;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * BlockingQueueTest
 *
 * @author yang
 * @date 2022/2/22
 */
public class BlockingQueueTest {

    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(10);
        new Thread(new producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}
class producer implements Runnable {

    private BlockingQueue<Integer> queue;
    public producer(BlockingQueue queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产：" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {

    private BlockingQueue<Integer> queue;
    public Consumer(BlockingQueue queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费：" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

