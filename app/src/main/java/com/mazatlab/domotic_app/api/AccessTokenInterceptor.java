package com.mazatlab.domotic_app.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AccessTokenInterceptor implements Interceptor {

    String accessToken;
    public AccessTokenInterceptor(String accessToken) {
        this.accessToken = accessToken;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        //rewrite the request to add bearer token
        Request newRequest=chain.request().newBuilder()
                .header("Authorization","Bearer "+ this.accessToken)
                .build();

        return chain.proceed(newRequest);
    }
}
