package com.ass3.i190417_i192048.Models;

public class Users {
    String name;
    String email;
    String password;
    String profileURL;
    String gender;
    String userId;
    String latestMessage;
    String phoneNum;
    String deviceID;
    String status;

    public Users(){}

    public Users(String name, String email, String password, String profileURL, String gender, String phoneNum, String deviceID)
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileURL = profileURL;
        this.gender = gender;
        this.phoneNum = phoneNum;
        this.deviceID = deviceID;
    }


    public Users(String name, String email, String password, String profileURL, String gender, String phoneNum, String userId, String deviceID, String status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileURL = profileURL;
        this.gender = gender;
        this.userId = userId;
        this.phoneNum = phoneNum;
        this.deviceID = deviceID;
        this.status = status;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }
}
