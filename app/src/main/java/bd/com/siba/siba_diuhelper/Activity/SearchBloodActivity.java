package bd.com.siba.siba_diuhelper.Activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import bd.com.siba.siba_diuhelper.CustomAdapter.BloodDonorAdapter;
import bd.com.siba.siba_diuhelper.Model.BloodDonor;
import bd.com.siba.siba_diuhelper.R;

public class SearchBloodActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private DatabaseReference databaseReference;

    private RecyclerView bloodDonorRecyclerView;
    private TextView noDataTV;
    private ProgressBar progressBar;

    private List<BloodDonor> bloodDonorList;

    private BloodDonorAdapter bloodDonorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_blood);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        initialize();

        getBloodDonorFromDB();

        bloodDonorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bloodDonorAdapter = new BloodDonorAdapter(bloodDonorList,this,this);
        bloodDonorRecyclerView.setAdapter(bloodDonorAdapter);
    }


    private void initialize() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        bloodDonorRecyclerView = findViewById(R.id.bloodDonorRecyclerView);
        noDataTV = findViewById(R.id.noDataTV);
        noDataTV.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);

        bloodDonorList = new ArrayList<>();
    }


    private void getBloodDonorFromDB() {
        Query userDB = databaseReference.child("UserList").orderByChild("isBloodDonor").equalTo(true);
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isFound = false;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.child("GeneralInfo").exists() && data.child("UniversityInfo").exists()) {
                            BloodDonor bloodDonor = data.getValue(BloodDonor.class);
                            if (data.child("GeneralInfo").child("profileImage").exists()){
                                bloodDonor.setProfileImage(data.child("GeneralInfo").child("profileImage").getValue().toString());
                            }else {
                                bloodDonor.setProfileImage("");
                            }
                            if (data.child("AddressInfo").child("PresentAddress").exists()){
                                bloodDonor.setLocation(data.child("AddressInfo").child("PresentAddress").child("address").getValue().toString());
                            }else {
                                bloodDonor.setLocation("");
                            }

                            bloodDonorList.add(bloodDonor);
                            isFound = true;
                            bloodDonorAdapter.notifyDataSetChanged();
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                    if (isFound == true) {
                        bloodDonorRecyclerView.setVisibility(View.VISIBLE);
                        noDataTV.setVisibility(View.GONE);
                    } else if (isFound == false) {
                        bloodDonorRecyclerView.setVisibility(View.GONE);
                        noDataTV.setVisibility(View.VISIBLE);
                    }

                } else {
                    bloodDonorRecyclerView.setVisibility(View.GONE);
                    noDataTV.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)MenuItemCompat.getActionView(menuItem);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(this);

        return true;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_left);
    }

    public void backBtnTap(View view) {
        onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();

        List<BloodDonor> newBloodDonorList = new ArrayList<>();
        for (BloodDonor bloodDonor: bloodDonorList){
            String name = bloodDonor.getGeneralInfo().getName().toLowerCase();
            String bloodGroup = bloodDonor.getGeneralInfo().getBloodGroup().toLowerCase();
            String batch = bloodDonor.getUniversityInfo().getBatch().toLowerCase();
            String dept = bloodDonor.getUniversityInfo().getDept().toLowerCase();
            String location = bloodDonor.getLocation().toLowerCase();

            if (name.contains(newText)
                    || bloodGroup.contains(newText)
                    || batch.contains(newText)
                    || dept.contains(newText)
                    || location.contains(newText)){
                newBloodDonorList.add(bloodDonor);
            }

        }

        bloodDonorAdapter.setFilter(newBloodDonorList);

        return true;
    }
}
