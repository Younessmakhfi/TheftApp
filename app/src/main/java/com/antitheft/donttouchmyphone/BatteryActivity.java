package com.antitheft.donttouchmyphone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antitheft.donttouchmyphone.Ads.AdManager;


import pl.droidsonroids.gif.GifImageView;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BatteryActivity extends AppCompatActivity {
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    boolean isRunning = false;
    int notification = 0;
    MediaPlayer mplayer = new MediaPlayer();
    boolean isMediaAvailable = true;
    boolean isVibrationAvailable = true;
    boolean isFlashAvailable = true;
    BroadcastReceiver receiver = null;
    IntentFilter filter;
    RelativeLayout password_layout;
    GifImageView gib;
    TextView timerText;
    protected void onResume() {
        super.onResume();

    }
    protected void onPause() {
        super.onPause();

    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isRunning){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {

            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Start with some variables
        // In onCreate method

        initSensor();
        initSwitchers();
        initGifModerator();
        loadBanner();
    }
    public void loadBanner(){
        LinearLayout adContainer = findViewById(R.id.banner_container_admob);
        LinearLayout faceBookAdContainer = findViewById(R.id.banner_container_facebook);
        AdManager.loadBanner(BatteryActivity.this,adContainer);
        //Facebook.loadFaceBookBanner(getApplicationContext(),faceBookAdContainer, RemoteConfig.FACEBOOK_BANNER_BATTERY);
    }
    private void initGifModerator() {
        timerText = findViewById(R.id.timerText);
        timerText.setVisibility(View.GONE);
        ImageView action_bar = findViewById(R.id.action_bar);
        action_bar.setBackgroundResource(R.drawable.battery_action_bar);
        gib = findViewById(R.id.startMovementListener);
        gib.setImageResource(R.drawable.star_gif);
        gib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdManager.showInter(BatteryActivity.this, new AdManager.GetBackPointer() {
                    @Override
                    public void returnAction() {
                        gib.setClickable(false);
                        gib.setImageResource(R.drawable.wait);
                        if (!isRunning){
                            timerText.setVisibility(View.VISIBLE);
                            new CountDownTimer(4000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    int seconds = (int)((millisUntilFinished / 1000) % 60);
                                    timerText.setText(String.valueOf(seconds));
                                }

                                public void onFinish() {
                                    timerText.setVisibility(View.GONE);
                                    gib.setImageResource(R.drawable.stop);
                                    isRunning = true;
                                    gib.setClickable(true);
                                    batteryListener("start");
                                }
                            }.start();
                        } else if (isRunning){
                            PinDataBase.context = getApplicationContext();
                            if (PinDataBase.isPinActivated()){
                                checkPin(true);
                                PinDataBase.context = null;
                            } else if (!PinDataBase.isPinActivated()){
                                timerText.setVisibility(View.VISIBLE);
                                new CountDownTimer(4000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        int seconds = (int)((millisUntilFinished / 1000) % 60);
                                        timerText.setText(String.valueOf(seconds));
                                    }
                                    public void onFinish() {
                                        timerText.setVisibility(View.GONE);
                                        gib.setImageResource(R.drawable.star_gif);
                                        isRunning = false;
                                        gib.setClickable(true);
                                        batteryListener("stop");
                                    }
                                }.start();
                            }
                        }
                    }
                });

            }
        });
    }
    public void batteryListener(String order){
        if (order.equalsIgnoreCase("start")){
            //sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            registerReceiver(receiver, filter);
            notification = 0;
        }else if (order.equalsIgnoreCase("stop")){
           // sensorMan.unregisterListener(this);
            runSoundAlarm(false);
            runFlashAlarm(false);
            runVibrationAlarm(false);
            unregisterReceiver(receiver);

        }

    }
    public void initSensor(){
        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
                    Toast.makeText(context, "on AC power", Toast.LENGTH_SHORT).show();
                    // on AC power
                } else if (plugged == BatteryManager.BATTERY_PLUGGED_USB) {
                    // on USB power
                    Toast.makeText(context, "on USB power", Toast.LENGTH_SHORT).show();
                    //notifyUser();
                } else if (plugged == 0) {
                    // on battery power
                    Toast.makeText(context, "on battery power", Toast.LENGTH_SHORT).show();
                    if (notification == 0){
                        notifyUser();
                        notification = 1;
                    }
                }
            }
        };
        filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    }
    public void initSwitchers(){
        TextView textView = findViewById(R.id.mouvementText);
        textView.setVisibility(View.GONE);
        SwitchCompat soundSwitcher = findViewById(R.id.sound);
        SwitchCompat vibrationSwitcher = findViewById(R.id.vibration);
        SwitchCompat flashSwitcher = findViewById(R.id.flash);
        soundSwitcher.setChecked(true);
        flashSwitcher.setChecked(true);
        vibrationSwitcher.setChecked(true);
        soundSwitcher.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                //open job.
                isMediaAvailable = true;
            }
            else  {
                //close job.
                isMediaAvailable = false;
            }
        });
        flashSwitcher.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                //open job.
                isFlashAvailable = true;
            }
            else  {
                //close job.
                isFlashAvailable = false;
            }
        });
        vibrationSwitcher.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                //open job.
                isVibrationAvailable = true;
            }
            else  {
                //close job.
                isVibrationAvailable = false;
            }
        });
    }




    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Life cycle : onStop");
    }



    private void notifyUser() {
        if (isMediaAvailable) {
            runSoundAlarm(true);
        }
        if (isFlashAvailable){
            runFlashAlarm(true);
        }
        if (isVibrationAvailable){
            runVibrationAlarm(true);
        }
    }

    private void runVibrationAlarm(Boolean running) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (running){


// Start time delay
// Vibrate for 500 milliseconds
// Sleep for 1000 milliseconds
            long[] pattern = {0, 500, 1000};

// 0 meaning is repeat indefinitely
            vibrator.vibrate(pattern, 0);

        }if (!running){
            vibrator.cancel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void runFlashAlarm(Boolean running) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (running){
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cameraManager.setTorchMode(cameraId, true);
                }
            } catch (CameraAccessException e) {
            }
        } else if (!running){
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cameraManager.setTorchMode(cameraId, false);
                }
            } catch (CameraAccessException e) {
            }
        }

    }
    private void runSoundAlarm(Boolean running) {
        // int resID=getResources().getIdentifier("sound.mp3", "raw", getPackageName());
        if (running){
            mplayer =MediaPlayer.create(this, R.raw.sound);
            mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mplayer.start();
                }
            });
            mplayer.start();
        } else
        if (!running) {
            if (mplayer.isPlaying()){
                mplayer.pause();
            }
        }

    }


    private void checkPin(Boolean showLayout) {
        password_layout = findViewById(R.id.password_layout);
        if (showLayout){
            password_layout.setVisibility(View.VISIBLE);
        }
        if (!showLayout){
            password_layout.setVisibility(View.INVISIBLE);
            timerText.setVisibility(View.VISIBLE);
            new CountDownTimer(4000, 1000) {

                public void onTick(long millisUntilFinished) {
                    int seconds = (int)((millisUntilFinished / 1000) % 60);
                    timerText.setText(String.valueOf(seconds));
                }

                public void onFinish() {
                    timerText.setVisibility(View.GONE);
                    gib.setImageResource(R.drawable.star_gif);
                    isRunning = false;
                    gib.setClickable(true);
                    batteryListener("stop");
                }
            }.start();
        }

    }
    public void savePassword(View view) {
        AdManager.showInter(this, new AdManager.GetBackPointer() {
            @Override
            public void returnAction() {
                PinDataBase.context = getApplicationContext();
                EditText usernameEditText = (EditText) findViewById(R.id.password);
                String userPin = usernameEditText.getText().toString();
                if (userPin.length() != 4) {
                    // Toast.makeText(this, "Please enter 4 digits pin code", Toast.LENGTH_SHORT).show();
                    usernameEditText.setError("Password Wrong!");

                }
                else {

                    PinDataBase.setPin(userPin);
                    //Toast.makeText(this, "Pin set seccessfuly :" + PinDataBase.getPin(), Toast.LENGTH_SHORT).show();
                    if (PinDataBase.pinConnexion(userPin)) {
                        checkPin(false);
                        PinDataBase.context = null;
                    } else {
                        usernameEditText.setError("Password Wrong!");
                    }

                    //finish();
                }
            }
        });



    }
}