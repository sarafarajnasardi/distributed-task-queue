package com.example.taskqueue.queue;

import com.example.taskqueue.model.Task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue {
    public static BlockingQueue<Task> queue = new LinkedBlockingQueue<>();
}