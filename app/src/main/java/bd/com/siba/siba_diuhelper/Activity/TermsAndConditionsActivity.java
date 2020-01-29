package bd.com.siba.siba_diuhelper.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import bd.com.siba.siba_diuhelper.OtherClass.ExpandableTextView;
import bd.com.siba.siba_diuhelper.R;

public class TermsAndConditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        String yourText = "INTRODUCTORY: Important – please read these terms carefully. By using this Service, " +
                "you agree that you have read, understood, accepted and agreed with the Terms and Conditions. " +
                "If you do not agree to or fall within the Terms and Conditions of the Service (as defined below) " +
                "and wish to discontinue using the Service, please do not continue using this Application or Service. ";

        ExpandableTextView expandableTextView =findViewById(R.id.lorem_ipsum);
        expandableTextView.setText(yourText);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_left);
    }

    public void backBtnTap(View view) {
        onBackPressed();
    }
}
