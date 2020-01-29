package bd.com.siba.siba_diuhelper.OtherClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import bd.com.siba.siba_diuhelper.R;

public class Internet {
    private Context context;
    private Activity activity;

    public Internet(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void showDialogForInternet() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View mView = activity.getLayoutInflater().inflate(R.layout.dialog_internet, null);
        Button itsOk = mView.findViewById(R.id.itsOkBtnID);
        Button settings = mView.findViewById(R.id.settingsBtnID);

        builder.setView(mView);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        itsOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                dialog.dismiss();
            }
        });
    }

    public boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else return false;
    }
}
