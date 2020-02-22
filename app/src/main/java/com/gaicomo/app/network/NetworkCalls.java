package com.gaicomo.app.network;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.gaicomo.app.Base.BaseActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.utils.CommonUtil;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by surya on 8/1/18.
 */

public class NetworkCalls {

    NetworkCallBack myCBI;
    public Dialog mprogress;
    Context context;
    private static final String TAG = "NetworkCalls";

    public NetworkCalls(NetworkCallBack callBackInterface, Context context) {
        myCBI = callBackInterface;
        this.context = context;
    }

    public void NetworkAPICall(final String apiType, final boolean showprogress) {
        Log.e("NetworkAPI Interface", "================" + apiType);
        if (CommonUtil.isConnectingToInternet(context)) {
            if(showprogress) ((BaseActivity)context).showProgressDialog(context.getString(R.string.loging_please_wait));
            myCBI.getAPIB(apiType)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String jsonString) {
//                            Log.d(TAG, "onCompleted: "+jsonString.toString());
                           if(showprogress) ((BaseActivity)context).cancelProgressDialog();
                            try {
                                if (e == null) {
                                    if (jsonString != null && !jsonString.isEmpty()) {
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        myCBI.SuccessCallBack(jsonObject, apiType);
                                    } else {
                                        myCBI.ErrorCallBack(context.getString(R.string.jsonparsing_error_message), apiType);
                                    }
                                } else {
                                    myCBI.ErrorCallBack(context.getString(R.string.exception_api_error_message), apiType);
                                }
                            } catch (JSONException e1) {
//                                Log.d(TAG, "onCompleted: "+e1.toString());
                                e1.printStackTrace();
                            }
                        }
                    });
        } else {
            myCBI.ErrorCallBack(context.getString(R.string.internet_error_message), apiType);
        }
    }


    public interface NetworkCallBack {

        Builders.Any.B getAPIB(String ApiName);

        void SuccessCallBack(JSONObject Result, String ApiName) throws JSONException;

        void ErrorCallBack(String Message, String ApiName);
    }


}