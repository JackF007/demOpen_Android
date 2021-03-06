package com.gaicomo.app.Base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.gaicomo.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    public static final int PICK_FROM_CAMERA = 3, PICK_FROM_FILE = 2;
    public static final int MEDIA_TYPE_IMAGE = 1, MEDIA_TYPE_VIDEO = 6,CUSTOM_CAMERA = 4;
    public SpotsDialog progressDialog;
    public Uri fileUri = null;

    private int mLayoutXml;
    private View mView;
    private Dialog dialog;

    public void onCreate(Bundle savedInstanceState, int layoutXml) {
        super.onCreate(savedInstanceState);
        this.mLayoutXml = layoutXml;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.mView = inflater.inflate(mLayoutXml, container, false);
        this.mView.setClickable(true);

        initView(mView);
        bindControls();
        return this.mView;
//        if(this.mView!=null){
//            if((ViewGroup)mView.getParent()!=null)
//                ((ViewGroup)mView.getParent()).removeView(mView);
//            return this.mView;
//        }else{
//            this.mView = inflater.inflate(mLayoutXml, container, false);
//            this.mView.setClickable(false);
//            initView(mView);
//            bindControls();
//            return this.mView;
//        }
    }

    /**
     * Init view ids
     *
     * @param mView
     */
    protected abstract void initView(View mView);

    /**
     * Bind view data into view
     */
    protected abstract void bindControls();

    /**
     * Get Parent activity
     *
     * @return
     */



    public void snackBar(String message) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.frame_main),message, Snackbar.LENGTH_SHORT);
        View mView = snackbar.getView();
        TextView mTextView = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        snackbar.show();
    }

    public SpotsDialog showProgressDialog() {
        try {

            progressDialog = new SpotsDialog(getActivity(), R.style.CustomProgress);
            progressDialog.setCancelable(false);


            progressDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return progressDialog;
    }

    public void cancelProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }




    /*
   * String Array of Permission
   * */
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 404;


    public boolean checkPermissions(Context context,String[] permissions) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }




    public static void hideKeyboard(Context context) {
        try {
            ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            if ((((Activity) context).getCurrentFocus() != null) && (((Activity) context).getCurrentFocus().getWindowToken() != null)) {
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
