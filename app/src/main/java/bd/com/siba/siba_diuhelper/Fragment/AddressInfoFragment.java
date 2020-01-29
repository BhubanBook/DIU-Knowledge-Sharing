package bd.com.siba.siba_diuhelper.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

import bd.com.siba.siba_diuhelper.Activity.PickLocationMapActivity;
import bd.com.siba.siba_diuhelper.OtherClass.LocationType;
import bd.com.siba.siba_diuhelper.R;

public class AddressInfoFragment extends Fragment {


    private ImageView presentAddressPickerIV, permanentAddressPickerIV;
    private TextView presentAddressTV, permanentAddressTV;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String userID;

    public AddressInfoFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_address_info, container, false);

        initialize(view);

        getUserAddressInfoFromDB();

        return view;
    }



    private void initialize(View view) {

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid();

        presentAddressPickerIV = view.findViewById(R.id.presentAddressPickerIVID);
        presentAddressTV = view.findViewById(R.id.presentAddressTV);

        permanentAddressPickerIV = view.findViewById(R.id.permanentAddressPickerIVID);
        permanentAddressTV = view.findViewById(R.id.permanentAddressTV);

        presentAddressPickerIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),PickLocationMapActivity.class)
                        .putExtra("locationType",LocationType.PRESENT));
                getActivity().overridePendingTransition(R.anim.slide_in_up,R.anim.stay);
            }
        });

        permanentAddressPickerIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(),PickLocationMapActivity.class)
                        .putExtra("locationType",LocationType.PERMANENT));
                getActivity().overridePendingTransition(R.anim.slide_in_up,R.anim.stay);
            }
        });

    }


    private void getUserAddressInfoFromDB() {

        DatabaseReference addressInfoDB = databaseReference.child("UserList").child(userID).child("AddressInfo");
        addressInfoDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("PermanentAddress").exists()){
                        String permanentAddress = dataSnapshot.child("PermanentAddress").child("address").getValue().toString();
                        setAddressInfo(permanentAddress,permanentAddressTV);
                    }

                    if (dataSnapshot.child("PresentAddress").exists()){
                        String presentAddress = dataSnapshot.child("PresentAddress").child("address").getValue().toString();
                        setAddressInfo(presentAddress,presentAddressTV);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setAddressInfo(String address, TextView addressTV) {
        addressTV.setText(address);
    }

}
