package com.gaicomo.app.HomeModule.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseActivity;
import com.gaicomo.app.HomeModule.fragment.CategoryFragment;
import com.gaicomo.app.HomeModule.fragment.TagDetailFragment;
import com.gaicomo.app.LoginModule.activity.LoginActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.Tweet.fragment.PostTweetFragment;
import com.gaicomo.app.Tweet.fragment.ProfileFragment;
import com.gaicomo.app.Tweet.fragment.TweetDetailFragment;
import com.gaicomo.app.Tweet.fragment.TweetListFragment;
import com.gaicomo.app.Tweet.fragment.TweetTagSearchFragment;
import com.gaicomo.app.common.fragment.ListFragment;
import com.gaicomo.app.common.fragment.ParsedListFragment;
import com.gaicomo.app.common.fragment.SubListFragment;
import com.gaicomo.app.common.fragment.WebFragment;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.views.AppTextView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends BaseActivity implements View.OnClickListener {


    private static final String TAG = "HomeActivity";
    public static HomeActivity homeContext = null;
    public boolean status = false;
    public int tab_id = 1;
    public ImageView ivSearch, ivBack, ivShare;
    public AppTextView tvTitle;

    //////////Tab//////////
    public LinearLayout llTab, llShadow, llToolbar;
    FrameLayout frameMain;
    CircleImageView ivUser;
    ImageView ivForeignCurrency, ivCommunication, ivCompetition, ivOpenData, ivHome;
    private boolean closeApp = false;
    private FragmentManager manager;
    private PopupMenu popup;
    private String type = "", id = "", name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    private void init() {
        homeContext = this;
        frameMain = findViewById(R.id.frame_main);
        ivBack = findViewById(R.id.iv_back);
        ivSearch = findViewById(R.id.iv_search);
        ivShare = findViewById(R.id.iv_share);
        ivUser = findViewById(R.id.iv_user);
        tvTitle = findViewById(R.id.tv_title);

        llShadow = findViewById(R.id.ll_shadow);
        llTab = findViewById(R.id.ll_tab);
        llToolbar = findViewById(R.id.ll_tool_bar);
        ivForeignCurrency = findViewById(R.id.iv_foreign_currency);
        ivCommunication = findViewById(R.id.iv_communication);
        ivCompetition = findViewById(R.id.iv_competition);
        ivOpenData = findViewById(R.id.iv_open_data);
        ivHome = findViewById(R.id.iv_home);
        ivHome = findViewById(R.id.iv_home);

        bindView();
    }


    private void bindView() {


        Log.d(TAG, "bindView: ");

        if (getIntent().hasExtra(Constant.TYPE)) {
            type = getIntent().getStringExtra(Constant.TYPE);
        }

        if (getIntent().hasExtra(Constant.ID)) {
            id = getIntent().getStringExtra(Constant.ID);
        }

        if (getIntent().hasExtra(Constant.NAME)) {
            name = getIntent().getStringExtra(Constant.NAME);
        }


        ivBack.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivUser.setOnClickListener(this);

        ivForeignCurrency.setOnClickListener(this);
        ivCommunication.setOnClickListener(this);
        ivHome.setOnClickListener(this);
        ivCompetition.setOnClickListener(this);
        ivOpenData.setOnClickListener(this);

        if (type.equals(Constant.TWEET))
            replaceFragment(TweetDetailFragment.newInstance(id));
        else
            addFragment(CategoryFragment.newInstance());

        setImage();
        popup = new PopupMenu(HomeActivity.this, ivUser);
        popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile:
                        replaceFragment(ProfileFragment.newInstance(AppController.getManager().getId()));
                        break;
                    case R.id.logout:
                        AppController.getManager().clearPreference();
                        Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                        break;
                }

                return true;
            }
        });

    }


    public void setImage() {
        if (!AppController.getManager().getProfile_image().equalsIgnoreCase("") && AppController.getManager().getProfile_image() != null) {
            Ion.with(this)
                    .load(AppController.getManager().getProfile_image())
                    .asBitmap().setCallback(new FutureCallback<Bitmap>() {
                @Override
                public void onCompleted(Exception e, Bitmap result) {
                    if (e == null)
                        ivUser.setImageBitmap(result);
                }
            });
        }
    }

    public void setTitle(String name, Fragment fragment) {
        tvTitle.setText(name);
        if (fragment instanceof CategoryFragment) {
            llToolbar.setVisibility(View.GONE);
            showSearch();
            ivSearch.setVisibility(View.INVISIBLE);
        } else if (fragment instanceof TweetListFragment || fragment instanceof ProfileFragment || fragment instanceof TagDetailFragment) {
            llToolbar.setVisibility(View.VISIBLE);
            showSearch();
        } else if (fragment instanceof ListFragment) {
            llToolbar.setVisibility(View.VISIBLE);
            showTab();
        } else if (fragment instanceof TweetDetailFragment || fragment instanceof TweetTagSearchFragment || fragment instanceof PostTweetFragment || fragment instanceof ParsedListFragment || fragment instanceof SubListFragment) {
            llToolbar.setVisibility(View.VISIBLE);
            showBack();
            ivShare.setVisibility(View.INVISIBLE);
        }

        if (tab_id == 1) {
            ivForeignCurrency.setImageResource(R.drawable.foreign_currency_active);
            ivCommunication.setImageResource(R.drawable.communication);
            ivCompetition.setImageResource(R.drawable.competition);
            ivOpenData.setImageResource(R.drawable.open_data);
        } else if (tab_id == 2) {
            ivForeignCurrency.setImageResource(R.drawable.foreign_currency);
            ivCommunication.setImageResource(R.drawable.communication_active);
            ivCompetition.setImageResource(R.drawable.competition);
            ivOpenData.setImageResource(R.drawable.open_data);
        } else if (tab_id == 3) {
            ivForeignCurrency.setImageResource(R.drawable.foreign_currency);
            ivCommunication.setImageResource(R.drawable.communication);
            ivCompetition.setImageResource(R.drawable.competition_active);
            ivOpenData.setImageResource(R.drawable.open_data);
        } else if (tab_id == 4) {
            ivForeignCurrency.setImageResource(R.drawable.foreign_currency);
            ivCommunication.setImageResource(R.drawable.communication);
            ivCompetition.setImageResource(R.drawable.competition);
            ivOpenData.setImageResource(R.drawable.open_data_active);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.frame_main);


        if (fragment instanceof CategoryFragment) {
            if (closeApp) {
                finish();
                super.onBackPressed();
            } else {
                CommonUtil.showLongToast(HomeActivity.this, getString(R.string.press_back_button));
                new CountDownTimer(10000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        closeApp = true;
                    }

                    public void onFinish() {
                        closeApp = false;
                    }
                }.start();
            }
        } else {
            getActiveFragment();
        }
    }


    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.frame_main);

        switch (v.getId()) {
            case R.id.iv_back:
                getActiveFragment();
                break;
            case R.id.iv_search:
                Intent i = new Intent(this, SearchActivity.class);
                i.putExtra(Constant.TYPE, fragment.getClass().getSimpleName());
                startActivityForResult(i, Constant.SEARCH);
//                if(fragment instanceof TweetListFragment )
//                    replaceFragment(SearchFragment.newInstance(Constant.TWEET));
//                else
//                    replaceFragment(SearchFragment.newInstance(Constant.OPEN_DATA));
                break;
            case R.id.iv_share:
//                if (fragment instanceof DetailFragment)
//                    ((DetailFragment) fragment).share();
//                else if (fragment instanceof competitionDetailFragment)
//                    ((competitionDetailFragment) fragment).share();
                break;
            case R.id.iv_user:
                popup.show();
                break;
            case R.id.iv_foreign_currency:
                if (tab_id != 1) {
                    tab_id = 1;
                    refreshFragment();
                }
                break;
            case R.id.iv_communication:
                tab_id = 2;
                refreshFragment();
                break;
            case R.id.iv_competition:
                tab_id = 3;
                refreshFragment();
                break;
            case R.id.iv_open_data:
                tab_id = 4;
                refreshFragment();
                break;
            case R.id.iv_home:
                getActiveFragment();
                break;

        }
    }

    private void refreshFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.frame_main);

        if (fragment instanceof ListFragment)
            ((ListFragment) fragment).refresh();

    }

    public void replaceFragment(Fragment fragment) {
        Log.d(TAG, "replaceFragment: " + getSupportFragmentManager().getBackStackEntryCount());
        String backStateName = fragment.getClass().getSimpleName();
        String fragmentTag = backStateName;

        FragmentManager manager = this.getSupportFragmentManager();
        ;
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) {
            //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
//            ft.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_out_right);
            ft.replace(R.id.frame_main, fragment, fragmentTag);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.frame_main, fragment);
        ft.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.frame_main);


            if (requestCode == Constant.SEARCH) {
                if (data != null) {
                    if (fragment instanceof TweetListFragment) {
                        if (data.hasExtra(Constant.ID))
                            replaceFragment(TweetTagSearchFragment.newInstance(data.getStringExtra(Constant.ID), data.getStringExtra(Constant.SEARCH_TEXT)));
                        else
                            fragment.onActivityResult(requestCode, resultCode, data);
                    } else {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
            } else {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
    }

    public void getActiveFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.frame_main);


//        if (fragment instanceof ForeignInvestmentListFragment || fragment instanceof CommunicationListFragment || fragment instanceof CompetitionListFragment || fragment instanceof OpenDataFragment) {
//            popAllStack();
//            replaceFragment(CategoryFragment.newInstance());
//        } else
        if (fragment instanceof TweetTagSearchFragment) {
            popStack();
//            popAllStack();
//            replaceFragment(CategoryFragment.newInstance());
        } else if ((fragment instanceof TweetDetailFragment)
                && getSupportFragmentManager().getBackStackEntryCount() == 1) {
            popAllStack();
            replaceFragment(CategoryFragment.newInstance());

        }else if(fragment instanceof WebFragment){
            if(((WebFragment)fragment).webView.canGoBack())
                ((WebFragment)fragment).webView.goBack();
            else
                popStack();
        }
            else {
            popStack();
        }
    }

    public void popAllStack() {
        FragmentManager fm = this.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }


    public void showSearch() {
        ivSearch.setVisibility(View.VISIBLE);
        ivUser.setVisibility(View.VISIBLE);
        ivShare.setVisibility(View.GONE);
        ivBack.setVisibility(View.GONE);
        llTab.setVisibility(View.GONE);
        llShadow.setVisibility(View.GONE);
        YoYo.with(Techniques.BounceIn)
                .duration(1500)
                .playOn(ivSearch);

        YoYo.with(Techniques.BounceIn)
                .duration(1500)
                .playOn(ivUser);
    }

    public void showBack() {
        ivSearch.setVisibility(View.GONE);
        ivUser.setVisibility(View.GONE);
        ivShare.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        llTab.setVisibility(View.GONE);
        llShadow.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.BounceIn)
                .duration(1500)
                .playOn(ivBack);
    }

    public void showTab() {
        ivSearch.setVisibility(View.VISIBLE);
        ivUser.setVisibility(View.VISIBLE);
        ivShare.setVisibility(View.GONE);
        ivBack.setVisibility(View.GONE);
        llTab.setVisibility(View.VISIBLE);
        llShadow.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.BounceIn)
                .duration(1500)
                .playOn(ivUser);
    }


}
