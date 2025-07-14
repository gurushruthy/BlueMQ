package com.reliabiliq.core;

public class Message{
    private final String key;
    private final String payload;
    private final long timestamp;

    public Message(String key,String payload){
        this.key=key;
        this.payload=payload;
        this.timestamp=System.currentTimeMillis();
    }

    public String getKey(){return key;}
    public String getPayload(){return payload;}
    public long getTimestamp(){return timestamp;}
}
