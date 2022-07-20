package com.antitheft.donttouchmyphone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.antitheft.donttouchmyphone.Ads.AdManager;

public class PinActivity extends AppCompatActivity {
    boolean isPinActivated = true;

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        loadBanner();

        SwitchCompat pinSwitcher = findViewById(R.id.pinSwitch);
        pinSwitcher.setChecked(true);
        pinSwitcher.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                //open job.
                isPinActivated = true;
            } else {
                //close job.
                isPinActivated = false;
            }
        });
    }

    public void loadBanner() {
        LinearLayout adContainer = findViewById(R.id.banner_container_admob);
        LinearLayout faceBookAdContainer = findViewById(R.id.banner_container_facebook);
        AdManager.loadBanner(PinActivity.this, adContainer);
        // Facebook.loadFaceBookBanner(getApplicationContext(),faceBookAdContainer, RemoteConfig.ADMOB_BANNER_PIN);
    }

    public void savePassword(View view) {
        AdManager.showInter(this, new AdManager.GetBackPointer() {
            @Override
            public void returnAction() {
                PinDataBase.context = getApplicationContext();
                if (isPinActivated) {
                    EditText usernameEditText = (EditText) findViewById(R.id.password);
                    String userPin = usernameEditText.getText().toString();
                    if (userPin.length() != 4) {
                        // Toast.makeText(this, "Please enter 4 digits pin code", Toast.LENGTH_SHORT).show();
                        usernameEditText.setError("Please enter 4 digits pin code");

                    } else {

                        PinDataBase.setPin(userPin);
                        Toast.makeText(PinActivity.this, "Pin set successfully", Toast.LENGTH_SHORT).show();
                        PinDataBase.context = null;
                        finish();
                    }
                } else {
                    PinDataBase.setIsPinActivated(false);
                    PinDataBase.context = null;
                    finish();
                }
            }
        });


    }
}