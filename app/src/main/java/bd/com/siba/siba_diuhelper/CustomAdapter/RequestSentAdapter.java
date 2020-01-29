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
import bd.com.siba.siba_diuhelper.Model.RequestSent;
import bd.com.siba.siba_diuhelper.OtherClass.CustomToast;
import bd.com.siba.siba_diuhelper.OtherClass.Status;
import bd.com.siba.siba_diuhelper.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class RequestSentAdapter extends RecyclerView.Adapter<RequestSentAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<RequestSent> requestSentList;
    private DatabaseReference databaseReference;

    public RequestSentAdapter(Context context, Activity activity, List<RequestSent> requestSentList) {
        this.context = context;
        this.activity = activity;
        this.requestSentList = requestSentList;

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.model_sent_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final RequestSent requestSent = requestSentList.get(i);

        if (!requestSent.getReceiverProfileImage().equals("")) {
            Glide.with(activity.getApplicationContext()).applyDefaultRequestOptions(new RequestOptions()
                    .placeholder(R.drawable.profile)).load(requestSent.getReceiverProfileImage()).into(viewHolder.receiverProfileImageCIV);
        }

        viewHolder.receiverNameTV.setText(requestSent.getReceiverGeneralInfo().getName());
        viewHolder.receiverBatchDeptTV.setText(requestSent.getReceiverUniversityInfo().getBatch() + " Batch, "
                + requestSent.getReceiverUniversityInfo().getDept() + " Dept.");
        viewHolder.requestedCourseTV.setText(requestSent.getCourseName());
        viewHolder.receiverRatingRB.setRating((float) requestSent.getReceiverAverageRating());
        viewHolder.receiverRatingCountTV.setText("(" + requestSent.getReceiverTotalRatingCounter() + ")");

        if (requestSent.getStatus().equals(Status.ACCEPTED)
                || requestSent.getStatus().equals(Status.IGNORED)) {

            viewHolder.statusTV.setText(requestSent.getStatus());

            if (requestSent.getStatus().equals(Status.ACCEPTED)) {
                viewHolder.statusIconTV.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            } else {
                viewHolder.statusIconTV.setTextColor(activity.getResources().getColor(R.color.red));
            }
            viewHolder.cancelBtnTV.setVisibility(View.GONE);
        } else if (requestSent.getStatus().equals(Status.PENDING)) {
            viewHolder.cancelBtnTV.setVisibility(View.VISIBLE);

            viewHolder.statusTV.setText(requestSent.getStatus());
            viewHolder.statusIconTV.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        }

        viewHolder.messageBtnIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("name",requestSent.getReceiverGeneralInfo().getName());
                intent.putExtra("chatUserId",requestSent.getReceiverId());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            }
        });

        viewHolder.callBtnIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestSent.getStatus().equals(Status.ACCEPTED)) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + requestSent.getReceiverPhoneNumber()));
                    activity.startActivity(intent);
                } else if (requestSent.getStatus().equals(Status.PENDING)) {
                    CustomToast.errorToast("Your request is Pending.", activity);
                } else if (requestSent.getStatus().equals(Status.IGNORED)) {
                    CustomToast.errorToast("Your request is Ignored by the user.", activity);
                }

            }
        });

        viewHolder.cancelBtnTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCancelConfirmationDialog(requestSent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return requestSentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView receiverProfileImageCIV;
        private TextView receiverNameTV, receiverBatchDeptTV, statusTV, statusIconTV, requestedCourseTV,
                receiverRatingCountTV, cancelBtnTV;
        private ImageView messageBtnIV, callBtnIV;
        private RatingBar receiverRatingRB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverProfileImageCIV = itemView.findViewById(R.id.receiverProfileImageCIV);
            receiverNameTV = itemView.findViewById(R.id.receiverNameTV);
            receiverBatchDeptTV = itemView.findViewById(R.id.receiverBatchDeptTV);
            statusTV = itemView.findViewById(R.id.statusTV);
            statusIconTV = itemView.findViewById(R.id.statusIconTV);
            requestedCourseTV = itemView.findViewById(R.id.requestedCourseTV);
            receiverRatingRB = itemView.findViewById(R.id.receiverRatingRB);
            receiverRatingCountTV = itemView.findViewById(R.id.receiverRatingCountTV);
            cancelBtnTV = itemView.findViewById(R.id.cancelBtnTV);
            messageBtnIV = itemView.findViewById(R.id.messageBtnIV);
            callBtnIV = itemView.findViewById(R.id.callBtnIV);
        }
    }


    private void openCancelConfirmationDialog(final RequestSent requestSent) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_confirmation);
        TextView no = dialog.findViewById(R.id.noTVID);
        TextView yes = dialog.findViewById(R.id.yesTVID);
        TextView alertMessage = dialog.findViewById(R.id.alertMessageTVID);
        alertMessage.setText("Are you sure you want to cancel this request?");
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequest(requestSent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void cancelRequest(RequestSent requestSent) {
        DatabaseReference senderDB = databaseReference.child("UserList").child(requestSent.getSenderId())
                .child("RequestList").child("SentList").child(requestSent.getRequestId());
        DatabaseReference receiverDB = databaseReference.child("UserList").child(requestSent.getReceiverId())
                .child("RequestList").child("ReceivedList").child(requestSent.getRequestId());

        senderDB.child("status").setValue(Status.CANCELLED);
        receiverDB.child("status").setValue(Status.CANCELLED);
    }
}
