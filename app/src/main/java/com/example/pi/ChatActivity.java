package com.example.pi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bridgefy.sdk.client.BFEngineProfile;
import com.bridgefy.sdk.client.Bridgefy;
import com.example.pi.entities.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.pi.ChatFragment.BROADCAST_CHAT;
import static com.example.pi.ChatFragment.INTENT_EXTRA_USER;
import static com.example.pi.ChatFragment.PAYLOAD_TEXT;
import static com.example.pi.ChatFragment.PAYLOAD_USER;



public class ChatActivity extends AppCompatActivity {

    private String conversationName;
    private String broadcastUser;
    private String conversationId;

    SharedPreferences namepref;
    DBHelper db;
    ArrayList<String> messages;
    ArrayList<String> times;
    ArrayList<String> senders;
    ArrayList<String> dbmsgs;
    ArrayList<String> direction;
    ListView listView;
    MsgListAdapter msgListAdapter;

    Date date = new Date();
    String currentDateTimeString = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(date);

    @BindView(R.id.txtMessage)
    EditText txtMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        ButterKnife.bind(this);

        conversationName = getIntent().getStringExtra(ChatFragment.INTENT_EXTRA_USER);
        conversationId   = getIntent().getStringExtra(ChatFragment.INTENT_EXTRA_UUID);
        broadcastUser = getIntent().getStringExtra(ChatFragment.INTENT_EXTRA_LUSER);

        Log.i("Values", ""+conversationId+""+conversationName+""+broadcastUser);

        listView = findViewById(R.id.msglist);
        ButterKnife.bind(this);

        db = new DBHelper(this);

        messages =new ArrayList<>();
        times = new ArrayList<>();
        senders = new ArrayList<>();
        dbmsgs = new ArrayList<>();
        direction = new ArrayList<>();
        namepref = this.getSharedPreferences("USERNAME", Context.MODE_PRIVATE);

        Toolbar tb = findViewById(R.id.acttoolbar);
        setSupportActionBar(tb);

        dbmsgs = db.getMessages(conversationId);

        if (dbmsgs.isEmpty()){
            Toast.makeText(this,"No messages yet!", Toast.LENGTH_SHORT).show();
        }
        else {
            for (int x = 0; x < dbmsgs.size(); x++) {
                Log.i("From db", dbmsgs.toString());
                String check = dbmsgs.get(x);
                String[] split = check.split("##");
                String msgtime = split[0];
                String message = split[1];
                int dir = Integer.parseInt(split[4]);
                if (dir==1){
                    direction.add("to");
                }else{
                    direction.add("fro");
                }
                messages.add(message);
                times.add(msgtime);
                if (dir==0){
                    senders.add(conversationName);
                }else{
                    senders.add("You");
                }
            }
        }

        msgListAdapter = new MsgListAdapter(R.layout.message_row, R.id.txtMessage, R.id.time, R.id.sendername, getApplicationContext(),messages,times,senders,direction);

        listView.setAdapter(msgListAdapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setStackFromBottom(true);
        namepref = this.getSharedPreferences("USERNAME", Context.MODE_PRIVATE);

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(conversationName);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // register the receiver to listen for incoming messages
        LocalBroadcastManager.getInstance(getBaseContext())
                .registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String uname = intent.getStringExtra(INTENT_EXTRA_USER);
                        String msg = intent.getStringExtra(ChatFragment.INTENT_EXTRA_MSG);
                        int dir = Message.INCOMING_MESSAGE;
                        Message message = new Message(msg);
                        message.setDirection(dir);
                        message.setUsername(uname);
                        msgListAdapter.notifyDataSetChanged();

                        messages.add(msg);
                        times.add(currentDateTimeString);
                        senders.add(conversationName);

                        //add message to list view
                        db.insertMessage(conversationId, currentDateTimeString,msg,dir);
                        messageNotif(conversationName);
                        Log.i("Chat Activity:","Incoming message inserted");
                        Log.i("VALUES:", conversationId + currentDateTimeString + msg + dir);

                    }
                }, new IntentFilter(conversationId));

    }

    public void messageNotif(String name){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Beam notifications");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ChatActivity.this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("New Message!")
                .setContentText("From "+name);
        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }


    @OnClick({R.id.btnSend})
    public void onMessageSend(View v) {
        String user = namepref.getString("USERNAME", "User");
        // get the message and push it to the views
        String messageString = txtMessage.getText().toString();
        if (messageString.trim().length() > 0) {
            // update the views
            txtMessage.setText("");
            Message message = new Message(messageString);
            message.setDirection(Message.OUTGOING_MESSAGE);

            msgListAdapter.notifyDataSetChanged();


            db.insertMessage(conversationId,currentDateTimeString, messageString,Message.OUTGOING_MESSAGE);

            Log.i("VALUES:", conversationId + currentDateTimeString + messageString + Message.OUTGOING_MESSAGE);
            Log.i("Chat Activity:","Outgoing message inserted");

            //add message to list view

            messages.add(messageString);
            times.add(currentDateTimeString);
            senders.add("You");

            // create a HashMap object to send
            HashMap<String, Object> content = new HashMap<>();
            content.put(PAYLOAD_TEXT, messageString);
            content.put(PAYLOAD_USER, user);

            // send text message to device(s)
            if (conversationId.equals(BROADCAST_CHAT)) {
                // we put extra information in broadcast packets since they won't be bound to a session
                content.put(PAYLOAD_USER, user);


                com.bridgefy.sdk.client.Message.Builder builder=new com.bridgefy.sdk.client.Message.Builder();
                builder.setContent(content);
                Bridgefy.sendBroadcastMessage(builder.build(),
                        BFEngineProfile.BFConfigProfileLongReach);

            } else {
                com.bridgefy.sdk.client.Message.Builder builder=new com.bridgefy.sdk.client.Message.Builder();
                builder.setContent(content).setReceiverId(conversationId);
                Bridgefy.sendMessage(builder.build(),
                        BFEngineProfile.BFConfigProfileLongReach);
            }
        }
    }

}