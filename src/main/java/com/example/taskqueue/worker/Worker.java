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
                    try {
                        Task task = queueService.dequeue();

                        if(task==null)continue;
                        System.out.println(Thread.currentThread().getName()
                                + " Processing: " + task.getId());

                        Thread.sleep(2000);

                        System.out.println(Thread.currentThread().getName()
                                + " Done: " + task.getId());
                        queueService.ack(task);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}