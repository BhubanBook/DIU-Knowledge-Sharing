package bd.com.siba.siba_diuhelper.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import bd.com.siba.siba_diuhelper.CustomAdapter.ViewPagerAdapter;
import bd.com.siba.siba_diuhelper.Fragment.GeneralInfoFragment;
import bd.com.siba.siba_diuhelper.Fragment.AddressInfoFragment;
import bd.com.siba.siba_diuhelper.Fragment.UniversityInfoFragment;
import bd.com.siba.siba_diuhelper.OtherClass.CustomToast;
import bd.com.siba.siba_diuhelper.R;
import bd.com.siba.siba_diuhelper.UI.SelectImageBottomSheet;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements SelectImageBottomSheet.BottomSheetListener{



    private static int REQUEST_CODE_FOR_CAMERA=1;
    private static int REQUEST_CODE_FOR_GALLERY=2;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private SelectImageBottomSheet selectImageBottomSheet;

    private ProgressDialog progressDialog;
    private CircleImageView profileImageCIV;
    private TextView mobileNumberTV, memberSinceTV;


    private String userId;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-yyyy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        initialize();

        adapter.addFragment(new GeneralInfoFragment(),"General Info");
        adapter.addFragment(new AddressInfoFragment(),"Address Info");
        adapter.addFragment(new UniversityInfoFragment(),"University Info");

        viewPager.setOffscreenPageLimit(3);
        viewPager.setSaveFromParentEnabled(false);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        getUserInfoFromDB();
    }




    private void initialize(){
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();

        adapter= new ViewPagerAdapter(getSupportFragmentManager());
        viewPager=findViewById(R.id.viewPager_id);
        tabLayout=findViewById(R.id.tabLayout_id);
        profileImageCIV = findViewById(R.id.userProfileImage);
        mobileNumberTV = findViewById(R.id.mobileNumberTV);
        memberSinceTV = findViewById(R.id.memberSinceTV);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }


    private void getUserInfoFromDB() {
        DatabaseReference userDB = databaseReference.child("UserList").child(userId);

        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String mobileNumber = dataSnapshot.child("phoneNumber").getValue().toString();
                    long createdDate = Long.valueOf(dataSnapshot.child("createdDate").getValue().toString());
                    String profileImage = "";

                    if (dataSnapshot.child("GeneralInfo").child("profileImage").exists()){
                        profileImage = dataSnapshot.child("GeneralInfo").child("profileImage").getValue().toString();
                    }

                    setUserInfo(mobileNumber,profileImage,createdDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setUserInfo(String mobileNumber, String profileImage, long createdDate) {
        mobileNumberTV.setText(mobileNumber);
        memberSinceTV.setText(convertDateMillisToString(createdDate));
        if (!profileImage.equals("")){
            Glide.with(getApplicationContext()).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.profile)).load(profileImage).into(profileImageCIV);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.logout_menu_id) {

            logOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        firebaseAuth.signOut();

        startActivity(new Intent(this,LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay,R.anim.slide_out_left);
    }

    public void backBtnTap(View view) {
        onBackPressed();
    }






    // -----------------------Start profile image code---------------------------
    public void addProfileImage(View view) {
        selectImageBottomSheet = new SelectImageBottomSheet();
        selectImageBottomSheet.show(getSupportFragmentManager(),"SelectImageBottomSheet");
    }

    @Override
    public void onCameraButtonClicked() {
        selectImageBottomSheet.dismiss();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_FOR_CAMERA);
        }
    }

    @Override
    public void onGalleryButtonClicked() {
        selectImageBottomSheet.dismiss();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_FOR_GALLERY);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode== REQUEST_CODE_FOR_CAMERA && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            profileImageCIV.setImageBitmap(bitmap);
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos1);
            byte[] byteImage = baos1.toByteArray();
            progressDialog.show();
            saveImage(byteImage);
        }

        if (requestCode== REQUEST_CODE_FOR_GALLERY && resultCode == RESULT_OK){
            Uri uri = data.getData();
            profileImageCIV.setImageURI(uri);
            progressDialog.show();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                float originalWidth= bitmap.getWidth();
                float originalHeight = bitmap.getHeight();
                if(originalWidth>1200 && originalHeight>=originalWidth){

                    float destWidth = 1200;
                    float destHeight = originalHeight/(originalWidth/destWidth);
                    Bitmap bitmap1= Bitmap.createScaledBitmap(bitmap,Math.round(destWidth),Math.round(destHeight),false);
                    ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                    bitmap1.compress(Bitmap.CompressFormat.JPEG,50,baos1);
                    byte[] byteImage = baos1.toByteArray();
                    saveImage(byteImage);


                }else if (originalWidth>1200 && originalHeight<originalWidth){
                    float destWidth = 1400;
                    float destHeight = originalHeight/(originalWidth/destWidth);
                    Bitmap bitmap1= Bitmap.createScaledBitmap(bitmap,Math.round(destWidth),Math.round(destHeight),false);
                    ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                    bitmap1.compress(Bitmap.CompressFormat.JPEG,50,baos1);
                    byte[] byteImage = baos1.toByteArray();
                    saveImage(byteImage);

                }else {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos); //decodedFoodBitmap is the bitmap object
                    byte[] byteImage = baos.toByteArray();
                    saveImage(byteImage);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void saveImage(byte[] byteImage) {
        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("ProfileImage").child(imageUrlMaker());
        filepath.putBytes(byteImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String photoUrl = uri.toString();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("profileImage",photoUrl);
                        databaseReference.child("UserList").child(userId).child("GeneralInfo").updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CustomToast.successToast("Image uploaded successfully",ProfileActivity.this);
                            }
                        });

                    }
                });
            }
        });
    }

    public String imageUrlMaker() {
        long time = System.currentTimeMillis();
        String millis = Long.toString(time);
        String url = millis;
        return url;
    }
    // -----------------------end profile image code---------------------------


    private String convertDateMillisToString(long millisDate){
        return SDF.format(millisDate);
    }


}
