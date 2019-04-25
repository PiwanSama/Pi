package com.example.pi.models;

import java.io.File;
import java.sql.Blob;
import java.util.Date;

public class Files {
    public static final String TABLE_NAME = "Files";

    public static final String TIMESTAMP = "DateTime";
    public static final String SENDERDEVICE_ID = "SenderID";
    public static final String RECEIVER_ID = "ReceiverID";
    public static final String FILE = "File";
    public static final String DIRECTION = "Direction";

    private String senderID;
    private String dateTime;
    private Blob attachment;


    //Create SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + SENDERDEVICE_ID +" VARCHAR(30),"
                    + RECEIVER_ID +" VARCHAR(30),"
                    + TIMESTAMP + " VARCHAR(30),"
                    + FILE +" BLOB,"
                    + DIRECTION +" VARCHAR(4));";

    public Files(String senderID, Blob file, String dateTime, int direction) {
        this.senderID = senderID;
        this.dateTime = dateTime;
        this.attachment = file;
    }

    public String getSenderID(String senderID)
    {
        return senderID;
    }
    public String getDateTime(String dateTime)
    {
        return dateTime;
    }
    public Blob getFile(Blob file)
    {
        return file;
    }

    public void setSenderID(String senderID)
    {
        this.senderID= senderID;
    }
    public void setDateTime(String dateTime)
    {
        this.dateTime= dateTime;
    }
    public void setFile(Blob file)
    {
        this.attachment= file;
    }
}
