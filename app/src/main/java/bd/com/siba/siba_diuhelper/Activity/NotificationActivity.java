package bd.com.siba.siba_diuhelper.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import bd.com.siba.siba_diuhelper.CustomAdapter.NotificationAdapter;
import bd.com.siba.siba_diuhelper.Model.Notification;
import bd.com.siba.siba_diuhelper.R;

public class NotificationActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String userId;
    private List<Notification> notificationList;

    private RecyclerView notificationRecyclerView;
    private TextView noDataTV;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initialize();
        getNotificationFromDB();

        notificationRecyclerView.setAdapter(notificationAdapter);
    }



    private void initialize() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();
        notificationList = new ArrayList<>();

        noDataTV = findViewById(R.id.noDataTV);
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationAdapter = new NotificationAdapter(notificationList);

    }


    private void getNotificationFromDB() {
        DatabaseReference notificationDB = databaseReference.child("AdminSection").child("Notification");
        notificationDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    notificationList.clear();
                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        Notification notification = data.getValue(Notification.class);
                        if (notification.getValidity()>System.currentTimeMillis()){
                            notificationList.add(notification);
                            notificationAdapter.notifyDataSetChanged();
                        }
                    }
                    if (notificationList.size()>0){
                        noDataTV.setVisibility(View.GONE);
                    }else {
                        noDataTV.setVisibility(View.VISIBLE);
                    }

                }else {
                    noDataTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
