package com.ass3.i190417_i192048.Models;

public class Messages {
    String userID;
    String message;
    String timestamp;
    String chatSendMsgID;
    String chatReceiveMsgID;
    String senderRoom;
    String receiverRoom;
    String msgType;
    String imageURL;

    public Messages() {}

    public Messages(String userID, String message, String timestamp) {
        this.userID = userID;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Messages(String userID, String message) {
        this.userID = userID;
        this.message = message;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getChatSendMsgID() {
        return chatSendMsgID;
    }

    public void setChatSendMsgID(String chatSendMsgID) {
        this.chatSendMsgID = chatSendMsgID;
    }

    public String getChatReceiveMsgID() {
        return chatReceiveMsgID;
    }

    public void setChatReceiveMsgID(String chatReceiveMsgID) {
        this.chatReceiveMsgID = chatReceiveMsgID;
    }

    public String getSenderRoom() {
        return senderRoom;
    }

    public void setSenderRoom(String senderRoom) {
        this.senderRoom = senderRoom;
    }

    public String getReceiverRoom() {
        return receiverRoom;
    }

    public void setReceiverRoom(String receiverRoom) {
        this.receiverRoom = receiverRoom;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}