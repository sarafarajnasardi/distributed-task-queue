package com.example.taskqueue.controller;

import com.example.taskqueue.model.Task;
import com.example.taskqueue.queue.RedisQueueService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final RedisQueueService queueService;

    public TaskController(RedisQueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping
    public String createTask(@RequestBody String payload) {
        String id = UUID.randomUUID().toString();
        Task task = new Task(id, payload);

        queueService.enqueue(task);

        return "Task added with ID: " + id;
    }
}