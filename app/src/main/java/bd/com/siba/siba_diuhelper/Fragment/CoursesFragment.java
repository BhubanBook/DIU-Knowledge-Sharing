package bd.com.siba.siba_diuhelper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import bd.com.siba.siba_diuhelper.CustomAdapter.CourseAdapter;
import bd.com.siba.siba_diuhelper.Model.Course;
import bd.com.siba.siba_diuhelper.R;
import bd.com.siba.siba_diuhelper.UI.AddNewCourseBottomSheet;


public class CoursesFragment extends Fragment{

    private FloatingActionButton addNewCourseFAB;
    private RecyclerView coursesRecyclerView;
    private TextView noDataTV;
    private ProgressBar progressBar;


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private CourseAdapter courseAdapter;

    private String userID;
    private List<Course> courses;

    public CoursesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_courses, container, false);


        initialize(view);

        getUserCoursesFromDB();

        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        courseAdapter = new CourseAdapter(getActivity(),getActivity(),courses);
        coursesRecyclerView.setAdapter(courseAdapter);

        return view;
    }




    private void initialize(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        courses = new ArrayList<>();

        addNewCourseFAB = view.findViewById(R.id.addNewCourseFAB);
        coursesRecyclerView = view.findViewById(R.id.coursesRecyclerView);
        noDataTV = view.findViewById(R.id.noDataTV);
        noDataTV.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBar);

        addNewCourseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddNewCourseBottomSheet sendRequestBottomSheet = new AddNewCourseBottomSheet();
                sendRequestBottomSheet.show(((FragmentActivity)getContext()).getSupportFragmentManager(), "AddNewCourseBottomSheet");
            }
        });
    }


    private void getUserCoursesFromDB() {
        DatabaseReference userCourseDB = databaseReference.child("UserList").child(userID).child("CourseList");
        userCourseDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courses.clear();
                if (dataSnapshot.exists()){
                    coursesRecyclerView.setVisibility(View.VISIBLE);
                    noDataTV.setVisibility(View.GONE);

                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        Course course = data.getValue(Course.class);
                        courses.add(course);
                        courseAdapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                }else {
                    coursesRecyclerView.setVisibility(View.GONE);
                    noDataTV.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




}
