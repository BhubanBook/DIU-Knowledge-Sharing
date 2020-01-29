package bd.com.siba.siba_diuhelper.CustomAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import bd.com.siba.siba_diuhelper.Model.BloodDonor;
import bd.com.siba.siba_diuhelper.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class BloodDonorAdapter extends RecyclerView.Adapter<BloodDonorAdapter.ViewHolder> {

    private List<BloodDonor> bloodDonorList;
    private Context context;
    private Activity activity;

    public BloodDonorAdapter(List<BloodDonor> bloodDonorList, Context context, Activity activity) {
        this.bloodDonorList = bloodDonorList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.model_blood_donor, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final BloodDonor bloodDonor = bloodDonorList.get(i);

        viewHolder.bloodGroupTV.setText(bloodDonor.getGeneralInfo().getBloodGroup());
        if (!bloodDonor.getProfileImage().equals("")) {
            Glide.with(activity.getApplicationContext()).applyDefaultRequestOptions(new RequestOptions()
                    .placeholder(R.drawable.profile)).load(bloodDonor.getProfileImage()).into(viewHolder.profileImage);
        }
        viewHolder.batchDeptTVID.setText(bloodDonor.getUniversityInfo().getBatch()+" Batch, "+bloodDonor.getUniversityInfo().getDept()+" Dept.");

        if (!bloodDonor.getLocation().equals("")){
            viewHolder.locationTV.setText(bloodDonor.getLocation());
        }else {
            viewHolder.locationTV.setText("No address");
        }
        viewHolder.nameTV.setText(bloodDonor.getGeneralInfo().getName());

        viewHolder.callBtnIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+bloodDonor.getPhoneNumber()));
                activity.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return bloodDonorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImage;
        private TextView nameTV, batchDeptTVID, locationTV, bloodGroupTV;
        private ImageView callBtnIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImageID);
            nameTV = itemView.findViewById(R.id.nameTV);
            batchDeptTVID = itemView.findViewById(R.id.batchDeptTVID);
            locationTV = itemView.findViewById(R.id.locationTV);
            bloodGroupTV = itemView.findViewById(R.id.bloodGroupTV);
            callBtnIV = itemView.findViewById(R.id.callBtnIV);
        }
    }

    public void setFilter(List<BloodDonor> newBloodDonorList){
        bloodDonorList = new ArrayList<>();
        bloodDonorList.addAll(newBloodDonorList);
        notifyDataSetChanged();
    }
}
