package com.example.pi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.pi.models.Chats;
import com.example.pi.models.Files;
import com.example.pi.models.Peers;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    //Database Version
    private static  final int DATABASE_VERSION =4;
    //Database Name
    private static  final String DATABASE_NAME = "BeamDB";

    public DBHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create tables
        db.execSQL(Peers.CREATE_TABLE);
        db.execSQL(Chats.CREATE_TABLE);
        db.execSQL(Files.CREATE_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Peers.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Chats.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Files.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    ArrayList<String> getAllPeers(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap()
        Cursor res =  db.rawQuery( "select * from "+Peers.TABLE_NAME, null );
        if (res.isBeforeFirst()){
            while (res.moveToNext()){
                String did = res.getString(0);
                String dname = res.getString(1);
                String dtime = res.getString(2);
                String toadd=did+"##"+dname+"##"+dtime;
                array_list.add(toadd);

            }
            res.close();
        }
        return array_list;
    }

    long insertDevice(String deviceID, String deviceDname, String time){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Peers.COLUMN_DEVICE_ID, deviceID);
        values.put(Peers.COLUMN_DNAME, deviceDname);
        values.put(Peers.COLUMN_TIMESTAMP, time);

        long idDevice = db.insert(Peers.TABLE_NAME, null, values);

        //close connection
        db.close();

        //return newly inserted row id
        return idDevice;

    }

    void insertMessage(String SenderID, String Timestamp, String Message, int Direction){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Chats.SENDER_ID, SenderID);
        values.put(Chats.TIMESTAMP, Timestamp);
        values.put(Chats.MESSAGE, Message);
        values.put(Chats.DIRECTION, Direction);

        db.insert(Chats.TABLE_NAME, null, values);

        //close connection
        db.close();

        //return newly inserted row id
    }

    ArrayList<String> getMessages(String deviceID){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap()
        Cursor res =  db.rawQuery( "select * from "+Chats.TABLE_NAME+ " where SenderID ='"+deviceID+"'", null );
        if (res.isBeforeFirst()){
            while (res.moveToNext()){
                String sender = res.getString(1);
                String time = res.getString(2);
                String msg = res.getString(3);
                String dir = res.getString(4);
                String toadd = time+"##"+msg+"##"+"##"+sender+"##"+dir;
                array_list.add(toadd);
            }
            res.close();
        }
        return array_list;
    }

    void insertFile(String SenderID, String Timestamp, byte[] file, int Direction){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Files.SENDERDEVICE_ID, SenderID);
        values.put(Files.TIMESTAMP, Timestamp);
        values.put(Files.FILE, file);
        values.put(Files.DIRECTION, Direction);

        db.insert(Files.TABLE_NAME, null, values);

        //close connection
        db.close();

        //return newly inserted row id
    }

    ArrayList<String> getFiles(String deviceID){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap()
        Cursor res =  db.rawQuery( "select * from "+Files.TABLE_NAME+ " where SenderID ='"+deviceID+"'", null );
        if (res.isBeforeFirst()){
            while (res.moveToNext()){
                String time = res.getString(1);
                String direct = res.getString(2);
                String hj = res.getString(4);

                String toadd = time+"##"+direct+"##"+hj;

                array_list.add(toadd);
            }
            res.close();
        }
        return array_list;
    }

    ArrayList<byte[]> getImgs(String deviceID){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<byte[]> array_file = new ArrayList<byte[]>();

        //hp = new HashMap()
        Cursor res =  db.rawQuery( "select * from "+Files.TABLE_NAME+ " where SenderID ='"+deviceID+"'", null );
        if (res.isBeforeFirst()){
            while (res.moveToNext()){
                byte[] img = res.getBlob(3);
                array_file.add(img);
            }
            res.close();
        }
        return array_file;
    }

}

