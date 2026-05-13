package lk.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SessionManager sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (sessionManager.isLoggedIn()) {
                    intent = new Intent(SplashActivity.this, TodoActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, SignInActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}