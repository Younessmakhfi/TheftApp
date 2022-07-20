package com.antitheft.donttouchmyphone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.antitheft.donttouchmyphone.Ads.AdManager;
import com.applovin.sdk.AppLovinSdkUtils;
import com.onesignal.OneSignal;

import java.util.concurrent.TimeUnit;

public class SplachScreen extends AppCompatActivity {
    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String ONESIGNAL_APP_ID = "a3c9cb9e-89b3-4ab5-ba79-745beb904dc5";
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        setContentView(R.layout.activity_splach_screen);
        AdManager.initVideoAds(this);

//        CountDownTimer countDownTimer = new CountDownTimer(4000, 1000) {
//            @Override
//            public void onTick(long l) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                letStartActivity();
//
//            }
//        }.start();
        AppLovinSdkUtils.runOnUiThreadDelayed( () -> {
            Intent intent = new Intent( this, HomeActivity.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION );
            startActivity( intent );
        }, TimeUnit.SECONDS.toMillis( 2 ) );
    }

    public void letStartActivity() {
        CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(SplachScreen.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }

}