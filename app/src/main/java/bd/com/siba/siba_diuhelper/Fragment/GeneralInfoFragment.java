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

import java.text.SimpleDateFormat;

import bd.com.siba.siba_diuhelper.Model.GeneralInfo;
import bd.com.siba.siba_diuhelper.R;
import bd.com.siba.siba_diuhelper.UI.EditGeneralInfoBottomSheet;

public class GeneralInfoFragment extends Fragment {


    private ImageView editGeneralInfoIV;
    private TextView nameTV, genderTV, bloodTV, birthDateTV, emailTV;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userID;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-yyyy");


    public GeneralInfoFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_general_info, container, false);

        initialize(view);

        getUserGeneralInfoFromDB();

        return view;
    }


    private void initialize(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        userID = firebaseAuth.getCurrentUser().getUid();

        editGeneralInfoIV = view.findViewById(R.id.editGeneralInfoIV);
        nameTV = view.findViewById(R.id.nameTV);
        genderTV = view.findViewById(R.id.genderTV);
        bloodTV = view.findViewById(R.id.bloodTV);
        birthDateTV = view.findViewById(R.id.birthDateTV);
        emailTV = view.findViewById(R.id.emailTV);

        editGeneralInfoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditGeneralInfoBottomSheet sendRequestBottomSheet = new EditGeneralInfoBottomSheet();
                sendRequestBottomSheet.show(((FragmentActivity)getContext()).getSupportFragmentManager(), "EditGeneralInfoBottomSheet");

            }
        });
    }


    private void getUserGeneralInfoFromDB() {
        DatabaseReference userGeneralInfoDB = databaseReference.child("UserList").child(userID).child("GeneralInfo");
        userGeneralInfoDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("name").exists()){
                    GeneralInfo generalInfo = dataSnapshot.getValue(GeneralInfo.class);
                    setUserGeneralInfo(generalInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUserGeneralInfo(GeneralInfo generalInfo) {
        nameTV.setText(generalInfo.getName());
        genderTV.setText(generalInfo.getGender());
        bloodTV.setText(generalInfo.getBloodGroup());
        emailTV.setText(generalInfo.getEmail());
        birthDateTV.setText(convertDateMillisToString(generalInfo.getBirthDate()));
    }


    private String convertDateMillisToString(long millisDate){
        return SDF.format(millisDate);
    }

}
