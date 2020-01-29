package bd.com.siba.siba_diuhelper.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bd.com.siba.siba_diuhelper.HomeActivity;
import bd.com.siba.siba_diuhelper.Model.BasicInfo;
import bd.com.siba.siba_diuhelper.R;

public class LoginActivity extends AppCompatActivity {


    private EditText phoneNumberET;

    private FirebaseAuth firebaseAuth;
    private Boolean status;
    private String phoneNumber;
    private ProgressBar progressBar;
    private Button nextBtn;
    private CheckBox termsAndConditionCB;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initialize();

        getLocationPermission();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        }

        termsAndConditionCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    nextBtn.setVisibility(View.VISIBLE);
                } else {
                    nextBtn.setVisibility(View.GONE);
                }
            }
        });

    }

    private void initialize() {
        firebaseAuth = FirebaseAuth.getInstance();
        phoneNumberET = findViewById(R.id.phoneNumberEtID);
        progressBar = findViewById(R.id.progressBarId);
        progressBar.setVisibility(View.GONE);
        nextBtn = findViewById(R.id.nextBtn);
        termsAndConditionCB = findViewById(R.id.termsAndConditionCBID);

        phoneNumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if (phoneNumberET.getText().toString().length() > 11) {
                    phoneNumberET.setError("Mobile number not more than 11 digits");
                    phoneNumberET.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void next(View view) {
        phoneNumber = phoneNumberET.getText().toString();
        if (!phoneNumber.isEmpty() && phoneNumber.matches("01[0-9]{9}")) {
            checkUser();
        } else {
            phoneNumberET.setError("Enter your valid phone number");
            phoneNumberET.requestFocus();
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }

    }

    private void checkUser() {

        if (isConnected() == false) {
            showDialogForInternet();
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.GONE);

        status = false;
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("UserList");

        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        BasicInfo basicInfo = data.getValue(BasicInfo.class);
                        if (basicInfo != null & basicInfo.getPhoneNumber() != null) {
                            if (basicInfo.getPhoneNumber().contains(phoneNumber) | basicInfo.getPhoneNumber().equals("+88" + phoneNumber)) {
                                status = true;
                            }
                        }
                    }
                    if (status) {
                        startActivity(new Intent(LoginActivity.this, VerifyOTPActivity.class)
                                .putExtra("phoneNumber", phoneNumber)
                                .putExtra("user", "old_user"));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                    } else {
                        startActivity(new Intent(LoginActivity.this, VerifyOTPActivity.class)
                                .putExtra("phoneNumber", phoneNumber)
                                .putExtra("user", "new_user"));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                    }

                    progressBar.setVisibility(View.GONE);
                    nextBtn.setVisibility(View.VISIBLE);
                } else {
                    startActivity(new Intent(LoginActivity.this, VerifyOTPActivity.class)
                            .putExtra("phoneNumber", phoneNumber)
                            .putExtra("user", "new_user"));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                    progressBar.setVisibility(View.GONE);
                    nextBtn.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    /*-------------------------------------------------------------
    Get user current location details -- start
    -------------------------------------------------------------*/

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    private void showDialogForInternet() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_internet, null);
        Button itsOk = mView.findViewById(R.id.itsOkBtnID);
        Button settings = mView.findViewById(R.id.settingsBtnID);

        builder.setView(mView);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        itsOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                dialog.dismiss();
            }
        });
    }

    public boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else return false;
    }
}
