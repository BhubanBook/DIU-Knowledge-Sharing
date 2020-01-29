package bd.com.siba.siba_diuhelper.CustomAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import bd.com.siba.siba_diuhelper.Activity.ChatActivity;
import bd.com.siba.siba_diuhelper.Activity.MapActivity;
import bd.com.siba.siba_diuhelper.Model.RequestReceived;
import bd.com.siba.siba_diuhelper.OtherClass.Status;
import bd.com.siba.siba_diuhelper.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class RequestReceivedAdapter extends RecyclerView.Adapter<RequestReceivedAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<RequestReceived> requestReceivedList;
    private DatabaseReference databaseReference;

    public RequestReceivedAdapter(Context context, Activity activity, List<RequestReceived> requestReceivedList) {
        this.context = context;
        this.activity = activity;
        this.requestReceivedList = requestReceivedList;

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.model_receive_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final RequestReceived requestReceived = requestReceivedList.get(i);

        if (!requestReceived.getSenderProfileImage().equals("")) {
            Glide.with(activity.getApplicationContext()).applyDefaultRequestOptions(new RequestOptions()
                    .placeholder(R.drawable.profile)).load(requestReceived.getSenderProfileImage()).into(viewHolder.seederProfileImageCIV);
        }

        if (requestReceived.getStatus().equals(Status.ACCEPTED)
                || requestReceived.getStatus().equals(Status.IGNORED)) {

            viewHolder.statusTV.setVisibility(View.VISIBLE);
            viewHolder.statusIconTV.setVisibility(View.VISIBLE);
            viewHolder.statusTV.setText(requestReceived.getStatus());

            if (requestReceived.getStatus().equals(Status.ACCEPTED)) {
                viewHolder.statusIconTV.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            }else{
                viewHolder.statusIconTV.setTextColor(activity.getResources().getColor(R.color.red));
            }
            viewHolder.ignoreBtnTV.setVisibility(View.GONE);
            viewHolder.acceptBtnTV.setVisibility(View.GONE);
        } else if (requestReceived.getStatus().equals(Status.PENDING)) {
            viewHolder.ignoreBtnTV.setVisibility(View.VISIBLE);
            viewHolder.acceptBtnTV.setVisibility(View.VISIBLE);
            viewHolder.statusTV.setVisibility(View.GONE);
            viewHolder.statusIconTV.setVisibility(View.GONE);
        }

        viewHolder.senderNameTV.setText(requestReceived.getSenderGeneralInfo().getName());
        viewHolder.senderBatchDeptTV.setText(requestReceived.getSenderUniversityInfo().getBatch() + " Batch, "
                + requestReceived.getSenderUniversityInfo().getDept() + " Dept.");

        viewHolder.requestedCourseTV.setText(requestReceived.getCourseName());
        viewHolder.senderRatingRB.setRating((float) requestReceived.getSenderAverageRating());
        viewHolder.senderRatingCountTV.setText("(" + String.valueOf(requestReceived.getSenderTotalRatingCounter()) + ")");

        viewHolder.callBtnIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + requestReceived.getSenderPhoneNumber()));
                activity.startActivity(intent);
            }
        });

        viewHolder.acceptBtnTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAcceptConfirmationDialog(requestReceived);
            }
        });

        viewHolder.ignoreBtnTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIgnoreConfirmationDialog(requestReceived);
            }
        });

        viewHolder.messageBtnIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("name",requestReceived.getSenderGeneralInfo().getName());
                intent.putExtra("chatUserId",requestReceived.getSenderId());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_up,R.anim.stay);
            }
        });


    }


    @Override
    public int getItemCount() {
        return requestReceivedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView seederProfileImageCIV;
        private TextView senderNameTV, senderBatchDeptTV, requestedCourseTV, senderRatingCountTV,
                ignoreBtnTV, acceptBtnTV, statusTV, statusIconTV;
        private ImageView callBtnIV, messageBtnIV;
        private RatingBar senderRatingRB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            seederProfileImageCIV = itemView.findViewById(R.id.seederProfileImageCIV);
            senderNameTV = itemView.findViewById(R.id.senderNameTV);
            senderBatchDeptTV = itemView.findViewById(R.id.senderBatchDeptTV);
            requestedCourseTV = itemView.findViewById(R.id.requestedCourseTV);
            senderRatingCountTV = itemView.findViewById(R.id.senderRatingCountTV);
            ignoreBtnTV = itemView.findViewById(R.id.ignoreBtnTV);
            acceptBtnTV = itemView.findViewById(R.id.acceptBtnTV);
            callBtnIV = itemView.findViewById(R.id.callBtnIV);
            senderRatingRB = itemView.findViewById(R.id.senderRatingRB);
            statusIconTV = itemView.findViewById(R.id.statusIconTV);
            statusTV = itemView.findViewById(R.id.statusTV);
            messageBtnIV = itemView.findViewById(R.id.messageBtnIV);
        }
    }


    private void openIgnoreConfirmationDialog(final RequestReceived requestReceived) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_confirmation);
        TextView no = dialog.findViewById(R.id.noTVID);
        TextView yes = dialog.findViewById(R.id.yesTVID);
        TextView alertMessage = dialog.findViewById(R.id.alertMessageTVID);
        alertMessage.setText("Are you sure you want to ignore this request?");
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ignoreRequest(requestReceived);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void openAcceptConfirmationDialog(final RequestReceived requestReceived) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_confirmation);
        TextView no = dialog.findViewById(R.id.noTVID);
        TextView yes = dialog.findViewById(R.id.yesTVID);
        TextView alertMessage = dialog.findViewById(R.id.alertMessageTVID);
        alertMessage.setText("Are you sure you want to accept this request?");
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRequest(requestReceived);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void ignoreRequest(RequestReceived requestReceived) {
        DatabaseReference senderDB = databaseReference.child("UserList").child(requestReceived.getSenderId())
                .child("RequestList").child("SentList").child(requestReceived.getRequestId());
        DatabaseReference receiverDB = databaseReference.child("UserList").child(requestReceived.getReceiverId())
                .child("RequestList").child("ReceivedList").child(requestReceived.getRequestId());

        senderDB.child("status").setValue(Status.IGNORED);
        receiverDB.child("status").setValue(Status.IGNORED);
    }

    private void acceptRequest(RequestReceived requestReceived) {
        DatabaseReference senderDB = databaseReference.child("UserList").child(requestReceived.getSenderId())
                .child("RequestList").child("SentList").child(requestReceived.getRequestId());
        DatabaseReference receiverDB = databaseReference.child("UserList").child(requestReceived.getReceiverId())
                .child("RequestList").child("ReceivedList").child(requestReceived.getRequestId());

        senderDB.child("status").setValue(Status.ACCEPTED);
        receiverDB.child("status").setValue(Status.ACCEPTED);
    }
}
