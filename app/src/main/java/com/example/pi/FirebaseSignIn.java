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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class FirebaseSignIn extends AppCompatActivity {

    Button signmein;
    EditText emailtextbox, passwordtextbox;
    TextView prom;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    SharedPreferences mailpref;
    ConnectivityManager connectivityManager;
    boolean connected = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        setContentView(R.layout.signin);

        mailpref = this.getSharedPreferences("EMAIL", 0);

        mAuth = FirebaseAuth.getInstance();

        emailtextbox = findViewById(R.id.userEmailIn);
        passwordtextbox = findViewById(R.id.userPasswordIn);
        prom = findViewById(R.id.prompt);
        signmein = findViewById(R.id.signinBtn);
        progressBar=findViewById(R.id.pbarin);


        prom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirebaseSignIn.this, FirebaseSignup.class));
            }
        });

        signmein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppStatus a = new AppStatus();
                if (a.isOnline()){
                    signinUser();
                }
                else{
                    new AlertDialog.Builder(FirebaseSignIn.this)
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

    private void signinUser() {

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

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                   // Toast.makeText(getApplicationContext(), "Logging you in", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(FirebaseSignIn.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "This account isn't in our records", Toast.LENGTH_SHORT).show();
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
                connectivityManager = (ConnectivityManager)FirebaseSignIn.this
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