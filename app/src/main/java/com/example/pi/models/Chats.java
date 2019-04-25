package com.example.pi.models;

import java.sql.Blob;
import java.util.Date;

public class Chats {
    public static final String TABLE_NAME = "Chats";

    public static final String TIMESTAMP = "DateTime";
    public static final String SENDER_ID = "SenderID";
    public static final String RECEIVER_ID = "ReceiverID";
    public static final String MESSAGE = "Message";
    public static final String DIRECTION = "Direction";

    private String ID;
    private String conversationID;
    private String senderID;
    private Date dateTime;
    private Blob attachment;


    //Create SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + SENDER_ID +" VARCHAR(30),"
                    + RECEIVER_ID +" VARCHAR(30),"
                    + TIMESTAMP + " VARCHAR(30),"
                    + MESSAGE +" VARCHAR(300),"
                    + DIRECTION +" VARCHAR(4));";

    public Chats(String ID,String senderID, String message, Date dateTime) {
        this.ID =ID;
        this.senderID = senderID;
        this.dateTime = dateTime;
    }

    public String getID(String ID)
    {
        return ID;
    }
    public String getChatID(String chatID)
    {
        return chatID;
    }
    public String getSenderID(String senderID)
    {
        return senderID;
    }
    public String getMessage(String message)
    {
        return message;
    }
    public Date getDateTime(String message)
    {
        return dateTime;
    }

    public void setID(String ID)
    {
        this.ID= ID;
    }
    public void setChatID(String chatID)
    {
        this.conversationID= chatID;
    }
    public void setSenderID(String senderID)
    {
        this.senderID= senderID;
    }



    public void setMessage(String message)
    {
    }
    public void dateTime(Date dateTime)
    {
        this.dateTime= dateTime;
    }
}
