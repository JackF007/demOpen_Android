package com.gaicomo.app.LoginModule.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.hbb20.CountryCodePicker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends BaseFragment implements View.OnClickListener{

    AppTextView tvMemeber,tvSignIn;
    AppEditText etEmail,etPassword,etAddress,etNumber,etName;
    AppButton btSignUp;
    LinearLayout llSignIn;
    CountryCodePicker ccp;
    String strCountry="";
    private static final String TAG = "SignupFragment";

    public static SignupFragment newInstance() {
        Bundle args = new Bundle();
        SignupFragment fragment = new SignupFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_signup);
    }
    @Override
    protected void initView(View mView) {
        tvMemeber= mView.findViewById(R.id.tv_member);
        tvSignIn= mView.findViewById(R.id.tv_signIn);
        etEmail= mView.findViewById(R.id.et_email);
        etPassword= mView.findViewById(R.id.et_password);
        etAddress= mView.findViewById(R.id.et_address);
        etNumber= mView.findViewById(R.id.et_number);
        etName= mView.findViewById(R.id.et_name);
        btSignUp=mView.findViewById(R.id.bt_signup);
        llSignIn=mView.findViewById(R.id.ll_sign_in);
        ccp=mView.findViewById(R.id.ccp);
    }

    @Override
    protected void bindControls() {
        ((LoginActivity)getActivity()).setTitle("SIGN UP",true);
        tvMemeber.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);
        btSignUp.setOnClickListener(this);
        llSignIn.setOnClickListener(this);

        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
            String countryCodeValue = tm.getNetworkCountryIso();
            ccp.setCountryForNameCode(countryCodeValue);
        } else {
            //no sim card available
        }

        Log.d(TAG, "bindControls: "+ ccp.getSelectedCountryCode());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_member:
            case R.id.tv_signIn:
            case R.id.ll_sign_in:
                hideKeyboard(getActivity());
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            case R.id.bt_signup:
                hideKeyboard(getActivity());
                validate();
                break;
        }
    }

    private void validate() {
        if(!TextUtils.isEmpty(etName.getText().toString().trim())){
            if(!TextUtils.isEmpty(etEmail.getText().toString().trim())){
                if(CommonUtil.checkEmail(etEmail.getText().toString().trim())){
                    if(!TextUtils.isEmpty(etNumber.getText().toString().trim())){
                        if(etNumber.getText().toString().trim().length()==10){
                            if(!TextUtils.isEmpty(etAddress.getText().toString().trim())){
                                if(etPassword.getText().toString().trim().length()>=8){
                                    signUp();
                                }else{
                                    snackBar("Please enter atleast 8 character of password");
                                }
                            }else{
                                snackBar("please enter your address");
                            }
                        }else{
                            snackBar("Please enter a valid phone number");
                        }
                    }else{
                        snackBar("Please enter your phone number");
                    }
                }else{
                    snackBar("please enter a valid email address");
                }
            }else{
                snackBar("Please enter your email address");
            }
        }else{
            snackBar("Please enter your name");
        }
    }


    private void signUp() {
        strCountry=ccp.getSelectedCountryCode().replace("+","");

        showProgressDialog();
        WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
        Call<LoginPojo> call = Service.signup(etName.getText().toString().trim(),etEmail.getText().toString().trim(),
                strCountry,etNumber.getText().toString().trim(),etAddress.getText().toString(),etPassword.getText().toString().trim(),
                "1","fr ","0");
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
