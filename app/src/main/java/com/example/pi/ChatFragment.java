package com.example.pi;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;
import com.example.pi.entities.Peer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class ChatFragment extends Fragment {

    private String TAG = "Chat Fragment";
    Activity mActivity;
    SharedPreferences namepref;
    ProgressDialog dialog;


    static final String INTENT_EXTRA_NAME = "peerName";
    static final String INTENT_EXTRA_UUID = "peerUuid";
    static final String INTENT_EXTRA_MSG = "message";
    static final String INTENT_EXTRA_USER = "username";
    static final String INTENT_EXTRA_LUSER = "buser";
    static final String BROADCAST_CHAT = "Broadcast";

    static final String PAYLOAD_DEVICE_TYPE = "device_type";
    static final String PAYLOAD_DEVICE_NAME = "device_name";
    static final String PAYLOAD_TEXT = "text";
    static final String PAYLOAD_USER = "user_name";
    String currentDateTimeString = java.text.DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());


    ArrayList<String> av;
    ArrayList<String> names;
    ArrayList<String> statii;
    ArrayList<String> direction;
    ListAdapter listAdapter;
    ListView lv;
    DBHelper db;
    ArrayList<String> purz;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.chat_fragment, container, false);
        setHasOptionsMenu(true);
        mActivity = getActivity();
        db = new DBHelper(mActivity);
        av = new ArrayList<>();
        names = new ArrayList<>();
        direction = new ArrayList<>();
        statii = new ArrayList<>();
        purz = new ArrayList<>();

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Please wait...");
        dialog.setMessage("Searching for a new peer");
        dialog.setCanceledOnTouchOutside(false);

        purz = db.getAllPeers();
        if (purz.isEmpty()){
            Toast.makeText(mActivity,"No Friends yet!", Toast.LENGTH_SHORT).show();
        }

        if (!purz.isEmpty()) {
            for (int x = 0; x < purz.size(); x++) {
                String check = purz.get(x);
                String[] split = check.split("##");
                String name = split[1];
                av.add(String.valueOf(name.charAt(0)));
                names.add(name);
                statii.add("off");
                direction.add("fro");
            }
        }

        namepref = mActivity.getSharedPreferences("USERNAME", Context.MODE_PRIVATE);
        listAdapter = new ListAdapter(R.layout.peer_row, R.id.peerAvatar, R.id.peerUsername, R.id.online, R.id.offline, mActivity, av, names, statii,direction);
        lv = v.findViewById(R.id.peerlistlist);
        lv.setAdapter(listAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startActivity(new Intent(mActivity, ChatActivity.class)
                        .putExtra(INTENT_EXTRA_NAME, names.get(position))
                        .putExtra(INTENT_EXTRA_UUID, purz.get(position).split("##")[0])
                        .putExtra(INTENT_EXTRA_USER, purz.get(position).split("##")[1]));

            }
        });

        FloatingActionButton fab1 = (FloatingActionButton) v.findViewById(R.id.cstart);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bridgefy.initialize(mActivity, new RegistrationListener() {
                    @Override
                    public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                        Bridgefy.start(messageListener,stateListener);
                    }
                });
                dialog.show();
                Log.i("Chat Fragment", "Bridefy started");
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) v.findViewById(R.id.cstop);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bridgefy.stop();
                Log.i("Chat Fragment", "Bridefy stopped");
            }
        });

        return v;
    }

    public void sendNotification(){
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
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


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mActivity, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Connected!")
                .setContentText("You have connected to a friend. Say hi");
                 notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_broadcast:
                startActivity(new Intent(getActivity(), ChatActivity.class)
                        .putExtra(INTENT_EXTRA_NAME, BROADCAST_CHAT)
                        .putExtra(INTENT_EXTRA_USER, BROADCAST_CHAT)
                        .putExtra(INTENT_EXTRA_UUID, BROADCAST_CHAT));
                return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    /**
     * BRIDGEFY METHODS
     */

    private MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            // direct messages carrying a Device name represent device handshakes
            String currentDateTimeString = java.text.DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
            // direct messages carrying a Device name represent device handshakes
            if (message.getContent().get(PAYLOAD_DEVICE_NAME) != null) {
                Peer peer = new Peer(message.getSenderId(),
                        (String) message.getContent().get(PAYLOAD_DEVICE_NAME), (String) message.getContent().get(PAYLOAD_USER));
                db.insertDevice(peer.getUuid(), peer.getUsername(), currentDateTimeString);
                Toast.makeText(mActivity,"Message connection to "+peer.getUsername(), Toast.LENGTH_SHORT).show();
                Log.i("PEER:","Peer inserted in database");
                Log.d(TAG, "Peer introduced itself: " + peer.getDeviceName() + " " + peer.getUsername());
                dialog.hide();

                sendNotification();

                // any other direct message should be treated as such
            } else {
                String incomingMessage = (String) message.getContent().get("text");
                String senderUsername = (String) message.getContent().get("user_name");
                Log.d(TAG, "Incoming private message: " + incomingMessage);
                Log.d(TAG, "From: " + senderUsername);
                LocalBroadcastManager.getInstance(mActivity.getBaseContext()).sendBroadcast(
                        new Intent(message.getSenderId())
                                .putExtra(INTENT_EXTRA_MSG, incomingMessage)
                                .putExtra(INTENT_EXTRA_USER, senderUsername));
            }

        }

        @Override
        public void onBroadcastMessageReceived(Message message) {
            // we should not expect to have connected previously to the device that originated
            // the incoming broadcast message, so device information0 is included in this packet
            String incomingMsg = (String) message.getContent().get("text");
            String userName = (String) message.getContent().get("user_name");

            Log.d(TAG, "Incoming broadcast message: " + incomingMsg);
            Log.d(TAG, "From: " + userName);
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(
                    new Intent(BROADCAST_CHAT)
                            .putExtra(INTENT_EXTRA_MSG, userName+":\n\n"+incomingMsg));

        }
    };

    private StateListener stateListener = new StateListener() {
        @Override
        public void onDeviceConnected(final Device device, Session session) {
            Log.i(TAG, "onDeviceConnected: " + device.getUserId());
            // send our information to the Device
            HashMap<String, Object> map = new HashMap<>();
            String name = namepref.getString("USERNAME", "User");
            map.put(PAYLOAD_DEVICE_NAME, Build.MANUFACTURER + " " + Build.MODEL);
            map.put(PAYLOAD_DEVICE_TYPE, Peer.DeviceType.ANDROID.ordinal());
            map.put(PAYLOAD_USER, name);

            device.sendMessage(map);
        }

        @Override
        public void onDeviceLost(Device device) {
            Log.w(TAG, "onDeviceLost: " + device.getUserId());
            Toast.makeText(mActivity, "Stopped message connection to peer", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStartError(String message, int errorCode) {
            Log.e(TAG, "onStartError: " + message);

            if (errorCode == StateListener.INSUFFICIENT_PERMISSIONS) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    };
}
