package com.gaicomo.app.LoginModule.fragment;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.LoginModule.activity.LoginActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.utils.CommonPojo;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.views.AppButton;
import com.gaicomo.app.views.AppEditText;
import com.gaicomo.app.webutils.WebInterface;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotFragment extends BaseFragment implements View.OnClickListener{

    AppEditText etEmail;
    AppButton btSend;
    private String link="";

    public static ForgotFragment newInstance() {
        Bundle args = new Bundle();
        ForgotFragment fragment = new ForgotFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_forgot);
    }


    @Override
    protected void initView(View mView) {
         etEmail=mView.findViewById(R.id.et_email);
         btSend=mView.findViewById(R.id.bt_send);

    }

    @Override
    protected void bindControls() {
        ((LoginActivity)getActivity()).setTitle(getString(R.string.Forgot_password),true);
        btSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
           switch(v.getId()){

               case R.id.bt_send:
                   hideKeyboard(getActivity());
                   if(!TextUtils.isEmpty(etEmail.getText().toString().trim())){
                       if(CommonUtil.checkEmail(etEmail.getText().toString().trim())){
                           forgotPassword();
                       }else{
                           snackBar("Please enter a valid email address");
                       }
                   }else{
                       snackBar("please enter your email address");
                   }
                   break;
           }
    }


    private void forgotPassword() {
        Random rnd = new Random();
        final int n = 1000000 + rnd.nextInt(90000000);
        showProgressDialog();
        WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
        Call<CommonPojo> call = Service.forgotPassword(etEmail.getText().toString().trim(), String.valueOf(n));
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                cancelProgressDialog();
                    if (response.body().getStatus()) {
                        ((LoginActivity)getActivity()).replaceFragment(ResetPasswordFragment.newInstance(etEmail.getText().toString().toString().trim(),String.valueOf(n)));
                        snackBar(response.body().getMessage());
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
