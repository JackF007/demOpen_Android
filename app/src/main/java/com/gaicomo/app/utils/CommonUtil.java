package com.gaicomo.app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.gaicomo.app.R;
import com.gaicomo.app.views.AppEditText;
import com.gaicomo.app.views.AppTextView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dmax.dialog.SpotsDialog;


/**
 * Created by kindlebit on 9/12/2016.
 */
public class CommonUtil {

    public static SpotsDialog progressDialog;

    private static AlertDialog alertDialog;
//    private static AlertDialog progressDialog;
    private static int hour,minutes;
    final String time="";


    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public static void showLongToast(Context context, String message) {
        final Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 60000);
    }

    public static void showShortToast(Context context, String message) {
        final Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1000);
    }

    public static void showAlert(Context context, String message, String tittle) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(tittle);

        alertDialogBuilder.setNegativeButton(R.string.close,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });


        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


//    public static void imageDialog(String image, Context context){
//
//        Dialog imageDialog = new Dialog(context);
//        imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//
//        imageDialog.setCancelable(true);
//        imageDialog.setCanceledOnTouchOutside(true);
//        imageDialog.setContentView(R.layout.image_dialogue_layout);
//
//        final ImageView ivImage= imageDialog.findViewById(R.tab_id.iv_image);
//
//        if(!image.equals("")) {
//            Ion.with(context)
//                    .load(image)
//                    .asBitmap().setCallback(new FutureCallback<Bitmap>() {
//                @Override
//                public void onCompleted(Exception e, Bitmap result) {
//                    if(e==null)
//                       ivImage.setImageBitmap(result);
//                    else
//                        ivImage.setImageResource(R.drawable.profile_default);
//                }
//            });
//        }else{
//            ivImage.setImageResource(R.drawable.profile_default);
//        }
//
//        imageDialog.show();
//    }



    public static void sendLink(Activity activity, String subject, String msg) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
//        sharingIntent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
        if (sharingIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(sharingIntent);
        }
    }

    public static boolean checkEmail(String email) {
        return Constant.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }


    public static void snackBar(String message,Activity context) {
        Snackbar snackbar = Snackbar.make(context.findViewById(R.id.frame_main),message, Snackbar.LENGTH_SHORT);
        View mView = snackbar.getView();
        TextView mTextView = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        snackbar.show();
    }

    public static SpotsDialog showProgressDialog(Activity context) {
        try {

            progressDialog = new SpotsDialog(context, R.style.CustomProgress);
            progressDialog.setCancelable(false);


            progressDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return progressDialog;
    }

    public static void cancelProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }



    public static void DatePicker(Context context,final AppTextView tv){
        int mYear, mMonth, mDay, mHour, mMinute;

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = null;
        int style;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            style = R.style.datepickerlolipop;
        } else {
            style = R.style.datepickerkitkat;
        }
        datePickerDialog = new DatePickerDialog(context, style,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

//                        SimpleDateFormat sdfSource = new SimpleDateFormat("dd mm yyyy");
//                        //parse the string into Date object
//                        Date date = null;
//                        try {
//                            date = sdfSource.parse(dayOfMonth + " " + (monthOfYear + 1) + " " + year);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//
//                        //create SimpleDateFormat object with desired date format
//                        SimpleDateFormat sdfDestination = new SimpleDateFormat("dd MMM yyyy");

                        //parse the date into another format
                        tv.setText(year+ "-"+ (monthOfYear + 1)+ "-"+dayOfMonth  );

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

}
