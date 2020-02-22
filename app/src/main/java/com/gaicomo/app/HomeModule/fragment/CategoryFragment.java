package com.gaicomo.app.HomeModule.fragment;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.Tweet.fragment.TweetListFragment;
import com.gaicomo.app.common.fragment.ListFragment;
import com.gaicomo.app.views.AppTextView;
/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends BaseFragment implements View.OnClickListener{

    CountDownTimer countDownTimer = null;
    int count = 0;
    ImageView ivInvestment, ivCommunication, ivTweet, ivCompetition, ivOpenData;
    AppTextView tvInvestment, tvCommunication, tvTweet, tvCompetition, tvOpenData;


    public static CategoryFragment newInstance() {
        Bundle args = new Bundle();
        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_category);

    }

    @Override
    public void onPause() {
        super.onPause();
//        if(getActivity()!=null) {
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(getActivity()!=null)
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void initView(View mView) {
        ((HomeActivity)getActivity()).setTitle(getString(R.string.app_name),CategoryFragment.this);

        ivCommunication = mView.findViewById(R.id.ivCommunication);
        ivInvestment = mView.findViewById(R.id.ivInvestment);
        ivTweet = mView.findViewById(R.id.ivTweet);
        ivCompetition = mView.findViewById(R.id.ivCompetition);
        ivOpenData = mView.findViewById(R.id.ivOpenData);

        tvCommunication = mView.findViewById(R.id.tvCommunication);
        tvInvestment = mView.findViewById(R.id.tvInvestment);
        tvTweet = mView.findViewById(R.id.tvTweet);
        tvCompetition = mView.findViewById(R.id.tvCompetition);
        tvOpenData = mView.findViewById(R.id.tvOpenData);
    }

    @Override
    protected void bindControls() {

        ivTweet.setOnClickListener(this);
        ivCommunication.setOnClickListener(this);
        ivInvestment.setOnClickListener(this);
        ivCompetition.setOnClickListener(this);
        ivOpenData.setOnClickListener(this);

        tvTweet.setOnClickListener(this);
        tvCommunication.setOnClickListener(this);
        tvInvestment.setOnClickListener(this);
        tvCompetition.setOnClickListener(this);
        tvOpenData.setOnClickListener(this);

        if(!((HomeActivity)getActivity()).status) {
            animate(ivCommunication, tvCommunication);
            animate(ivInvestment, tvInvestment);
            animate(ivTweet, tvTweet);
            animate(ivCompetition, tvCompetition);
            animate(ivOpenData, tvOpenData);
            ((HomeActivity)getActivity()).status=true;
        }else{
            ivCommunication.setVisibility(View.VISIBLE);
            tvCommunication.setVisibility(View.VISIBLE);
            ivInvestment.setVisibility(View.VISIBLE);
            tvInvestment.setVisibility(View.VISIBLE);
            ivTweet.setVisibility(View.VISIBLE);
            tvTweet.setVisibility(View.VISIBLE);
            ivCompetition.setVisibility(View.VISIBLE);
            tvCompetition.setVisibility(View.VISIBLE);
            ivOpenData.setVisibility(View.VISIBLE);
            tvOpenData.setVisibility(View.VISIBLE);
        }
    }

    void animate(final ImageView iv, final TextView tv) {

        count = count + 100;
//        if (count == 0)
//            count = count + 100;
//        else
//            count = count + 150;

        countDownTimer = new CountDownTimer(count, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                iv.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                iv.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.move));
                tv.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.move));
            }
        };
        countDownTimer.start();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.tvTweet:
            case R.id.ivTweet:
                ((HomeActivity)getActivity()).replaceFragment(TweetListFragment.newInstance());
                break;
            case R.id.ivInvestment:
            case R.id.tvInvestment:
                ((HomeActivity)getActivity()).tab_id =1;
                ((HomeActivity)getActivity()).replaceFragment(ListFragment.newInstance());
                break;
            case R.id.ivCommunication:
            case R.id.tvCommunication:
                ((HomeActivity)getActivity()).tab_id =2;
                ((HomeActivity)getActivity()).replaceFragment(ListFragment.newInstance());
                break;
            case R.id.tvCompetition:
            case R.id.ivCompetition:
                ((HomeActivity)getActivity()).tab_id =3;
                ((HomeActivity)getActivity()).replaceFragment(ListFragment.newInstance());
                break;
            case R.id.tvOpenData:
            case R.id.ivOpenData:
                ((HomeActivity)getActivity()).tab_id =4;
                ((HomeActivity)getActivity()).replaceFragment(ListFragment.newInstance());
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(countDownTimer!=null)
            countDownTimer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null)
        countDownTimer.cancel();

    }
}
