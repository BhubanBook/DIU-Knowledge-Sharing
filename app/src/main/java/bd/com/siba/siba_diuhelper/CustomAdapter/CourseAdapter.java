package bd.com.siba.siba_diuhelper.CustomAdapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import bd.com.siba.siba_diuhelper.Model.Course;
import bd.com.siba.siba_diuhelper.OtherClass.CustomToast;
import bd.com.siba.siba_diuhelper.R;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private List<Course> courses;
    private Context context;
    private Activity activity;

    private FirebaseAuth firebaseAuth;
    private String userID;


    public CourseAdapter(Context context,Activity activity, List<Course> courses) {
        this.courses = courses;
        this.context = context;
        this.activity = activity;

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.model_course_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Course course = courses.get(i);

        viewHolder.courseNameTV.setText(course.getCourseName());
        viewHolder.courseCodeTV.setText(course.getCourseCode());
        viewHolder.expertLevelTv.setText(course.getExpertiseLevel());

        viewHolder.popupMenuIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, viewHolder.popupMenuIBtn);
                //inflating menu from xml resource
                popup.inflate(R.menu.course_option_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuDelete:
                                deleteCourse(course);
                                break;

                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

    }

    private void deleteCourse(Course course) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("UserList")
                .child(userID).child("CourseList").child(course.getCourseId());
        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                CustomToast.successToast("Deleted successfully",activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView courseNameTV, courseCodeTV, expertLevelTv;
        private ImageButton popupMenuIBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameTV = itemView.findViewById(R.id.courseNameTV);
            courseCodeTV = itemView.findViewById(R.id.courseCodeTV);
            expertLevelTv = itemView.findViewById(R.id.expertLevelTv);
            popupMenuIBtn = itemView.findViewById(R.id.popupMenuIBtn);
        }
    }
}
