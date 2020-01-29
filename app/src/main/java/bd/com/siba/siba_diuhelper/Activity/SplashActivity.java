package bd.com.siba.siba_diuhelper.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;

import bd.com.siba.siba_diuhelper.HomeActivity;
import bd.com.siba.siba_diuhelper.OtherClass.GpsProvider;
import bd.com.siba.siba_diuhelper.R;


public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private GpsProvider gpsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth= FirebaseAuth.getInstance();
        gpsProvider = new GpsProvider(this,this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (gpsProvider.statusCheck()==true){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (mAuth.getCurrentUser() != null) {
                        Intent intent=new Intent(SplashActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition( R.anim.slide_in_left, R.anim.stay );
                        SplashActivity.this.finish();
                    }
                    else {
                        Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition( R.anim.slide_in_left, R.anim.stay );
                        SplashActivity.this.finish();
                    }
                }
            }, 3000);
        }else {
            gpsProvider.showDialogForLocation();
        }


    }
}
