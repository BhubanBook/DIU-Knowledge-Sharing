package bd.com.siba.siba_diuhelper.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bd.com.siba.siba_diuhelper.CustomAdapter.RequestSentAdapter;
import bd.com.siba.siba_diuhelper.Model.RequestSent;
import bd.com.siba.siba_diuhelper.OtherClass.Status;
import bd.com.siba.siba_diuhelper.R;


public class SendFragment extends Fragment {


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String userID;
    private List<RequestSent> requestSentList;
    private RequestSentAdapter requestSentAdapter;

    private RecyclerView sentRecyclerView;
    private TextView noDataTV;
    private ProgressBar progressBar;

    public SendFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_send, container, false);

        initialize(view);

        getSentRequestDataFromDB();

        sentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestSentAdapter = new RequestSentAdapter(getActivity(), getActivity(), requestSentList);

        sentRecyclerView.setAdapter(requestSentAdapter);


        return view;
    }

    private void initialize(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid();

        requestSentList = new ArrayList<>();

        sentRecyclerView = view.findViewById(R.id.sentRecyclerView);
        noDataTV = view.findViewById(R.id.noDataTV);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void getSentRequestDataFromDB() {
        Query receivedRequestDB = databaseReference.child("UserList").child(userID)
                .child("RequestList").child("SentList").orderByChild("requestDate");

        receivedRequestDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    requestSentList.clear();
                    noDataTV.setVisibility(View.GONE);
                    sentRecyclerView.setVisibility(View.VISIBLE);

                    List<RequestSent> requestSents = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        RequestSent requestSent = data.getValue(RequestSent.class);
                        if (!requestSent.getStatus().equals(Status.CANCELLED)) {
                            requestSents.add(requestSent);
                        }

                    }

                    progressBar.setVisibility(View.GONE);
                    Collections.reverse(requestSents);
                    requestSentList.addAll(requestSents);
                    requestSentAdapter.notifyDataSetChanged();


                } else {
                    noDataTV.setVisibility(View.VISIBLE);
                    sentRecyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
