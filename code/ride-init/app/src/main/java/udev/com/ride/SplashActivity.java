package udev.com.ride;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    private TextView ff,splash_text;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ff = (TextView) findViewById(R.id.ff);

        Typeface textface = Typeface.createFromAsset(getAssets(),"fonts/Raleway-Regular.ttf");
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/lib.ttf");
        ff.setTypeface(typeface);

        mAuth = FirebaseAuth.getInstance();

        splash_text = (TextView)findViewById(R.id.splash_text);
        splash_text.setTypeface(textface);
        splash_text.setText("checking for Authentication...");

        if(mAuth.getCurrentUser()!=null){
            splash_text.setText("Authentication successful");

            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
        }else {
            splash_text.setText("Starting Login Screen...");


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
        }
    }

}