package com.gaicomo.app.LoginModule.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.LoginModule.activity.LoginActivity;
import com.gaicomo.app.LoginModule.model.Login.LoginPojo;
import com.gaicomo.app.R;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.views.AppButton;
import com.gaicomo.app.views.AppEditText;
import com.gaicomo.app.views.AppTextView;
import com.gaicomo.app.webutils.WebInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "LoginFragment";

    AppEditText etEmail,etPassword;
    AppTextView tvForgot,tvSignup,tvnewUser;
    AppButton btLogin;
    LinearLayout llSignup;


    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_login);
    }

    @Override
    protected void initView(View mView) {
        etEmail=mView.findViewById(R.id.et_email);
        etPassword=mView.findViewById(R.id.et_password);
        tvForgot= mView.findViewById(R.id.tv_forgot);
        tvSignup= mView.findViewById(R.id.tv_signup);
        tvnewUser= mView.findViewById(R.id.tv_new_user);
        btLogin=mView.findViewById(R.id.bt_login);
        llSignup=mView.findViewById(R.id.ll_sign_up);
    }

    @Override
    protected void bindControls() {
        ((LoginActivity)getActivity()).setTitle("",false);
        tvForgot.setOnClickListener(this);
        tvSignup.setOnClickListener(this);
        tvForgot.setOnClickListener(this);
        btLogin.setOnClickListener(this);
        llSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_forgot:
                hideKeyboard(getActivity());
                ((LoginActivity)getActivity()).replaceFragment(ForgotFragment.newInstance());
                break;
            case R.id.bt_login:
                hideKeyboard(getActivity());
                 validation();
                break;

            case R.id.tv_new_user:
            case R.id.tv_signup:
            case R.id.ll_sign_up:
                hideKeyboard(getActivity());
                ((LoginActivity)getActivity()).replaceFragment(SignupFragment.newInstance());
                break;
        }
    }

    private void validation() {
        if(!TextUtils.isEmpty(etEmail.getText().toString().trim())) {
            if (CommonUtil.checkEmail(etEmail.getText().toString().trim())) {
                     if(!TextUtils.isEmpty(etPassword.getText().toString().trim())){
                         if(etPassword.getText().toString().trim().length()>=8){
                             logIn();
                         }else{
                             snackBar("Please enter a valid password.");
                         }
                     }else{
                         snackBar("Please enter your password");
                     }
            } else {
                snackBar("Please enter a valid email address");
            }
        }else{
            snackBar("Please enter your email address");
        }
    }


    private void logIn() {

        showProgressDialog();
        WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
        Call<LoginPojo> call = Service.login(etEmail.getText().toString().trim(),etPassword.getText().toString().trim(),"1","da");
        call.enqueue(new Callback<LoginPojo>() {
            @Override
            public void onResponse(Call<LoginPojo> call, Response<LoginPojo> response) {
                cancelProgressDialog();

                LoginPojo loginResponse=response.body();
                if (loginResponse.getStatus()) {
                    AppController.getManager().setId(loginResponse.getData().getId());
                    AppController.getManager().setName(loginResponse.getData().getName());
                    AppController.getManager().setEmail(loginResponse.getData().getEmail());
                    AppController.getManager().setMobile(loginResponse.getData().getMobile());
                    AppController.getManager().setProfile_image(loginResponse.getData().getProfilePicture());
                    AppController.getManager().setUser_name(loginResponse.getData().getUserName());
                    Intent i=new Intent(getActivity(), HomeActivity.class);
                    startActivity(i);
                    getActivity().finish();
                }else{
                    snackBar(loginResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<LoginPojo> call, Throwable t) {
                cancelProgressDialog();
                snackBar(t.getMessage());
            }
        });

    }




}
