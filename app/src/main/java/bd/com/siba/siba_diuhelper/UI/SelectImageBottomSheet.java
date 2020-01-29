package bd.com.siba.siba_diuhelper.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import bd.com.siba.siba_diuhelper.R;


public class SelectImageBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    LinearLayout camera,gallery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_image_picker,container,false);
        camera = view.findViewById(R.id.cameraImageLayoutID);
        gallery = view.findViewById(R.id.galleryImageLayoutID);
        onClick();

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    private void onClick() {
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCameraButtonClicked();

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onGalleryButtonClicked();
            }
        });
    }
    public interface BottomSheetListener {
        void onCameraButtonClicked();
        void onGalleryButtonClicked();
    }
}
