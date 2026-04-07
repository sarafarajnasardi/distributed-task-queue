package com.example.taskqueue.model;

public class Task {

    private String id;
    private String payload;

    public Task() {}

    public Task(String id, String payload) {
        this.id = id;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public String getPayload() {
        return payload;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}