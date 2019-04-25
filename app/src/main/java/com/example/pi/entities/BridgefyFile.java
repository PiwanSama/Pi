package com.example.pi.entities;

import com.google.gson.Gson;

public class BridgefyFile {

    public final static int INCOMING_FILE = 0;
    public final static int OUTGOING_FILE = 1;

    private int direction;
    private String deviceName;
    private String filePath;
    private byte[] data;

    public BridgefyFile (String text){
        this.filePath = text;
    }

    public int getDirection(){
        return direction;
    }

    public void setDirection(int direction){
        this.direction = direction;
    }

    public String getDeviceName(){
        return deviceName;
    }

    public String getFilePath(){
        return filePath;
    }

    public void setDeviceName(String deviceName){
        this.deviceName = deviceName;
    }

    public static BridgefyFile create(String json){
        return new Gson().fromJson(json, BridgefyFile.class);
    }

    @Override
    public String toString(){
        return new Gson().toJson(this);
    }

    public void setData(byte [] data){
        this.data = data;
    }

    public byte[] getData(){
        return data;
    }
}
