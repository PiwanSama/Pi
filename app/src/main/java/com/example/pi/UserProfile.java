package com.example.pi;

import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.abdularis.civ.AvatarImageView;

import androidx.appcompat.app.AppCompatActivity;



public class UserProfile extends AppCompatActivity implements MonDialog.MonDialogListener{

    TextView usernameText, userEmail;
    AvatarImageView av;
    ImageView pencil;
    SharedPreferences namepref, mailpref, settingspref;
    BluetoothAdapter bt;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);;

        usernameText = (TextView) findViewById(R.id.peerUsername);
        userEmail = (TextView) findViewById(R.id.userEmail);
        pencil = (ImageView) findViewById(R.id.editName);
        pencil.setOnClickListener(new UnameClicked());
        av = findViewById(R.id.userAvatar);

        namepref = this.getSharedPreferences("USERNAME", 0);
        String name = namepref.getString("USERNAME", "Your Username");
        av.setInitial(name);
        usernameText.setText(name);

        mailpref = this.getSharedPreferences("EMAIL", 0);
        String mail = mailpref.getString("EMAIL", "Your Email");
        userEmail.setText(mail);

        settingspref = this.getSharedPreferences("FIRST_RUN", 0);
    }

    public void openDialog() {
        MonDialog theDialog = new MonDialog();
        theDialog.show(getSupportFragmentManager(), "The dialog");
    }

    class UnameClicked implements View.OnClickListener{
        @Override
        public void onClick(View v){
            unameClicked();
        }
    }

    private void unameClicked() {
        openDialog();
    }

    public void applyTexts(String username) {
        if (username.isEmpty()){
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else{
            usernameText.setText(username);
            toSplit();
            if (settingspref.getBoolean("FIRST_RUN", true)) {
                SharedPreferences.Editor editor = settingspref.edit();
                editor.clear();
                editor.putBoolean("FIRST_RUN", false);
                editor.apply();
                editor.commit();
            }
        }
    }
    public void toSplit() {
        String newusername = usernameText.getText().toString();
        StringBuilder initials = new StringBuilder();
        String init = "";
        for (String s : newusername.split(" ")) {
            init = initials.append(s.charAt(0)).toString();
            av = (AvatarImageView) findViewById(R.id.userAvatar);
            av.setInitial(init.toUpperCase());
            av.setState(AvatarImageView.SHOW_INITIAL);
        }
        SharedPreferences.Editor editor = namepref.edit();
        editor.clear();
        editor.putString("USERNAME", newusername);
        editor.apply();
        editor.commit();
        Log.i("Uname Change", "New uname is:"+newusername);
    }

}






