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

import bd.com.siba.siba_diuhelper.CustomAdapter.RequestReceivedAdapter;
import bd.com.siba.siba_diuhelper.Model.RequestReceived;
import bd.com.siba.siba_diuhelper.OtherClass.Status;
import bd.com.siba.siba_diuhelper.R;


public class ReceiveFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String userID;
    private List<RequestReceived> requestReceivedList;
    private RequestReceivedAdapter requestReceivedAdapter;

    private RecyclerView receivedRecyclerView;
    private TextView noDataTV;
    private ProgressBar progressBar;

    public ReceiveFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_receive, container, false);

        initialize(view);

        getReceivedRequestDataFromDB();

        receivedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestReceivedAdapter = new RequestReceivedAdapter(getActivity(),getActivity(),requestReceivedList);

        receivedRecyclerView.setAdapter(requestReceivedAdapter);


        return view;
    }



    private void initialize(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userID=firebaseAuth.getCurrentUser().getUid();

        requestReceivedList = new ArrayList<>();

        receivedRecyclerView = view.findViewById(R.id.receivedRecyclerView);
        noDataTV = view.findViewById(R.id.noDataTV);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void getReceivedRequestDataFromDB() {
        Query receivedRequestDB = databaseReference.child("UserList").child(userID)
                .child("RequestList").child("ReceivedList").orderByChild("requestDate");

        receivedRequestDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    requestReceivedList.clear();
                    noDataTV.setVisibility(View.GONE);
                    receivedRecyclerView.setVisibility(View.VISIBLE);

                    List<RequestReceived> requestReceiveds = new ArrayList<>();
                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        RequestReceived requestReceived = data.getValue(RequestReceived.class);
                        if (!requestReceived.getStatus().equals(Status.CANCELLED)) {
                            requestReceiveds.add(requestReceived);
                        }

                    }

                    progressBar.setVisibility(View.GONE);
                    Collections.reverse(requestReceiveds);
                    requestReceivedList.addAll(requestReceiveds);
                    requestReceivedAdapter.notifyDataSetChanged();


                }else {
                    noDataTV.setVisibility(View.VISIBLE);
                    receivedRecyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
