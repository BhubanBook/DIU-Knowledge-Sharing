package bd.com.siba.siba_diuhelper.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bd.com.siba.siba_diuhelper.HomeActivity;
import bd.com.siba.siba_diuhelper.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MoreActivity extends AppCompatActivity {

    private Switch donateBloodSwitch;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String userId;

    private CircleImageView profileImageCIV;
    private TextView nameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        initialize();

        getSelfDonorInfo();

        getUserGeneralInfoFromDB();
    }




    private void initialize() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();

        donateBloodSwitch = findViewById(R.id.donateBloodSwitchId);
        profileImageCIV = findViewById(R.id.profileImageCIV);
        nameTV = findViewById(R.id.userFullNameTV);


        donateBloodSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true){
                    databaseReference.child("UserList").child(userId).child("isBloodDonor").setValue(true);
                }else if (isChecked ==false){
                    databaseReference.child("UserList").child(userId).child("isBloodDonor").setValue(false);
                }
            }
        });
    }




    private void getUserGeneralInfoFromDB() {
        DatabaseReference userGeneralInfoDB = databaseReference.child("UserList").child(userId).child("GeneralInfo");
        userGeneralInfoDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("name").exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    setName(name);
                }

                if (dataSnapshot.exists() && dataSnapshot.child("profileImage").exists()){
                    String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                    setProfileImage(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void setName(String name) {
        nameTV.setText(name);
    }

    private void setProfileImage(String profileImage) {
        if (!profileImage.equals("")){
            Glide.with(getApplicationContext()).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.profile)).load(profileImage).into(profileImageCIV);

        }
    }


    private void getSelfDonorInfo() {
        DatabaseReference selfDonorInfoDB = databaseReference.child("UserList").child(userId).child("isBloodDonor");
        selfDonorInfoDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    boolean isBloodDonor = Boolean.valueOf(dataSnapshot.getValue().toString());
                    setDonorInfo(isBloodDonor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setDonorInfo(boolean isBloodDonor) {
        if (isBloodDonor==true){
            donateBloodSwitch.setChecked(true);
        }else if (isBloodDonor==false){
            donateBloodSwitch.setChecked(false);
        }
    }


    @Override
    public void onBackPressed() {
            goToMain();
    }

    private void goToMain() {

        Intent intent = new Intent(MoreActivity.this,HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
    }


    public void backBtnTap(View view) {
        onBackPressed();
    }

    public void goToSearchBlood(View view) {
        startActivity(new Intent(MoreActivity.this,SearchBloodActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
    }

    public void goToProfile(View view) {
        startActivity(new Intent(MoreActivity.this,ProfileActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
    }

    public void goToNotification(View view) {
        startActivity(new Intent(MoreActivity.this,NotificationActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
    }

    public void goToContactUs(View view) {
        startActivity(new Intent(MoreActivity.this,ContactUsActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
    }

    public void goToAboutUs(View view) {
        startActivity(new Intent(MoreActivity.this,AboutUsActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
    }

    public void goToTerms(View view) {
        startActivity(new Intent(MoreActivity.this,TermsAndConditionsActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
    }
}
