package info.androidhive.firebasenotifications.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

import info.androidhive.firebasenotifications.R;
import info.androidhive.firebasenotifications.app.Config;
import info.androidhive.firebasenotifications.util.NotificationUtils;

public class CountDown extends AppCompatActivity implements View.OnClickListener{

    private TextView Waktu, txtRegId;
    private Button Start, Stop;
    private  TimerClass timerClass;
    public String Hours, Minutes, Seconds;
    public int timeSet;
    private static final String TAG = CountDown.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stream_countdown);
        Waktu = (TextView) findViewById(R.id.timer);
        Start = (Button) findViewById(R.id.start);
        Start.setOnClickListener(this);
        Start.setEnabled(false);
        Stop = (Button) findViewById(R.id.stop);
        Stop.setOnClickListener(this);
        Stop.setEnabled(false);
        txtRegId = (TextView) findViewById(R.id.txt_reg_id);

//        timeSet = 1;
//        Minutes = String.format("%02d",60000 * timeSet / 1000 / 60);
//        Waktu.setText("Waktu " +"00"+":"+Minutes+":"+"00");
//
//        //Set Waktu selama 3 detik = 60000 * 3 millis dengan interval 1 detik = 1000 millis
//        timerClass = new TimerClass(60000 * timeSet, 1000);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Firebase Message: " + message);
                    if(message.matches("\\d+(?:\\.\\d+)?")){
//                        System.out.println("Matches");
                        Log.e(TAG, "Matches");
                        timeSet = Integer.parseInt(message);
                        Minutes = String.format("%02d",60000 * timeSet / 1000 / 60);
                        Waktu.setText("Waktu " +"00"+":"+Minutes+":"+"00");

                        //Set Waktu selama 3 detik = 60000 * 3 millis dengan interval 1 detik = 1000 millis
                        timerClass = new TimerClass(60000 * timeSet, 1000);
                        timerClass.start();
                    } else {
//                        System.out.println("No Match");
                        Log.e(TAG, "No Match");
                    }
                }
            }
        };

        displayFirebaseRegId();
    }

    //Membuat InnerClass untuk konfigurasi Countdown Time
    public class TimerClass extends CountDownTimer {

        TimerClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //Method ini berjalan saat waktu/timer berubah
        @Override
        public void onTick(long millisUntilFinished) {
            //Kenfigurasi Format Waktu yang digunakan
            String waktu = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

            //Menampilkannya pada TexView
            Waktu.setText("Waktu " +waktu);
            Log.d("DEBUG","Timer = " + waktu);
        }

        @Override
        public void onFinish() {
            ///Berjalan saat waktu telah selesai atau berhenti
            Toast.makeText(CountDown.this, "Waktu Telah Berakhir", Toast.LENGTH_LONG).show();
//            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                //Menjalankan Timer
                timerClass.start();
                Toast.makeText(CountDown.this, "Mulai", Toast.LENGTH_LONG).show();
                break;

            case R.id.stop:
                //menghentikan Timer
                timerClass.cancel();
                Toast.makeText(CountDown.this, "Berhenti", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
