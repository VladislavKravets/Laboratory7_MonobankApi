package com.example.laboratory7retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IJSONPlaceHolderApi {
    /* тянем все данные */
    @GET("currency")
    Call<List<CurrencyPojo>> getAllCurrencies();
}
