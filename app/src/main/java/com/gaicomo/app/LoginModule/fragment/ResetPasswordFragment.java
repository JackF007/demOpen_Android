package com.gaicomo.app.LoginModule.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.LoginModule.activity.LoginActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.utils.CommonPojo;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.views.AppButton;
import com.gaicomo.app.views.AppEditText;
import com.gaicomo.app.webutils.WebInterface;


import org.json.JSONObject;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends BaseFragment implements View.OnClickListener {


    AppEditText etCode,etPassword,etCnPassword;
    AppButton btReset;
    private String code="",email="";

    public static ResetPasswordFragment newInstance(String email,String code) {
        Bundle args = new Bundle();
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        args.putString(Constant.EMAIL,email);
        args.putString(Constant.CODE,code);
        fragment.setArguments(args);
        return fragment;
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_reset_password);
        if(getArguments()!=null){
            code=getArguments().getString(Constant.CODE);
            email=getArguments().getString(Constant.EMAIL);
        }
    }


    @Override
    protected void initView(View mView) {
              etCode=mView.findViewById(R.id.et_code);
              etPassword=mView.findViewById(R.id.et_password);
              etCnPassword=mView.findViewById(R.id.et_cn_password);
              btReset=mView.findViewById(R.id.bt_reset);
    }

    @Override
    protected void bindControls() {
        ((LoginActivity)getActivity()).setTitle(getString(R.string.reset_password),true);
        btReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_reset:
                hideKeyboard(getActivity());
                if(code.equals(etCode.getText().toString().trim())){
                    if(!TextUtils.isEmpty(etPassword.getText().toString().trim())){
                        if(etPassword.getText().toString().trim().length()>=8){
                            if(etCnPassword.getText().toString().trim().equals(etPassword.getText().toString().trim())){
                                resetPassword();
                            }else{
                                snackBar("Password does not match.");
                            }
                        }else{
                            snackBar("Please enter atleast 8 character of password");
                        }
                    }else {
                        snackBar("Please enter your new password");
                    }
                }else{
                    snackBar("Please eneter a valid code.");
                }
                break;
        }
    }

    private void resetPassword() {
        showProgressDialog();
        WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
        Call<CommonPojo> call = Service.resetPassword(email, etPassword.getText().toString());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                cancelProgressDialog();
                if (response.body().getStatus()) {
                    snackBar(response.body().getMessage());
                    ((LoginActivity)getActivity()).popAllStack();
                    ((LoginActivity)getActivity()). replaceFragment(LoginFragment.newInstance());                    snackBar(response.body().getMessage());
                }else{
                    snackBar(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<CommonPojo> call, Throwable t) {
                cancelProgressDialog();
                snackBar(t.getMessage());
            }
        });

    }

}
