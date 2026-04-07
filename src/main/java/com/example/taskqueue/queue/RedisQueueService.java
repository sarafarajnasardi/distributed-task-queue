package com.example.taskqueue.queue;

import com.example.taskqueue.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisQueueService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String RETRY_QUEUE = "retry_queue";
    private static final String QUEUE_NAME = "task_queue";
    private static final String PROCESSING_QUEUE = "processing_queue";

    private static final String DLQ = "dead_letter_queue";
    public RedisQueueService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void moveToDLQ(Task task) {
        try {
            String json = objectMapper.writeValueAsString(task);
            redisTemplate.opsForList().leftPush(DLQ, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requeue(Task task) {
        try {
            String json = objectMapper.writeValueAsString(task);
            redisTemplate.opsForList().leftPush(QUEUE_NAME, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void enqueue(Task task) {
        try {
            String json = objectMapper.writeValueAsString(task);
            redisTemplate.opsForList().leftPush(QUEUE_NAME, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void ack(Task task) {
        try {
            String json = objectMapper.writeValueAsString(task);
            redisTemplate.opsForList().remove(PROCESSING_QUEUE, 1, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Task dequeue() {
        try {
            String json = redisTemplate.opsForList()
                    .rightPopAndLeftPush(QUEUE_NAME, PROCESSING_QUEUE, Duration.ofSeconds(30));

            if (json == null) return null;

            return objectMapper.readValue(json, Task.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}