package com.gaicomo.app.LoginModule.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseActivity;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.utils.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class SplashActivity extends BaseActivity {

    ImageView ivLogo;
    private static final String TAG = "SplashActivity";
    String type = "", id = "",name="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ivLogo=findViewById(R.id.iv_logo);
        ivLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_right));
        if(HomeActivity.homeContext!=null)
            HomeActivity.homeContext.finish();
        // [START get_deep_link]
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)

                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null) {
                            Log.d(TAG, "onSuccess: " + deepLink.toString());
                            if (!TextUtils.isEmpty(deepLink.getQueryParameter(Constant.TYPE)))
                                type = deepLink.getQueryParameter(Constant.TYPE);
                            if (!TextUtils.isEmpty(deepLink.getQueryParameter(Constant.ID)))
                                id = deepLink.getQueryParameter(Constant.ID);
                            if(type.equals(Constant.FOREIGN_INVESTMENT)||type.equals(Constant.COMPETITION))
                                name= deepLink.getQueryParameter(Constant.NAME);

                        } else {
                            Log.d(TAG, "getDynamicLink: no link found");
                        }


                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
        // [END get_deep_link]
        startHome();
    }


    void deepLinkDetected() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.TYPE, type);
        intent.putExtra(Constant.NAME, name);
        startActivity(intent);
        finish();
//        switch (type) {
//
//            case Constant.TWEET:
//            case Constant.OPEN_DATA:
//
//                break;
//        }
    }

    void startHome() {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "onSuccess: " + type + id);
                if (AppController.getManager().getId().equals("")) {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    if (!type.equals("")) {
                        deepLinkDetected();
                    } else {
                        Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                }

            }
        }, 2000);


    }
}
