package com.example.pi.models;


public class Peers {
    public static final String TABLE_NAME = "Peers";

    public static String COLUMN_ID = "ID";
    public static String COLUMN_TIMESTAMP = "TIMESTAMP";
    public static final String COLUMN_DEVICE_ID = "DeviceID";
    public static final String COLUMN_DNAME = "DeviceName";
    //public static final String COLUMN_INITIALS = "Initials";

    private int ID;
    private String deviceID;
    private String deviceName;
    private String timeStamp;
    private String deviceNickname;

    //Create SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_DEVICE_ID + " VARCHAR(30) PRIMARY KEY ,"
                    + COLUMN_DNAME +" VARCHAR(30),"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

    public Peers() {
    }

    public Peers(int ID, String timeStamp, String deviceID, String deviceName) {
        this.ID = ID;
        this.deviceID = deviceID;
        this.deviceName = deviceName;
        this.timeStamp = timeStamp;
    }

    public int getID()
    {
        return ID;
    }
    public String getDeviceID()
    {
        return deviceID;
    }
    public String getDeviceName()
    {
        return deviceName;
    }
    public String getTimeStamp(String timeStamp)
    {
        return timeStamp;
    }
    public void setID(int ID)
    {
        this.ID=ID;
    }
    public void setDeviceID(String deviceID){
        this.deviceID = deviceID;
    }
    public void setDeviceName(String deviceName){
        this.deviceName = deviceName;
    }
    public void setTimestamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
