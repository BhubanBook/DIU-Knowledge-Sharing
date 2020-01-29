package bd.com.siba.siba_diuhelper.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bd.com.siba.siba_diuhelper.Model.UniversityInfo;
import bd.com.siba.siba_diuhelper.R;
import bd.com.siba.siba_diuhelper.UI.EditUniversityInfoBottomSheet;


public class UniversityInfoFragment extends Fragment {

    private ImageView editUniversityInfoIV;
    private TextView deptTV, batchTV, rollTV, registrationTV;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userID;

    public UniversityInfoFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_university_info, container, false);

        initialize(view);

        getUserUniversityInfoFromDB();

        return view;
    }




    private void initialize(View view) {

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid();

        editUniversityInfoIV = view.findViewById(R.id.editUniversityInfoIV);
        deptTV = view.findViewById(R.id.deptTV);
        batchTV = view.findViewById(R.id.batchTV);
        rollTV = view.findViewById(R.id.rollTV);
        registrationTV = view.findViewById(R.id.registrationTV);


        editUniversityInfoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditUniversityInfoBottomSheet sendRequestBottomSheet = new EditUniversityInfoBottomSheet();
                sendRequestBottomSheet.show(((FragmentActivity)getContext()).getSupportFragmentManager(), "EditUniversityInfoBottomSheet");

            }
        });
    }


    private void getUserUniversityInfoFromDB() {
        DatabaseReference userUniversityInfoDB = databaseReference.child("UserList").child(userID).child("UniversityInfo");
        userUniversityInfoDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    UniversityInfo universityInfo = dataSnapshot.getValue(UniversityInfo.class);
                    setUniversityInfo(universityInfo);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUniversityInfo(UniversityInfo universityInfo) {
        deptTV.setText(universityInfo.getDept());
        rollTV.setText(universityInfo.getRoll());
        batchTV.setText(universityInfo.getBatch());
        registrationTV.setText(universityInfo.getRegistration());
    }

}
