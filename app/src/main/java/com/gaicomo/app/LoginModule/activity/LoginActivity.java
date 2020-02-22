package com.gaicomo.app.LoginModule.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.gaicomo.app.Base.BaseActivity;
import com.gaicomo.app.LoginModule.fragment.LoginFragment;
import com.gaicomo.app.LoginModule.fragment.ResetPasswordFragment;
import com.gaicomo.app.R;
import com.gaicomo.app.views.AppTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "LoginActivity";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    LinearLayout llTopBar;
    AppTextView tvTitle;
    ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        llTopBar=findViewById(R.id.ll_topbar);
        tvTitle=findViewById(R.id.tv_title);
        ivBack=findViewById(R.id.iv_back);

        ivBack.setOnClickListener(this);

        replaceFragment(LoginFragment.newInstance());
    }


    @Override
    public void onBackPressed() {
        onCustomBackPress();
    }

    public void setTitle(String title,boolean status){
        tvTitle.setText(title);

        if(status){
          ivBack.setVisibility(View.VISIBLE);
          tvTitle.setVisibility(View.VISIBLE);
        }else{
            ivBack.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
        }
    }

    public void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getSimpleName();
        String fragmentTag = backStateName;

        FragmentManager manager = this.getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frame_main, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }


    public void onCustomBackPress() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.frame_main);

        if (fragment instanceof LoginFragment) {
            finish();
        }
        else  {
            popStack();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_back:
                 onCustomBackPress();
                break;
        }
    }
}
