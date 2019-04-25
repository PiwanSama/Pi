package com.example.pi;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment {

    private final String TAG = "NearbyActivity";

    Activity mActivity;
    CardView bt, location, name;
    BluetoothAdapter bluetoothAdapter;
    int REQUEST_ENABLE_BT = 1;
    SharedPreferences namepref, firstpref;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        setHasOptionsMenu(true);
        namepref = mActivity.getSharedPreferences("USERNAME", Context.MODE_PRIVATE);
        firstpref = mActivity.getSharedPreferences("FIRST_RUN", Context.MODE_PRIVATE);

        if (firstpref.getBoolean("FIRST_RUN", true)) {
            Intent i = new Intent(mActivity, UserProfile.class);
            startActivity(i);
        }

        bt = view.findViewById(R.id.btcard);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.image_click));
                startBt();
            }
        });
        location = view.findViewById(R.id.locationcard);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.image_click));
                if (checkPermission()) {

                    Toast.makeText(mActivity, "Make sure your location is on", Toast.LENGTH_LONG).show();

                } else {

                    requestPermission();
                }
            }
        });

        return view;
    }

    private void startBt() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultcode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultcode == RESULT_OK) {
                Toast.makeText(getContext(), "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
            } else if (resultcode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestPermission() {
        requestPermissions(new String[]
                {
                        ACCESS_FINE_LOCATION,
                        BLUETOOTH,
                        BLUETOOTH_ADMIN
                }, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1:

                if (grantResults.length > 0) {

                    boolean BluetoothPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean LocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (BluetoothPermission && LocationPermission) {

                        Toast.makeText(mActivity, "Permissions Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mActivity, "Permissions Denied", Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(mActivity, BLUETOOTH);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(mActivity, ACCESS_FINE_LOCATION);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                FirebaseAuth.getInstance().signOut();
                Intent j = new Intent(mActivity, About.class);
                startActivity(j);
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(mActivity, FirebaseSignIn.class);
                startActivity(i);
                break;
        }
        return false;
    }
}