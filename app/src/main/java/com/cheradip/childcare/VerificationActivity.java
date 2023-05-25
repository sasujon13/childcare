package com.cheradip.childcare;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {
    private Button sendButton;
    private Button verifyButton;
    private Button resendButton;
    private EditText verificationCode;
    private EditText phoneNumberEditText;
    private int clickCount = 0;
    private long lastClickTime = 0;
    private int requestCount = 0;
    private CountDownTimer countdownTimer;
    private TextView countDown;
    private TextView registerTextView;
    private TextView registerTextView2;

    private final boolean isLayout1Displayed = true;
    private boolean isSendButtonEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification);

        sendButton = findViewById(R.id.sendButton);
        verifyButton = findViewById(R.id.verifyButton);
        resendButton = findViewById(R.id.resendButton);
        verificationCode = findViewById(R.id.verificationCode);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        countDown = findViewById(R.id.countDown);
        registerTextView = findViewById(R.id.registerTextView);
        registerTextView2 = findViewById(R.id.registerTextView2);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();
                long elapsedMillis = currentTime - lastClickTime;
                lastClickTime = currentTime;

                if (elapsedMillis < getClickInterval(clickCount)) {
                    Toast.makeText(VerificationActivity.this, "Please wait before requesting again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (requestCount >= 5) {
                    Toast.makeText(VerificationActivity.this, "Maximum number of requests reached.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Perform the code request
                requestCode();

                clickCount++;
                requestCount++;

                sendButton.setVisibility(View.GONE);
                phoneNumberEditText.setVisibility(View.GONE);
                verificationCode.setVisibility(View.VISIBLE);
                verifyButton.setVisibility(View.VISIBLE);
                resendButton.setVisibility(View.VISIBLE);
                countDown.setVisibility(View.VISIBLE);
                registerTextView.setVisibility(View.VISIBLE);
                registerTextView2.setVisibility(View.VISIBLE);

                startCountdownTimer(getClickInterval(clickCount));

                if (clickCount == 6) {
                    clickCount = 0; // Reset click count to 0 after the 7th click
                }
            }
        });

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButton.setVisibility(View.VISIBLE);
                phoneNumberEditText.setVisibility(View.VISIBLE);
                verificationCode.setVisibility(View.GONE);
                verifyButton.setVisibility(View.GONE);
                resendButton.setVisibility(View.GONE);
                countDown.setVisibility(View.GONE);
                registerTextView.setVisibility(View.GONE);
                registerTextView2.setVisibility(View.GONE);
            }
        });
    }

    private long getClickInterval(int clickCount) {
        if (clickCount == 0) {
            return 0; // 0 seconds for the 1st click
        } else if (clickCount == 1) {
            return 30000; // 30 seconds for the 2nd click
        } else if (clickCount == 2) {
            return 45000; // 45 seconds for the 3rd click
        } else if (clickCount == 3) {
            return 60000; // 60 seconds for the 4th click
        } else if (clickCount == 4) {
            return 120000; // 120 seconds for the 5th click
        } else if (clickCount == 5) {
            return TimeUnit.HOURS.toMillis(1); // 1 hour for the 6th click
        } else {
            return 0; // No waiting time for clickCount values greater than 6
        }
    }

    private void requestCode() {
        // Add your code to send the code request here
        // This method will be called when the conditions are met for a code request
    }

    private void startCountdownTimer(long durationMillis) {
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        isSendButtonEnabled = false;
        sendButton.setEnabled(false);

        countdownTimer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(minutes);
                countDown.setText("Wait: " + minutes + "m " + seconds + "s");
            }

            @Override
            public void onFinish() {
                countDown.setText("");
                isSendButtonEnabled = true;
                sendButton.setEnabled(true);
            }
        };
        countdownTimer.start();
    }
}