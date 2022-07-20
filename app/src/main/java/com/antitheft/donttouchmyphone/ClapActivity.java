package com.antitheft.donttouchmyphone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antitheft.donttouchmyphone.Ads.AdManager;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import pl.droidsonroids.gif.GifImageView;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ClapActivity extends AppCompatActivity {
    boolean isRunning = false;
    int notification = 0;
    MediaPlayer mplayer = new MediaPlayer();
    boolean isMediaAvailable = true;
    boolean isVibrationAvailable = true;
    boolean isFlashAvailable = true;
    Thread clapThread = null;
    Boolean isThreadRunning = false;
    RelativeLayout password_layout;
    GifImageView gib;
    TextView timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Start with some variables
        // In onCreate method


        initSwitchers();
        initGifModerator();
        checkRecordPermission();
        loadBanner();

    }
    public void loadBanner(){
        LinearLayout adContainer = findViewById(R.id.banner_container_admob);
        LinearLayout faceBookAdContainer = findViewById(R.id.banner_container_facebook);
        AdManager.loadBanner(ClapActivity.this,adContainer);
        //Facebook.loadFaceBookBanner(getApplicationContext(),faceBookAdContainer, RemoteConfig.FACEBOOK_BANNER_BATTERY);
    }
    private void checkRecordPermission() {
        if (ContextCompat.checkSelfPermission(ClapActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ClapActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1234);
        }else
        {
            initSensor();
        }
    }
    private void initGifModerator() {
        timerText = findViewById(R.id.timerText);
        timerText.setVisibility(View.GONE);
        ImageView action_bar = findViewById(R.id.action_bar);
        action_bar.setBackgroundResource(R.drawable.clap_action_bar);
        gib = findViewById(R.id.startMovementListener);
        gib.setImageResource(R.drawable.star_gif);
        gib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdManager.showInter(ClapActivity.this, new AdManager.GetBackPointer() {
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
                                    movementListener("start");
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
                                        movementListener("stop");
                                    }
                                }.start();
                            }
                        }
                    }
                });

            }
        });
    }
    public void movementListener(String order){
        if (order.equalsIgnoreCase("start")){
            if (!clapThread.isAlive()){

                clapThread.start();

                Toast.makeText(this, "isThreadRunning is now : " + isThreadRunning, Toast.LENGTH_SHORT).show();
            }
            notification = 0;
            isThreadRunning = true;
        }else if (order.equalsIgnoreCase("stop")){
            //clapThread.stop();
            isThreadRunning = false;
            Toast.makeText(this, "isThreadRunning is now : " + isThreadRunning, Toast.LENGTH_SHORT).show();
            runSoundAlarm(false);
            runFlashAlarm(false);
            runVibrationAlarm(false);
            notification = 0;
        }

    }
    public void initSensor(){
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
        double threshold = 8;
        double sensitivity = 20;
        PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                new OnsetHandler() {

                    @Override
                    public void handleOnset(double time, double salience) {
                        Log.d("TAG", "Clap detected!");
                        if (notification == 0){
                            System.out.println("isThreadRunning is now : " + isThreadRunning);
                            if (isThreadRunning){
                                // showToast();
                                notifyUser();

                            }


                        }
                        notification ++;
                        if (notification >= 2){
                            notification = 0;
                        }

                        //detectClap();
                    }
                }, sensitivity, threshold);

        dispatcher.addAudioProcessor(mPercussionDetector);
        clapThread = new Thread(dispatcher,"Audio Dispatcher");
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
                    movementListener("stop");
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
    protected void onResume() {
        super.onResume();

    }
    protected void onPause() {
        super.onPause();

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1234: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initSensor();

                } else {
                    Log.d("TAG", "permission denied by user");
                }
                return;
            }
        }

    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isThreadRunning){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {

            finish();
        }
    }
}