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
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import bd.com.siba.siba_diuhelper.Model.Address;
import bd.com.siba.siba_diuhelper.Model.Course;
import bd.com.siba.siba_diuhelper.Model.RequestReceived;
import bd.com.siba.siba_diuhelper.Model.RequestSent;
import bd.com.siba.siba_diuhelper.Model.User;
import bd.com.siba.siba_diuhelper.OtherClass.CustomToast;
import bd.com.siba.siba_diuhelper.OtherClass.Status;
import bd.com.siba.siba_diuhelper.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class SendRequestBottomSheet extends BottomSheetDialogFragment {

    private String userId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String selectedUserID;


    private CircleImageView profileImageCIV;
    private TextView nameTV, batchDeptTV, ratingCountTV, expertInTV;
    private RatingBar ratingRB;
    private Button sendRequestBtn;
    private AutoCompleteTextView selectedCourseACTV;

    private List<Course> courseList;
    private List<String> courseNameList;

    private User user, selectedUser;

    private boolean isUserAddressExits = true;
    private boolean isUserProfileInfoExits = true;
    private boolean isDataLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_send_request, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        if (getArguments() != null) {
            selectedUserID = getArguments().getString("selectedUserID");
        } else {
            selectedUserID = "";
        }

        initialize(view);

        if (!selectedUserID.equals("")) {
            getSelectedUserInfoFromDB();
        }

        getUserInfo();

        ArrayAdapter<String> courseNameAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, courseNameList);
        selectedCourseACTV.setThreshold(1);
        selectedCourseACTV.setAdapter(courseNameAdapter);

        return view;
    }


    private void initialize(View view) {

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();

        courseList = new ArrayList<>();
        courseNameList = new ArrayList<>();

        profileImageCIV = view.findViewById(R.id.profileImageCIV);
        nameTV = view.findViewById(R.id.nameTV);
        batchDeptTV = view.findViewById(R.id.batchDeptTV);
        ratingCountTV = view.findViewById(R.id.ratingCountTV);
        ratingRB = view.findViewById(R.id.ratingRB);
        sendRequestBtn = view.findViewById(R.id.sendRequestBtn);
        expertInTV = view.findViewById(R.id.expertInTV);
        selectedCourseACTV = view.findViewById(R.id.selectedCourseACTV);


        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataLoaded == true) {
                    if (isUserAddressExits == true && isUserProfileInfoExits == true) {
                        sendRequestBtn.setEnabled(false);
                        checkRequest();
                    } else {
                        CustomToast.errorToast("Please update your profile", getActivity());
                    }
                } else {
                    CustomToast.errorToast("Slow connection", getActivity());
                }

            }
        });

    }

    private void checkRequest() {
        final String courseName = selectedCourseACTV.getText().toString();
        if (!courseName.equals("")) {
            boolean isCourseMatch = false;

            for (String course : courseNameList) {
                if (course.contains(courseName)) {
                    isCourseMatch = true;
                    break;
                }
            }

            if (isCourseMatch == true) {


                final DatabaseReference selectedUserDB = databaseReference.child("UserList").child(selectedUserID);
                final DatabaseReference userDB = databaseReference.child("UserList").child(userId);

                selectedUserDB.child("RequestList").child("ReceivedList").orderByChild("senderId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            boolean isPendingRequest = false;

                            for (DataSnapshot requestData : dataSnapshot.getChildren()) {
                                String status = requestData.child("status").getValue().toString();
                                if (status.contains(Status.PENDING)){
                                    isPendingRequest = true;
                                    break;
                                }
                            }
                            if (isPendingRequest==true){
                                CustomToast.errorToast("You already have a pending request to this user.",getActivity());
                                sendRequestBtn.setEnabled(true);
                            }else {

                                sendRequest(selectedUserDB,userDB,courseName);
                            }
                        }else {
                            sendRequest(selectedUserDB,userDB,courseName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            } else {
                sendRequestBtn.setEnabled(true);
                CustomToast.errorToast("This user is not expert in this course.", getActivity());
            }


        } else {
            sendRequestBtn.setEnabled(true);
            CustomToast.errorToast("Please enter a course", getActivity());
        }


    }

    private void sendRequest(DatabaseReference selectedUserDB, final DatabaseReference userDB, String courseName) {
        final String requestId = selectedUserDB.child("RequestList").child("ReceivedList").push().getKey();
        final RequestReceived requestReceived = new RequestReceived(selectedUserID, requestId, userId, user.getGeneralInfo(),
                user.getUniversityInfo(), user.getPresentAddress(), courseName, user.getPhoneNumber(), user.getProfileImage(), 0, 0, Status.PENDING, System.currentTimeMillis());

        final RequestSent requestSent = new RequestSent(userId, requestId, selectedUserID, selectedUser.getGeneralInfo(), selectedUser.getUniversityInfo(),
                selectedUser.getPresentAddress(), courseName, selectedUser.getPhoneNumber(), selectedUser.getProfileImage(), 0, 0, Status.PENDING, System.currentTimeMillis());

        selectedUserDB.child("RequestList").child("ReceivedList").child(requestId).setValue(requestReceived).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                userDB.child("RequestList").child("SentList").child(requestId).setValue(requestSent).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        CustomToast.successToast("Request sent successfully", getActivity());
                        dismiss();
                    }
                });

            }
        });
    }


    private void getUserInfo() {
        DatabaseReference userDB = databaseReference.child("UserList").child(userId);
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("GeneralInfo").exists()
                            && dataSnapshot.child("UniversityInfo").exists()) {

                        user = dataSnapshot.getValue(User.class);
                        user.setPhoneNumber(dataSnapshot.child("phoneNumber").getValue().toString());

                        if (dataSnapshot.child("AddressInfo").child("PresentAddress").exists()) {
                            user.setPresentAddress(dataSnapshot.child("AddressInfo").child("PresentAddress").getValue(Address.class));
                        } else {
                            isUserAddressExits = false;
                        }
                        if (dataSnapshot.child("GeneralInfo").child("profileImage").exists()) {
                            user.setProfileImage(dataSnapshot.child("GeneralInfo").child("profileImage").getValue().toString());
                        } else {
                            user.setProfileImage("");
                        }

                        isDataLoaded = true;

                    } else {
                        isDataLoaded = true;
                        isUserProfileInfoExits = false;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getSelectedUserInfoFromDB() {
        DatabaseReference selectedUserDB = databaseReference.child("UserList").child(selectedUserID);
        selectedUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    selectedUser = dataSnapshot.getValue(User.class);

                    selectedUser.setPhoneNumber(dataSnapshot.child("phoneNumber").getValue().toString());
                    selectedUser.setPresentAddress(dataSnapshot.child("AddressInfo").child("PresentAddress").getValue(Address.class));

                    if (dataSnapshot.child("GeneralInfo").child("profileImage").exists()) {
                        selectedUser.setProfileImage(dataSnapshot.child("GeneralInfo").child("profileImage").getValue().toString());
                    } else {
                        selectedUser.setProfileImage("");
                    }

                    setSelectedUserInfo();

                    setSelectedUserCourseInfo(dataSnapshot);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setSelectedUserCourseInfo(DataSnapshot dataSnapshot) {

        if (dataSnapshot.child("CourseList").exists()) {
            courseList.clear();
            for (DataSnapshot data : dataSnapshot.child("CourseList").getChildren()) {
                Course course = data.getValue(Course.class);
                courseList.add(course);
            }
            String expertIn = "";
            int i = 1;
            courseNameList.clear();
            for (Course course : courseList) {

                courseNameList.add(course.getCourseName());
                if (i < courseList.size()) {
                    expertIn += course.getCourseName() + "(" + course.getExpertiseLevel() + "), ";
                    i = i + 1;
                } else if (i == courseList.size()) {
                    expertIn += course.getCourseName() + "(" + course.getExpertiseLevel() + ").";
                }

            }
            expertInTV.setText(expertIn);
        }
    }


    private void setSelectedUserInfo() {
        nameTV.setText(selectedUser.getGeneralInfo().getName());
        batchDeptTV.setText(selectedUser.getUniversityInfo().getBatch() + " Batch, " + selectedUser.getUniversityInfo().getDept() + " Dept.");
        if (!selectedUser.getProfileImage().equals("")) {
            Glide.with(getActivity().getApplicationContext()).applyDefaultRequestOptions(new RequestOptions()
                    .placeholder(R.drawable.profile)).load(selectedUser.getProfileImage()).into(profileImageCIV);
        }
    }
}
