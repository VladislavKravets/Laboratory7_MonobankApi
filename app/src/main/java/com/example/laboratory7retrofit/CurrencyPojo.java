package com.example.laboratory7retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrencyPojo {
    @SerializedName("currencyCodeA")
    @Expose
    private int currencyCodeA;
    @SerializedName("currencyCodeB")
    @Expose
    private int currencyCodeB;
    @SerializedName("date")
    @Expose
    private int date;
    @SerializedName("rateBuy")
    @Expose
    private float rateBuy;
    @SerializedName("rateCross")
    @Expose
    private float rateCross;
    @SerializedName("rateSell")
    @Expose
    private float rateSell;

    public CurrencyPojo() {
    }

    public int getCurrencyCodeA() {
        return currencyCodeA;
    }

    public void setCurrencyCodeA(int currencyCodeA) {
        this.currencyCodeA = currencyCodeA;
    }

    public int getCurrencyCodeB() {
        return currencyCodeB;
    }

    public void setCurrencyCodeB(int currencyCodeB) {
        this.currencyCodeB = currencyCodeB;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public float getRateBuy() {
        return rateBuy;
    }

    public void setRateBuy(float rateBuy) {
        this.rateBuy = rateBuy;
    }

    public float getRateCross() {
        return rateCross;
    }

    public void setRateCross(float rateCross) {
        this.rateCross = rateCross;
    }

    public float getRateSell() {
        return rateSell;
    }

    public void setRateSell(float rateSell) {
        this.rateSell = rateSell;
    }
}