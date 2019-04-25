package com.example.pi;

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

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;




public class FileFragment extends Fragment {

    Activity fActivity;
    SharedPreferences namepref;
    String TAG = "File Fragment";

    static final String INTENT_EXTRA_UUID = "peerUuid";
    static final String INTENT_EXTRA_USER = "username";
    static final String INTENT_EXTRA_FILE  = "bridgefyFile";

    static final String PAYLOAD_DEVICE_TYPE = "device_type";
    static final String PAYLOAD_DEVICE_NAME = "device_name";
    static final String PAYLOAD_USER = "user_name";


    ArrayList<String> av;
    ArrayList<String> names;
    ArrayList<String> statii;
    ArrayList<String> direction;
    ListAdapter listAdapter;
    ListView lv;
    DBHelper db;
    ArrayList<String> purz;
    ProgressDialog dialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.file_fragment, container, false);
        fActivity = getActivity();
        db = new DBHelper(fActivity);
        av = new ArrayList<>();
        names = new ArrayList<>();
        statii = new ArrayList<>();
        direction = new ArrayList<>();
        purz = new ArrayList<>();

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Please wait...");
        dialog.setMessage("Searching for a new peer");
        dialog.setCanceledOnTouchOutside(false);

        purz = db.getAllPeers();
        if (purz.isEmpty()){
            Toast.makeText(fActivity,"No Friends yet!", Toast.LENGTH_SHORT).show();
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

        namepref = fActivity.getSharedPreferences("USERNAME", Context.MODE_PRIVATE);
        listAdapter = new ListAdapter(R.layout.peer_row, R.id.peerAvatar, R.id.peerUsername, R.id.online, R.id.offline, fActivity, av, names, statii,direction);
        lv = v.findViewById(R.id.peerlister);
        lv.setAdapter(listAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(),""+position,Toast.LENGTH_SHORT).show();
                startActivity(new Intent(fActivity, FileActivity.class)
                        .putExtra(INTENT_EXTRA_UUID, purz.get(position).split("##")[0])
                        .putExtra(INTENT_EXTRA_USER, purz.get(position).split("##")[1]));
            }
        });
        FloatingActionButton fab1 = (FloatingActionButton) v.findViewById(R.id.fstart);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bridgefy.initialize(fActivity, new RegistrationListener() {
                    @Override
                    public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                        Bridgefy.start(messageListener,stateListener);
                    }
                });
                dialog.show();
                Log.i("File Fragment", "Bridefy started");
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) v.findViewById(R.id.fstop);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bridgefy.stop();
                Log.i("File Fragment", "Bridefy stopped");
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


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(fActivity, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Connected!")
                .setContentText("You have connected to a friend. Say hi");
        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onStop() {
        super.onStop();
    }
    public void onPause() {
        super.onPause();
    }

    private MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            Log.i(TAG, "onMessageReceived: ");
            // direct messages carrying a Device name represent device handshakes
            if (message.getContent().get("device_name") != null) {
                Peer peer = new Peer(message.getSenderId(),
                        (String) message.getContent().get(PAYLOAD_DEVICE_NAME), (String) message.getContent().get(PAYLOAD_USER));
                peer.setNearby(true);
                Toast.makeText(fActivity,"File connection to "+ peer.getUsername(), Toast.LENGTH_SHORT).show();
                Log.i("File Fragment", "Received message");
                dialog.hide();
                sendNotification();

                // any other direct bridgefyFile should be treated as such
            } else {
                Log.i(TAG, "onMessageReceived: sending broadcast to "+message.getSenderId());
                LocalBroadcastManager.getInstance(fActivity.getBaseContext()).sendBroadcast(
                        new Intent(message.getSenderId())
                                .putExtra(INTENT_EXTRA_FILE, message));
            }

        }

    };


    StateListener stateListener = new StateListener() {
        @Override
        public void onDeviceConnected(final Device device, Session session) {
            // send our information to the Device
            HashMap<String, Object> map = new HashMap<>();
            String name = namepref.getString("USERNAME", "User");
            map.put(PAYLOAD_DEVICE_NAME, Build.MANUFACTURER + " " + Build.MODEL);
            map.put(PAYLOAD_DEVICE_TYPE, Peer.DeviceType.ANDROID.ordinal());
            map.put(PAYLOAD_USER, name);
        }

        @Override
        public void onDeviceLost(Device peer) {
            Log.w(TAG, "onDeviceLost: " + peer.getUserId());
            Toast.makeText(fActivity, "Stopped file connection to peer", Toast.LENGTH_SHORT).show();
        }
    };

}
