package bd.com.siba.siba_diuhelper.UI;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import bd.com.siba.siba_diuhelper.Model.GeneralInfo;
import bd.com.siba.siba_diuhelper.OtherClass.CustomToast;
import bd.com.siba.siba_diuhelper.OtherClass.EmailMatcher;
import bd.com.siba.siba_diuhelper.R;

public class EditGeneralInfoBottomSheet extends BottomSheetDialogFragment {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-yyyy");
    private EditText nameET, emailET;
    private AutoCompleteTextView genderATV, bloodGroupATV;
    private TextView birthDateTV;
    private Button saveBtn;
    private String[] genderList = {"Male", "Female"};
    private String[] bloodGroupList = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private long birthDate = 0;
    private String userID;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_edit_general_info, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        initialize(view);

        getUserGeneralInfoFromDB();

        return view;
    }

    private void initialize(View view) {

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid();

        nameET = view.findViewById(R.id.nameET);
        emailET = view.findViewById(R.id.emailET);
        genderATV = view.findViewById(R.id.genderACTV);
        bloodGroupATV = view.findViewById(R.id.bloodGroupACTV);
        birthDateTV = view.findViewById(R.id.birthDateTV);
        saveBtn = view.findViewById(R.id.saveBtn);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, genderList);
        genderATV.setThreshold(1);
        genderATV.setAdapter(genderAdapter);
        ArrayAdapter<String> bloodGroupAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, bloodGroupList);
        bloodGroupATV.setThreshold(1);
        bloodGroupATV.setAdapter(bloodGroupAdapter);

        birthDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameET.getText().toString();
                String gender = genderATV.getText().toString();
                String bloodGroup = bloodGroupATV.getText().toString();
                String email = emailET.getText().toString();

                if (name.equals("") || gender.equals("") || bloodGroup.equals("") || email.equals("") || birthDate == 0) {
                    CustomToast.errorToast("All the fields are required", getActivity());
                } else if (EmailMatcher.validate(email)==false) {
                    CustomToast.errorToast("Enter a valid E-mail address", getActivity());
                } else {
                    saveGeneralInfoToDB(name, gender, bloodGroup, email);
                }

            }
        });

    }

    private void saveGeneralInfoToDB(String name, String gender, String bloodGroup, String email) {
        Map<String, Object> generalInfoMap = new HashMap<>();
        generalInfoMap.put("name", name);
        generalInfoMap.put("gender", gender);
        generalInfoMap.put("bloodGroup", bloodGroup);
        generalInfoMap.put("email", email);
        generalInfoMap.put("birthDate", birthDate);

        databaseReference.child("UserList").child(userID).child("GeneralInfo").updateChildren(generalInfoMap).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dismiss();
                    }
                }
        );


    }

    private void openDatePicker() {

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                SimpleDateFormat newSDF = new SimpleDateFormat("dd-MMM-yyyy");

                month = month + 1;
                String myDate = year + "/" + month + "/" + day + " 00:00:00";
                Date date = null;
                try {
                    date = SDF.parse(myDate);
                    birthDate = date.getTime();
                    birthDateTV.setText(newSDF.format(date));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener,
                year, month, day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }


    private void getUserGeneralInfoFromDB() {
        DatabaseReference userGeneralInfoDB = databaseReference.child("UserList").child(userID).child("GeneralInfo");
        userGeneralInfoDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("name").exists()) {
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
        nameET.setText(generalInfo.getName());
        genderATV.setText(generalInfo.getGender());
        bloodGroupATV.setText(generalInfo.getBloodGroup());
        emailET.setText(generalInfo.getEmail());
        birthDateTV.setText(convertDateMillisToString(generalInfo.getBirthDate()));
        birthDate = generalInfo.getBirthDate();
    }


    private String convertDateMillisToString(long millisDate) {
        return SDF.format(millisDate);
    }

}
