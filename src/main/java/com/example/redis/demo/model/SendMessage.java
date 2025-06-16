package com.example.redis.demo.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SendMessage implements Serializable {

    private String channel;
    private String content;
}
