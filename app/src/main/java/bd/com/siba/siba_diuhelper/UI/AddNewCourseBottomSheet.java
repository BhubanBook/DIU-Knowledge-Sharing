package bd.com.siba.siba_diuhelper.UI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.siba.siba_diuhelper.OtherClass.CoursesInfo;
import bd.com.siba.siba_diuhelper.OtherClass.CustomToast;
import bd.com.siba.siba_diuhelper.R;

public class AddNewCourseBottomSheet extends BottomSheetDialogFragment {

    private AutoCompleteTextView courseNameATV, courseCodeATV;
    private RadioButton highLevelRB, mediumLevelRB, lowLevelRB;
    private Button saveBtn;

    private CoursesInfo coursesInfo;

    private String userId;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private boolean isAlreadyExits= false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_new_course,container,false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        initialize(view);

        return view;
    }
    private void initialize(View view) {

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();

        coursesInfo = new CoursesInfo();


        courseNameATV = view.findViewById(R.id.courseNameACTV);
        courseCodeATV = view.findViewById(R.id.courseCodeACTV);
        highLevelRB = view.findViewById(R.id.highLevelRB);
        mediumLevelRB = view.findViewById(R.id.mediumLevelRB);
        lowLevelRB = view.findViewById(R.id.lowLevelRB);
        saveBtn = view.findViewById(R.id.saveBtn);


        ArrayAdapter<String> courseNameAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,coursesInfo.courseNameList);
        courseNameATV.setThreshold(1);
        courseNameATV.setAdapter(courseNameAdapter);
        courseNameATV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = coursesInfo.courseNameList.indexOf(courseNameATV.getText().toString());
                courseCodeATV.setText(coursesInfo.courseCodeList[index]);
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = courseNameATV.getText().toString();
                String courseCode = courseCodeATV.getText().toString();
                String expertiseLevel = "Medium";

                if (highLevelRB.isChecked()){
                    expertiseLevel = "High";
                }else if (mediumLevelRB.isChecked()){
                    expertiseLevel = "Medium";
                }else if (lowLevelRB.isChecked()){
                    expertiseLevel = "Low";
                }

                if (courseName.equals("") || courseCode.equals("") || expertiseLevel.equals("")){
                    CustomToast.errorToast("All the fields are required",getActivity());
                }else {
                    checkCourseToDB(courseName,courseCode,expertiseLevel);
                }

            }
        });

    }

    private void checkCourseToDB(final String courseName, final String courseCode, final String expertiseLevel) {

        isAlreadyExits = false;

        databaseReference.child("UserList").child(userId).child("CourseList").orderByChild("courseCode").equalTo(courseCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        isAlreadyExits = true;
                    }
                    if (isAlreadyExits==false){
                        saveCourseToDB(courseName, courseCode, expertiseLevel);
                    }else {
                        CustomToast.errorToast("You already added this course",getActivity());
                        courseNameATV.setText("");
                        courseCodeATV.setText("");
                    }
                }else {
                    saveCourseToDB(courseName, courseCode, expertiseLevel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void saveCourseToDB(String courseName, String courseCode, String expertiseLevel) {
        String courseId = databaseReference.child("UserList").child(userId).child("CourseList").push().getKey();
        Map<String,Object> courseMap = new HashMap<>();
        courseMap.put("courseName",courseName);
        courseMap.put("courseCode",courseCode);
        courseMap.put("expertiseLevel",expertiseLevel);
        courseMap.put("courseId",courseId);

        databaseReference.child("UserList").child(userId).child("CourseList").child(courseId).setValue(courseMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                CustomToast.successToast("New course added successfully",getActivity());
                dismiss();
            }
        });
    }


}
