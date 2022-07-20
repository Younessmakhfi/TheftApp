package com.antitheft.donttouchmyphone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.antitheft.donttouchmyphone.Ads.AdManager;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loadBanner();
    }
    public void loadBanner(){
        LinearLayout adContainer = findViewById(R.id.banner_container_admob);
        LinearLayout faceBookAdContainer = findViewById(R.id.banner_container_facebook);
        AdManager.loadBanner(HomeActivity.this,adContainer);
        //Facebook.loadFaceBookBanner(getApplicationContext(),faceBookAdContainer, RemoteConfig.FACEBOOK_BANNER_HOME);
    }
    public void startMovementDetector(View view) {
        try {
            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Start Sensor Activity", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

       // Toast.makeText(this, "Start Movement", Toast.LENGTH_SHORT).show();
    }
    protected void onResume() {
        super.onResume();
    }
    protected void onPause() {
        super.onPause();
    }
    public void startPinSetter(View view) {
        AdManager.showInter(this, new AdManager.GetBackPointer() {
            @Override
            public void returnAction() {
                Intent intent = new Intent(HomeActivity.this,PinActivity.class);

                startActivity(intent);
            }
        });


    }

    public void StartBatteryDetector(View view) {
        AdManager.showInter(this, new AdManager.GetBackPointer() {
            @Override
            public void returnAction() {
                Intent intent = new Intent(HomeActivity.this,BatteryActivity.class);

                startActivity(intent);
            }
        });


    }

    public void StartClapDetector(View view) {
        AdManager.showInter(this, new AdManager.GetBackPointer() {
            @Override
            public void returnAction() {
                Intent intent = new Intent(HomeActivity.this,ClapActivity.class);
                startActivity(intent);
            }
        });


    }


}