package com.mazatlab.domotic_app.api;

import android.os.SystemClock;

import com.mazatlab.domotic_app.utils.Network;

import java.io.IOException;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Network.API_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static Retrofit getClient(String accessToken, int retryTimes, int retryMillisecondsTime) {
        AccessTokenInterceptor interceptor = new AccessTokenInterceptor(accessToken);

        // It add the retry logic
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(retryTimes);

        // It add the delay logic
        Interceptor networkInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                SystemClock.sleep(retryMillisecondsTime);
                return chain.proceed(chain.request());
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(networkInterceptor)
                .dispatcher(dispatcher)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Network.API_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
