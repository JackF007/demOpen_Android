package com.gaicomo.app.common.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.views.AppTextView;

import static android.content.Context.DOWNLOAD_SERVICE;

public class WebFragment extends BaseFragment implements View.OnClickListener,DownloadListener {

    private String url,lastUrl;
    private String name;
    AppTextView tvTryAgain;
    public WebView webView;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };



    public WebFragment() {
        // Required empty public constructor
    }


    public static WebFragment newInstance(String name, String url) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(Constant.NAME, name);
        args.putString(Constant.URL, url);
        fragment.setArguments(args);
        return fragment;
    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_web);
        if (getArguments() != null) {
            name = getArguments().getString(Constant.NAME);
            url = getArguments().getString(Constant.URL);
        }
    }

    @Override
    protected void initView(View mView) {
        ((HomeActivity)getActivity()).setTitle(name,WebFragment.this);

        webView=mView.findViewById(R.id.webView);
        tvTryAgain=mView.findViewById(R.id.tvTryAgain);
         tvTryAgain.setOnClickListener(this);
         webView.setDownloadListener(this);
    }

    @Override
    protected void bindControls() {
        showProgressDialog();
        lastUrl=url;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                webView.setVisibility(View.VISIBLE);
                tvTryAgain.setVisibility(View.GONE);
                lastUrl=url;
                cancelProgressDialog();
            }



            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);

            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                lastUrl = failingUrl;
                webView.setVisibility(View.GONE);
                tvTryAgain.setVisibility(View.VISIBLE);
                cancelProgressDialog();
            }

        });

        webView.loadUrl(url);



    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tvTryAgain:
                webView.loadUrl(lastUrl);
                break;
        }
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermissions(getActivity(), permissions)) {
                Download(url,userAgent,contentDisposition,mimetype,contentLength);
            }
        } else {
            Download(url,userAgent,contentDisposition,mimetype,contentLength);
        }

//        CommonUtil.showLongToast(getActivity(),"Downloading File");
    }

    void Download(String url, String userAgent, String contentDisposition, String mimetype, long contentLength){

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setMimeType(mimetype);
        //------------------------COOKIE!!------------------------
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        //------------------------COOKIE!!------------------------
        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription("Downloading file...");
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
        DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(request);
    }

}
