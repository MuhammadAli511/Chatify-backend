package com.ass3.i190417_i192048.Models;

public class Messages {
    String messageID;
    String sender;
    String receiver;
    String message;
    String messageType;
    String timestamp;

    public Messages() {}

    public Messages(String id, String sender, String receiver, String message, String messageType, String timestamp) {
        this.messageID = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.messageType = messageType;
        this.timestamp = timestamp;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}