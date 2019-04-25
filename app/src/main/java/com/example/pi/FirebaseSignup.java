package com.example.pi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class FirebaseSignup extends AppCompatActivity {

    EditText emailtextbox, passwordtextbox;
    Button signmeup;
    ProgressBar progressBar;
    SharedPreferences mailpref;
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    static Context context;
    boolean connected = false;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        setContentView(R.layout.signup);

        mailpref = this.getSharedPreferences("EMAIL", 0);

        emailtextbox = findViewById(R.id.userEmailUp);
        passwordtextbox = findViewById(R.id.userPasswordUp);
        progressBar= findViewById(R.id.pbarup);

        mAuth = FirebaseAuth.getInstance();

        signmeup = findViewById(R.id.signupBtn);
        signmeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseSignup.AppStatus a = new FirebaseSignup.AppStatus();
                if (a.isOnline()){
                    registerUser();
                }
                else{
                    new AlertDialog.Builder(FirebaseSignup.this)
                            .setTitle("Couldn't Connect")
                            .setMessage("Please connect to the internet to sign in")
                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(R.string.cancel, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
    }


    private void registerUser() {
        String email = emailtextbox.getText().toString().trim();
        String password  = passwordtextbox.getText().toString().trim();
        if(email.isEmpty()){
            emailtextbox.setError("Email is required");
            emailtextbox.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailtextbox.setError("Please enter a valid email");
            emailtextbox.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordtextbox.setError("Password is required");
            passwordtextbox.requestFocus();
            return;
        }
        if (password.length()<=8){
            passwordtextbox.setError("Password must be greater than 8 characters");
            passwordtextbox.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "You're all set!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(FirebaseSignup.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                    else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(getApplicationContext(), "You're already registered!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        SharedPreferences.Editor editor = mailpref.edit();
        editor.putString("EMAIL", email);
        editor.apply();
        editor.commit();
    }
    class AppStatus {

        boolean isOnline() {
            try {
                connectivityManager = (ConnectivityManager)FirebaseSignup.this
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                connected = networkInfo != null && networkInfo.isAvailable() &&
                        networkInfo.isConnected();
                return connected;


            } catch (Exception e) {
                System.out.println("CheckConnectivity Exception: " + e.getMessage());
                Log.v("connectivity", e.toString());
            }
            return connected;
        }
    }
}