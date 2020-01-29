package bd.com.siba.siba_diuhelper.CustomAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import bd.com.siba.siba_diuhelper.Model.Message;
import bd.com.siba.siba_diuhelper.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messageList;
    private Context context;
    private String userId;
    private FirebaseAuth firebaseAuth;

    public MessageAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.model_message_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Message message = messageList.get(i);

        if (message.getSenderId().equals(userId)){
            viewHolder.messageTV.setText(message.getMessage());

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.messageTV.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(80,5,0,0);

            viewHolder.messageTV.setLayoutParams(params);
            viewHolder.messageTV.setBackground(context.getResources().getDrawable(R.drawable.corner_radius));
            viewHolder.messageTV.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            viewHolder.messageTV.setText(message.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTV;
        public ViewHolder(View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.messageTV);
        }
    }
}
