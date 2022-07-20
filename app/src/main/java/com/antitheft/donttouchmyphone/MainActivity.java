package com.antitheft.donttouchmyphone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
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

import com.antitheft.donttouchmyphone.Ads.AdManager;

import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorMan;
    private Sensor accelerometer;

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
    RelativeLayout password_layout;
    GifImageView gib;
    TextView timerText;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isRunning) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadBanner();
        initSensor();
        initSwitchers();
        initGifModerator();
    }

    public void loadBanner() {
        LinearLayout adContainer = findViewById(R.id.banner_container_admob);
        AdManager.loadBanner(MainActivity.this, adContainer);

    }

    private void initGifModerator() {
        timerText = findViewById(R.id.timerText);
        timerText.setVisibility(View.GONE);
        ImageView action_bar = findViewById(R.id.action_bar);
        action_bar.setBackgroundResource(R.drawable.move_action_bar);
        gib = findViewById(R.id.startMovementListener);
        gib.setImageResource(R.drawable.star_gif);
        gib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdManager.showInter(MainActivity.this, new AdManager.GetBackPointer() {
                    @Override
                    public void returnAction() {
                        gib.setClickable(false);
                        gib.setImageResource(R.drawable.wait);
                        if (!isRunning) {
                            timerText.setVisibility(View.VISIBLE);
                            new CountDownTimer(4000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    int seconds = (int) ((millisUntilFinished / 1000) % 60);
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
                        } else if (isRunning) {
                            PinDataBase.context = getApplicationContext();
                            if (PinDataBase.isPinActivated()) {
                                checkPin(true);
                                PinDataBase.context = null;
                            } else if (!PinDataBase.isPinActivated()) {
                                timerText.setVisibility(View.VISIBLE);
                                new CountDownTimer(4000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        int seconds = (int) ((millisUntilFinished / 1000) % 60);
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

    public void movementListener(String order) {
        if (order.equalsIgnoreCase("start")) {
            sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            notification = 0;
        } else if (order.equalsIgnoreCase("stop")) {
            sensorMan.unregisterListener(this);
            runSoundAlarm(false);
            runFlashAlarm(false);
            runVibrationAlarm(false);
        }
    }

    private void checkPin(Boolean showLayout) {
        password_layout = findViewById(R.id.password_layout);
        if (showLayout) {
            password_layout.setVisibility(View.VISIBLE);
        }
        if (!showLayout) {
            password_layout.setVisibility(View.INVISIBLE);
            timerText.setVisibility(View.VISIBLE);
            new CountDownTimer(4000, 1000) {

                public void onTick(long millisUntilFinished) {
                    int seconds = (int) ((millisUntilFinished / 1000) % 60);
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

                } else {

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

    public void initSensor() {
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    public void initSwitchers() {
        TextView textView = findViewById(R.id.mouvementText);
        textView.setVisibility(View.VISIBLE);
        SwitchCompat soundSwitcher = findViewById(R.id.sound);
        SwitchCompat vibrationSwitcher = findViewById(R.id.vibration);
        SwitchCompat flashSwitcher = findViewById(R.id.flash);
        soundSwitcher.setChecked(true);
        flashSwitcher.setChecked(true);
        vibrationSwitcher.setChecked(true);
        soundSwitcher.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                //open job.
                isMediaAvailable = true;
            } else {
                //close job.
                isMediaAvailable = false;
            }
        });
        flashSwitcher.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                //open job.
                isFlashAvailable = true;
            } else {
                //close job.
                isFlashAvailable = false;
            }
        });
        vibrationSwitcher.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                //open job.
                isVibrationAvailable = true;
            } else {
                //close job.
                isVibrationAvailable = false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sensorMan.equals(null)) {
            sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        System.out.println("Life cycle : onResume");
    }


    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if (mAccel > 0.5) {
                //Toast.makeText(MainActivity.this, "Sensor Run Hua Bc", Toast.LENGTH_SHORT).show();
                //MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.siren);
                //mPlayer.start();

//                    wakeDevice();
                //startActivity(new Intent(MainActivity.this, EnterPin.class));
                //finish();
                //Toast.makeText(MainActivity.this, "Phone moved", Toast.LENGTH_SHORT).show();
                System.out.println("Phone moved");
                if (notification == 0) {
                    notifyUser();
                    notification = 1;
                }


            }
        }

    }

    private void notifyUser() {
        if (isRunning) {
            if (isMediaAvailable) {
                runSoundAlarm(true);
            }
            if (isFlashAvailable) {
                runFlashAlarm(true);
            }
            if (isVibrationAvailable) {
                runVibrationAlarm(true);
            }
        }

    }

    private void runVibrationAlarm(Boolean running) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (running) {


// Start time delay
// Vibrate for 500 milliseconds
// Sleep for 1000 milliseconds
            long[] pattern = {0, 500, 1000};

// 0 meaning is repeat indefinitely
            //vibrator.vibrate(pattern, 0);

        }
        if (!running) {
            //vibrator.cancel();
        }
    }

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void runFlashAlarm(Boolean running) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (running) {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, true);
                    }
                } catch (CameraAccessException e) {
                }
            } else if (!running) {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, false);
                    }
                } catch (CameraAccessException e) {
                }
            }
        }


    }

    private void runSoundAlarm(Boolean running) {
        // int resID=getResources().getIdentifier("sound.mp3", "raw", getPackageName());
        if (running) {
            mplayer = MediaPlayer.create(this, R.raw.sound);
            mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mplayer.start();
                }
            });
            mplayer.start();
        } else if (!running) {
            if (mplayer.isPlaying()) {
                mplayer.pause();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // required method
        //Toast.makeText(this, "Phone Place Changed", Toast.LENGTH_SHORT).show();
    }
}