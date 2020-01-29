package bd.com.siba.siba_diuhelper.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import bd.com.siba.siba_diuhelper.CustomAdapter.MessageAdapter;
import bd.com.siba.siba_diuhelper.Model.Message;
import bd.com.siba.siba_diuhelper.R;

public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private MessageAdapter messageAdapter;

    private String userId;
    private String chatUserId;
    private String chatUserName;

    private TextView chatUserNameTV, noDataTV;
    private EditText messageET;
    private ImageView sendIV;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private List<Message> messageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getIntent().getExtras()!=null){
            chatUserName=getIntent().getStringExtra("name");
            chatUserId=getIntent().getStringExtra("chatUserId");
        }
        init();

        if (chatUserName!=null){
            chatUserNameTV.setText(chatUserName);
        }

        initRecyclerView();

        getMessages();

    }

    private void getMessages() {
        noDataTV.setVisibility(View.GONE);
        Query userDB = firebaseDatabase.getReference().child("UserList").child(userId).child("MessageList").child(chatUserId);
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                if (dataSnapshot.exists()){

                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        Message message = data.getValue(Message.class);
                        messageList.add(message);
                    }

                    recyclerView.smoothScrollToPosition(messageList.size());
                    progressBar.setVisibility(View.GONE);
                    noDataTV.setVisibility(View.GONE);
                    messageAdapter.notifyDataSetChanged();
                }else {
                    progressBar.setVisibility(View.GONE);
                    noDataTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messageList,this);
        recyclerView.setAdapter(messageAdapter);
    }

    private void init() {
        messageList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.recyclerView);
        chatUserNameTV = findViewById(R.id.chatUserNameTV);
        messageET = findViewById(R.id.messageET);
        sendIV = findViewById(R.id.sendIV);
        noDataTV = findViewById(R.id.noDataTV);
        progressBar = findViewById(R.id.progressBar);
    }


    public void backBtnTap(View view) {
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition( R.anim.stay, R.anim.slide_out_up );
    }

    public void send(View view) {
        String message = messageET.getText().toString();
        if (!message.equals("")){
            sendDataToDB(message);
        }
    }

    private void sendDataToDB(String message) {
        sendIV.setEnabled(false);
        messageET.setEnabled(false);
        DatabaseReference userDB = firebaseDatabase.getReference().child("UserList").child(userId).child("MessageList").child(chatUserId);
        final String messageId = userDB.push().getKey();

        final Message currentMessage = new Message(messageId,message,System.currentTimeMillis(),userId);
        userDB.child(messageId).setValue(currentMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DatabaseReference chatUserDB = firebaseDatabase.getReference().child("UserList").child(chatUserId).child("MessageList").child(userId);
                    chatUserDB.child(messageId).setValue(currentMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                sendIV.setEnabled(true);
                                messageET.setEnabled(true);
                                messageET.setText("");
                            }else {
                                sendIV.setEnabled(true);
                                messageET.setEnabled(true);
                            }
                        }
                    });
                }else {
                    sendIV.setEnabled(true);
                    messageET.setEnabled(true);
                }
            }
        });
    }
}
