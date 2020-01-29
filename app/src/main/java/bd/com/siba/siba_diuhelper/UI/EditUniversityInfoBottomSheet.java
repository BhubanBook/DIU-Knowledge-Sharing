package bd.com.siba.siba_diuhelper.UI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import bd.com.siba.siba_diuhelper.Model.UniversityInfo;
import bd.com.siba.siba_diuhelper.OtherClass.CustomToast;
import bd.com.siba.siba_diuhelper.R;

public class EditUniversityInfoBottomSheet extends BottomSheetDialogFragment {

    private AutoCompleteTextView deptATV;
    private EditText rollET, batchET, registrationET;
    private Button saveBtn;

    private String[] deptList = {"Business Administration","Civil Engineering","Law","CSE","English","EETE","Sociology","Pharmacy"};

    private String userID;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_edit_university_info,container,false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        initialize(view);

        getUserUniversityInfoFromDB();

        return view;
    }
    private void initialize(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        userID = firebaseAuth.getCurrentUser().getUid();


        deptATV = view.findViewById(R.id.deptACTV);
        rollET = view.findViewById(R.id.rollNumberET);
        batchET = view.findViewById(R.id.batchNumberET);
        registrationET = view.findViewById(R.id.registrationNumberET);
        saveBtn = view.findViewById(R.id.saveBtn);

        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,deptList);
        deptATV.setThreshold(1);
        deptATV.setAdapter(deptAdapter);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dept = deptATV.getText().toString();
                String roll = rollET.getText().toString();
                String batch = batchET.getText().toString();
                String registration = registrationET.getText().toString();

                if (dept.equals("") || roll.equals("") || batch.equals("") || registration.equals("")){
                    CustomToast.errorToast("All the fields are required", getActivity());
                }else {
                    saveUniversityInfoToDB(dept,roll,batch,registration);
                }

            }
        });

    }

    private void saveUniversityInfoToDB(String dept, String roll, String batch, String registration) {
        Map<String,Object> universityInfoMap = new HashMap<>();
        universityInfoMap.put("dept",dept);
        universityInfoMap.put("roll",roll);
        universityInfoMap.put("batch",batch);
        universityInfoMap.put("registration",registration);

        databaseReference.child("UserList").child(userID).child("UniversityInfo").setValue(universityInfoMap).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dismiss();
                    }
                }
        );
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
        deptATV.setText(universityInfo.getDept());
        rollET.setText(universityInfo.getRoll());
        batchET.setText(universityInfo.getBatch());
        registrationET.setText(universityInfo.getRegistration());
    }
}
