package com.example.laboratory7retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static NetworkService mInstance;
    private final Retrofit mRetrofit;

    private NetworkService(String url) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NetworkService getInstance(String url) {
        if (mInstance == null) {
            mInstance = new NetworkService(url);
        }
        return mInstance;
    }

    public IJSONPlaceHolderApi getJSONApi() {
        return mRetrofit.create(IJSONPlaceHolderApi.class);
    }
}
