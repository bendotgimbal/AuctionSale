package com.project.auction.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.project.auction.R;
import com.project.auction.app.Config;
import com.project.auction.util.NotificationUtils;
import java.util.concurrent.TimeUnit;

public class CountDown extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = CountDown.class.getSimpleName();
  public String Hours, Minutes, Seconds;
  public int timeSet;
  private TextView Waktu, txtRegId;
  private Button Start, Stop;
  private TimerClass timerClass;
  private BroadcastReceiver mRegistrationBroadcastReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_stream_countdown);
    Waktu = findViewById(R.id.timer);
    Start = findViewById(R.id.start);
    Start.setOnClickListener(this);
    Start.setEnabled(false);
    Stop = findViewById(R.id.stop);
    Stop.setOnClickListener(this);
    Stop.setEnabled(false);
    txtRegId = findViewById(R.id.txt_reg_id);

    FirebaseApp.initializeApp(this);

    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(result -> {
      SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
      SharedPreferences.Editor editor = pref.edit();
      editor.putString("regId", result.getResult());
      editor.apply();

      Toast.makeText(CountDown.this, "Token : " + result.getResult(), Toast.LENGTH_LONG).show();
    });

    mRegistrationBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
          FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
          displayFirebaseRegId();
        } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
          String message = intent.getStringExtra("message");
          Toast.makeText(getApplicationContext(), "Push notification: " + message,
              Toast.LENGTH_LONG).show();
          Log.e(TAG, "Firebase Message: " + message);
          if (message.matches("\\d+(?:\\.\\d+)?")) {
            Log.e(TAG, "Matches");
            timeSet = Integer.parseInt(message);
            Minutes = String.format("%02d", 60000 * timeSet / 1000 / 60);
            Waktu.setText("Waktu " + "00" + ":" + Minutes + ":" + "00");

            timerClass = new TimerClass(60000 * timeSet, 1000);
            timerClass.start();
          } else {
            Log.e(TAG, "No Match");
          }
        }
      }
    };

    displayFirebaseRegId();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.start:
        timerClass.start();
        Toast.makeText(CountDown.this, "Mulai", Toast.LENGTH_LONG).show();
        break;

      case R.id.stop:
        timerClass.cancel();
        Toast.makeText(CountDown.this, "Berhenti", Toast.LENGTH_LONG).show();
        break;
    }
  }

  private void displayFirebaseRegId() {
    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
    String regId = pref.getString("regId", null);

    Log.e(TAG, "Firebase reg id: " + regId);

      if (!TextUtils.isEmpty(regId)) {
          txtRegId.setText("Firebase Reg Id: " + regId);
      } else {
          txtRegId.setText("Firebase Reg Id is not received yet!");
      }
  }

  @Override
  protected void onResume() {
    super.onResume();

    LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
        new IntentFilter(Config.REGISTRATION_COMPLETE));
    LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
        new IntentFilter(Config.PUSH_NOTIFICATION));
    NotificationUtils.clearNotifications(getApplicationContext());
  }

  @Override
  protected void onPause() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    super.onPause();
  }

  public class TimerClass extends CountDownTimer {

    TimerClass(long millisInFuture, long countDownInterval) {
      super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
      String waktu = String.format("%02d:%02d:%02d",
          TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
          TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
              TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
          TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
              TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

      Waktu.setText("Waktu " + waktu);
      Log.d("DEBUG", "Timer = " + waktu);
    }

    @Override
    public void onFinish() {
      Toast.makeText(CountDown.this, "Waktu Telah Berakhir", Toast.LENGTH_LONG).show();
    }
  }
}
