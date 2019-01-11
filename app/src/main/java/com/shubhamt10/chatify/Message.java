package com.shubhamt10.chatify;

public class Message {

    private String text;
    private String photoUrl;
    private String senderName;
    private String receiverName;
    private String senderUid;
    private String receiverUid;

    public Message() {

    }

    public Message(String mText,String mSenderName,String mReceiverName,String mPhotoUrl,String mSenderUid, String mReceiverUid) {
        this.text = mText;
        this.senderName = mSenderName;
        this.photoUrl = mPhotoUrl;
        this.receiverName = mReceiverName;
        this.senderUid = mSenderUid;
        this.receiverUid = mReceiverUid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }
}
