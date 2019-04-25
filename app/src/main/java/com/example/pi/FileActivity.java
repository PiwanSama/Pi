package com.example.pi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.Message;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.example.pi.entities.BridgefyFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FileActivity extends AppCompatActivity {

    private static final String TAG = "file_activity";
    private String conversationName;
    private String conversationId;
    public int IMAGE_REQUEST_CODE = 553;
    FileListAdapter fileListAdapter;
    DBHelper db;
    ArrayList<byte[]> files;
    ArrayList<String> times;
    ArrayList<String>senders;
    ArrayList<byte[]> dbimgs;
    ArrayList<String> dbfiles;
    SharedPreferences namepref;
    ListView listView;
    Date date = new Date();
    String currentDateTimeString = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(date);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_activity);
        ButterKnife.bind(this);

        conversationName = getIntent().getStringExtra(FileFragment.INTENT_EXTRA_USER);
        conversationId   = getIntent().getStringExtra(FileFragment.INTENT_EXTRA_UUID);

        Toolbar tb = findViewById(R.id.acttoolbar);
        setSupportActionBar(tb);

        db = new DBHelper(this);
        files =new ArrayList<>();
        times = new ArrayList<>();
        senders = new ArrayList<>();
        dbfiles = new ArrayList<>();
        dbimgs = new ArrayList<>();

        dbfiles = db.getFiles(conversationId);
        dbimgs = db.getImgs(conversationId);

        if (dbimgs.isEmpty()){
            Toast.makeText(this,"No files yet!", Toast.LENGTH_SHORT).show();
        }
        else {
            for (int x = 0; x < dbfiles.size(); x++) {
                String check = dbfiles.get(x);
                Log.i("Files from db", dbfiles.toString());
                String[] split = check.split("##");

                String msgtime = split[1];
                int dir = Integer.parseInt(split[2]);
                times.add(msgtime);
                if (dir==1){
                    senders.add(conversationName);
                }else {
                    senders.add("You");
                }
            }
            files.addAll(dbimgs);
        }

        namepref = this.getSharedPreferences("USERNAME", Context.MODE_PRIVATE);
        fileListAdapter = new FileListAdapter( R.layout.file_row, R.id.txtFile, R.id.ftime, R.id.fsender, getApplicationContext(),files,times, senders);
        listView = findViewById(R.id.filelist);
        listView.setAdapter(fileListAdapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setStackFromBottom(true);

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(conversationName);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // register the receiver to listen for incoming bridgefyFiles
        // register the receiver to listen for incoming bridgefyFiles
        LocalBroadcastManager.getInstance(getBaseContext())
                .registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.i(TAG, "onReceive: received message to adapter");
                        Message message = intent.getParcelableExtra(FileFragment.INTENT_EXTRA_FILE);
                        byte[] fileBytes = message.getData();
                        BridgefyFile bridgefyFile = new BridgefyFile((String)message.getContent().get("file"));
                        bridgefyFile.setData(fileBytes);
                        bridgefyFile.setDirection(BridgefyFile.INCOMING_FILE);
                        String name = bridgefyFile.getFilePath();
                        files.add(fileBytes);
                        times.add(currentDateTimeString);
                        senders.add(conversationName);
                        fileListAdapter.notifyDataSetChanged();
                        fileNotif(conversationName);
                        db.insertFile(conversationId, currentDateTimeString, fileBytes, 1);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.length);
                        storeImage(bitmap);
                    }
                }, new IntentFilter(conversationId));

    }

    public void fileNotif(String name){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_LOW);
            // Configure the notification channel.
            notificationChannel.setDescription("Beam notifications");
            notificationChannel.enableLights(false);
            notificationChannel.setVibrationPattern(new long[]{0, 0, 0, 0});
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(FileActivity.this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("New File!")
                .setContentText("From "+name);
        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @OnClick({R.id.sendimg})
    public void onMessageSend(View v) {
        ImagePicker.create(this)
                .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                .folderMode(true) // folder mode (false by default)
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .includeVideo(false) // Show video on image picker
                .single() // single mode
                .theme(R.style.AppTheme)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                .start(IMAGE_REQUEST_CODE); // start image picker activity with request code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
                //try {
              if ((requestCode == IMAGE_REQUEST_CODE) && (resultCode == RESULT_OK)){
                      Image image = ImagePicker.getFirstImageOrNull(data);
                      String filePath = image.getPath();
                      Log.i(TAG, "onActivityResult: file path "+filePath);
                      File file=new File(filePath);
                      Log.i("File", "ccc");
                      byte fileContent[] = new byte[(int)file.length()];
                      try {
                          FileInputStream fin = new FileInputStream(file);
                          fin.read(fileContent);
                          Log.i("File", "hhh");
                          HashMap<String, Object> content = new HashMap<>();
                          content.put("file",file.getName());
                          Log.i("File", "uuu");
                          com.bridgefy.sdk.client.Message.Builder builder=new com.bridgefy.sdk.client.Message.Builder();
                          com.bridgefy.sdk.client.Message message = builder.setReceiverId(conversationId).setContent(content).setData(fileContent).build();
                          Bridgefy.sendMessage(message);
                          Log.i("File", "iii");
                          BridgefyFile bridgefyFile = new BridgefyFile(filePath);
                          bridgefyFile.setDirection(BridgefyFile.OUTGOING_FILE);
                          bridgefyFile.setData(fileContent);
                          Log.i("File Bytes:", fileContent.toString());
                          Log.i("File Path:", filePath);
                          Log.i("File Name:", file.getName());
                          files.add(fileContent);
                          senders.add("You");
                          times.add(currentDateTimeString);
                          fileListAdapter.notifyDataSetChanged();
                          db.insertFile(conversationId,currentDateTimeString,fileContent,0);
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
              }
       }
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/storage/emulated/0/DCIM/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="IMG_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

}


