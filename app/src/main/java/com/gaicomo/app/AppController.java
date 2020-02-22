package com.gaicomo.app;


import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.gaicomo.app.network.NetworkChangeReceiver;
import com.gaicomo.app.utils.PreferencesManager;
import com.gaicomo.app.webutils.WebConstants;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AppController extends MultiDexApplication {

    public static PreferencesManager manager;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static AppController controller;
    public static Retrofit retrofit;
    private static final String TAG = "AppController";

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate() {
        Fabric.with(this, new Crashlytics());
        MultiDex.install(this);
        controller = this;


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }


    public static PreferencesManager getManager() {

        if (manager == null)
            manager = new PreferencesManager(controller);

        return manager;
    }


    public static Retrofit getRetrofitInstance() {

        if (retrofit != null)
            return retrofit;

//            httpClient.addInterceptor(new Interceptor() {
//                @Override
//                public Response intercept(Chain chain) throws IOException {
//                    Request original = chain.request();
//
//                    Request.Builder requestBuilder = original.newBuilder()
//                            .header("Accept", "application/json")
//                            .header("x-api-key", "$2y$10$wWcYRnJiq3aDK1VavfCEOud7qSS5f2kal5df43f43545gfg56oKCQ4U43MHNumiTBAOS")
//                            .method(original.method(), original.body());
//
//                    Request request = requestBuilder.build();
//                    return chain.proceed(request);
//                }
//            });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = httpClient.addInterceptor(interceptor).connectTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES) .writeTimeout(1, TimeUnit.MINUTES).build();

        final Gson gson0 = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(WebConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson0))
                .client(client)
                .build();

        return retrofit;
    }

    public static synchronized AppController getInstance() {
        return controller;
    }

    public void setConnectivityListener(NetworkChangeReceiver.ConnectivityReceiverListener listener) {
        NetworkChangeReceiver.connectivityReceiverListener = listener;
    }

}
