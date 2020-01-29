package bd.com.siba.siba_diuhelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bd.com.siba.siba_diuhelper.Activity.MoreActivity;
import bd.com.siba.siba_diuhelper.Fragment.CoursesFragment;
import bd.com.siba.siba_diuhelper.Fragment.HomeFragment;
import bd.com.siba.siba_diuhelper.Fragment.MyActivityFragment;
import bd.com.siba.siba_diuhelper.OtherClass.CustomToast;
import bd.com.siba.siba_diuhelper.OtherClass.Internet;
import bd.com.siba.siba_diuhelper.OtherClass.Status;
import bd.com.siba.siba_diuhelper.UI.BottomNavigationViewHelper;

public class HomeActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private HomeFragment homeFragment;
    private MyActivityFragment myActivityFragment;
    private CoursesFragment coursesFragment;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userID;

    private BottomNavigationView navigation;

    private Internet internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        initialize();

        loadPendingRequestFromDB();

        replaceFragment(homeFragment);

        navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (internet.isConnected()==false){
            internet.showDialogForInternet();
        }
    }

    private void initialize() {

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        internet = new Internet(this,this);
        homeFragment = new HomeFragment();
        myActivityFragment = new MyActivityFragment();
        coursesFragment = new CoursesFragment();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    replaceFragment(homeFragment);
                    return true;
                case R.id.navigation_activity:

                    removeBadge(navigation,R.id.navigation_activity);
                    replaceFragment(myActivityFragment);
                    return true;
                case R.id.navigation_courses:
                    replaceFragment(coursesFragment);
                    return true;

                case R.id.navigation_more:

                    goToMore();
                    return true;

            }
            return false;
        }
    };

    private void replaceFragment(Fragment fragment) {

        FragmentManager fm =getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayoutId,fragment);
        ft.commit();
    }

    private void goToMore() {
        Intent intent = new Intent(HomeActivity.this,MoreActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
    }


    @Override
    public void onBackPressed() {
       /* if (doubleBackToExitPressedOnce){
            super.onBackPressed();
            this.finish();
        }

        this.doubleBackToExitPressedOnce = true;
        CustomToast.errorToast("Please press again to exit",this);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);*/

       super.onBackPressed();

    }


    private void loadPendingRequestFromDB() {
        databaseReference.child("UserList").child(userID).child("RequestList").child("ReceivedList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int requestCounter = 0;
                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        if (data.child("status").getValue().toString().equals(Status.PENDING)){
                            requestCounter++;
                        }

                    }if (requestCounter>9){
                        showBadge(HomeActivity.this, navigation,R.id.navigation_activity,"9+");
                    }
                    else if (requestCounter>0){
                        showBadge(HomeActivity.this, navigation,R.id.navigation_activity,String.valueOf(requestCounter));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static void showBadge(Context context, BottomNavigationView
            bottomNavigationView, @IdRes int itemId, String value) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        View badge = LayoutInflater.from(context).inflate(R.layout.notification_badge, bottomNavigationView, false);

        TextView text = badge.findViewById(R.id.badge_text_view);
        text.setText(value);
        itemView.addView(badge);

    }

    public static void removeBadge(BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);

        if (itemView.getChildCount() == 3) {
            itemView.removeViewAt(2);
        }
    }

}
