package bd.com.siba.siba_diuhelper.OtherClass;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import bd.com.siba.siba_diuhelper.R;


public class CustomToast {

    public static void errorToast (String text, Activity activity){
        LayoutInflater layoutInflater=activity.getLayoutInflater();
        View layout=layoutInflater.inflate(R.layout.toast_error,(ViewGroup)activity.findViewById(R.id.errorToastID));
        TextView textView=layout.findViewById(R.id.toast_text_id);
        textView.setText(text);
        Toast toast=new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM,0,30);
        toast.setView(layout);
        toast.show();
    }

    public static void successToast (String text, Activity activity){
        LayoutInflater layoutInflater=activity.getLayoutInflater();
        View layout=layoutInflater.inflate(R.layout.toast_success,(ViewGroup)activity.findViewById(R.id.successToastID));
        TextView textView=layout.findViewById(R.id.toast_text_id);
        textView.setText(text);
        Toast toast=new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM,0,30);
        toast.setView(layout);
        toast.show();
    }
}
