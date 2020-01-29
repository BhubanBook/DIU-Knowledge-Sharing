package bd.com.siba.siba_diuhelper.OtherClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;

import bd.com.siba.siba_diuhelper.R;

public class CustomProgressDialog {

    //custom progress dialog
    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_progress_bar);
        //dialog.setMessage("Please wait..");
        return dialog;
    }
}
