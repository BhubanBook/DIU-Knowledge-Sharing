package bd.com.siba.siba_diuhelper.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bd.com.siba.siba_diuhelper.HomeActivity;
import bd.com.siba.siba_diuhelper.Model.BasicInfo;
import bd.com.siba.siba_diuhelper.OtherClass.CustomToast;
import bd.com.siba.siba_diuhelper.R;

public class VerifyOTPActivity extends AppCompatActivity {

    private EditText otp1Et,otp2Et,otp3Et,otp4Et,otp5Et,otp6Et;
    private TextView showNumberTV;

    private String phoneNumber,userType,phoneNumberVerificationId,userId;
    private String otp1,otp2,otp3,otp4,otp5,otp6;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        initialize();
        onClick();
        sendVerificationCode(phoneNumber);
    }

    private void initialize() {

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        userType = getIntent().getStringExtra("user");
        showNumberTV= findViewById(R.id.showNumberTVID);
        progressBar = findViewById(R.id.progressBarId);
        continueBtn = findViewById(R.id.continueBtn);
        showNumberTV.setText(phoneNumber);
        otp1Et= findViewById(R.id.otp1EtID);
        otp2Et= findViewById(R.id.otp2EtID);
        otp3Et= findViewById(R.id.otp3EtID);
        otp4Et= findViewById(R.id.otp4EtID);
        otp5Et= findViewById(R.id.otp5EtID);
        otp6Et= findViewById(R.id.otp6EtID);
        mAuth= FirebaseAuth.getInstance();
    }

    private void onClick() {

        otp1Et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp1Et.getText().toString().length()>=1){
                    otp2Et.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp2Et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp2Et.getText().toString().length()==1){
                    otp3Et.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp3Et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp3Et.getText().toString().length()==1){
                    otp4Et.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp4Et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp4Et.getText().toString().length()==1){
                    otp5Et.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp5Et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp5Et.getText().toString().length()==1){
                    otp6Et.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp6Et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp6Et.getText().toString().length()==1){

                    otp1 = otp1Et.getText().toString();
                    otp2 = otp2Et.getText().toString();
                    otp3 = otp3Et.getText().toString();
                    otp4 = otp4Et.getText().toString();
                    otp5 = otp5Et.getText().toString();
                    otp6 = otp6Et.getText().toString();

                    String otp = otp1 + otp2 + otp3 + otp4 + otp5 + otp6;
                    if (otp1.equals("")
                            || otp2.equals("")
                            || otp3.equals("")
                            || otp4.equals("")
                            || otp5.equals("")
                            || otp6.equals("")
                            || otp.length() < 6) {
                        CustomToast.errorToast("Please insert 6 digits code properly",VerifyOTPActivity.this);
                    } else {
                        verifyPhoneCode(otp);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+88"+phoneNumber,
                120,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {

            String mCode=credential.getSmsCode();
            if (mCode!=null){

                otp1Et.setText(String.valueOf(mCode.charAt(0)));
                otp2Et.setText(String.valueOf(mCode.charAt(1)));
                otp3Et.setText(String.valueOf(mCode.charAt(2)));
                otp4Et.setText(String.valueOf(mCode.charAt(3)));
                otp5Et.setText(String.valueOf(mCode.charAt(4)));
                otp6Et.setText(String.valueOf(mCode.charAt(5)));
                verifyPhoneCode(mCode);
            }


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {

            phoneNumberVerificationId = verificationId;

        }
    };
    private void verifyPhoneCode(String code) {

        continueBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneNumberVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            userId = mAuth.getCurrentUser().getUid();

                            FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
                            String token_id= FirebaseInstanceId.getInstance().getToken();
                            Map<String,Object> userMap=new HashMap<>();
                            userMap.put("token_id",token_id);
                            userMap.put("name","Demo Name");

                            if (userType.equals("new_user")){
                                //fireStore
                                mFireStore.collection("UserList").document(userId).set(userMap);

                                BasicInfo rideSeeker = new BasicInfo(userId,phoneNumber,System.currentTimeMillis());
                                FirebaseDatabase.getInstance().getReference("UserList").child(userId).setValue(rideSeeker).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        Intent intent=new Intent(VerifyOTPActivity.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        overridePendingTransition( R.anim.slide_in_left, R.anim.stay );

                                        CustomToast.successToast("Login successful",VerifyOTPActivity.this);
                                    }
                                });
                            }
                            else {

                                //fireStore
                                mFireStore.collection("UserList").document(userId).set(userMap);

                                Intent intent=new Intent(VerifyOTPActivity.this,HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition( R.anim.slide_in_left, R.anim.stay );

                                CustomToast.successToast("Welcome back",VerifyOTPActivity.this);
                            }




                        } else {
                            CustomToast.errorToast("Invalid code",VerifyOTPActivity.this);
                            continueBtn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                });
    }


    public void continueNext(View view) {


        otp1 = otp1Et.getText().toString();
        otp2 = otp2Et.getText().toString();
        otp3 = otp3Et.getText().toString();
        otp4 = otp4Et.getText().toString();
        otp5 = otp5Et.getText().toString();
        otp6 = otp6Et.getText().toString();

        String otp = otp1 + otp2 + otp3 + otp4 + otp5 + otp6;
        if (otp.length() < 6) {
            CustomToast.errorToast("Please insert 6 digits code properly",VerifyOTPActivity.this);

        } else {
            verifyPhoneCode(otp);
        }

    }

    public void backLoginActivity(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay,R.anim.slide_out_left);
    }
}
