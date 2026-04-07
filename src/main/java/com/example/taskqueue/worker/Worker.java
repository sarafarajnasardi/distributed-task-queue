package com.example.taskqueue.worker;

import com.example.taskqueue.model.Task;
import com.example.taskqueue.queue.RedisQueueService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
public class Worker {

    private final RedisQueueService queueService;

    public Worker(RedisQueueService queueService) {
        this.queueService = queueService;
    }


    @PostConstruct
    public void start() {

        int workerCount = 5; // 🔥 configurable

        ExecutorService executor = Executors.newFixedThreadPool(workerCount);

        for (int i = 0; i < workerCount; i++) {
            executor.submit(() -> {
                while (true) {
                    int MAX_RETRIES = 3;

                    Task task = null;
                    try {
                        task = queueService.dequeue();

                        if (task == null) continue;

                        System.out.println(Thread.currentThread().getName()
                                + " Processing: " + task.getId());

                        //  Simulate failure randomly
//                        if (true) {
//                            throw new RuntimeException("Simulated failure");
//                        }

                        Thread.sleep(2000);

                        System.out.println(Thread.currentThread().getName()
                                + " Done: " + task.getId());

                        queueService.ack(task);

                    } catch (Exception e) {

                        System.out.println("Task failed");

                        if (task != null) {
                            task.setRetryCount(task.getRetryCount() + 1);

                            if (task.getRetryCount() <= MAX_RETRIES) {

                                int delay = (int) Math.pow(2, task.getRetryCount());

                                System.out.println("Retrying task: " + task.getId()
                                        + " after " + delay + " seconds");

                                Thread.sleep(delay * 1000);

                                queueService.requeue(task);

                            } else {
                                System.out.println("Task moved to DLQ: " + task.getId());
                                queueService.moveToDLQ(task);
                            }
                        }
                    }
                }
            });
        }
    }
}