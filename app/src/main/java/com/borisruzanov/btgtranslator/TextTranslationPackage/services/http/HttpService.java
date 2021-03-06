package com.borisruzanov.btgtranslator.TextTranslationPackage.services.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.borisruzanov.btgtranslator.TextTranslationPackage.Contract;
import com.borisruzanov.btgtranslator.TextTranslationPackage.services.UrlBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpService implements IHttpService{
//    language = mSettings.getString(LANGUAGE_SHORT,"");
//    translatedInputString = translatedTextInput.getText().toString().replace(" ","+");

    private static String TAG ="HttpService";
    private final RequestQueue requestQueue;
    private UrlBuilder urlBuilder;
//    IPreferencesManager iPreferencesManager;


    public HttpService(Context context, String language, String translatedTextInput) {
        urlBuilder = new UrlBuilder(context, language, translatedTextInput);
        requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public void translateText(String language, String inputText, final CallBack<String> callBack) {

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, urlBuilder.buildUrl(inputText, language), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray results = null;
                        try {
                            results = response.getJSONObject("data").getJSONArray("translations");
                            for (int i=0,j=results.length();i<j;i++) {
                                String translation = results.getJSONObject(i).getString("translatedText");
                                Log.v(Contract.TAG, "HttpService - In onResponse");

                                callBack.call(translation);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {
                        } else if (error instanceof ServerError) {
                            Log.e(TAG, "Server error! Please try again after some time!!");
                        } else if (error instanceof AuthFailureError) {
                            Log.e(TAG, "Auth error! Please try again after some time!!");
                        } else if (error instanceof ParseError) {
                            Log.e(TAG, "Parsing error! Please try again after some time!!");
                        } else if (error instanceof NoConnectionError) {
                            Log.e(TAG, "NoConnectionError!");
                        } else if (error instanceof TimeoutError) {
                            Log.e(TAG, "Connection TimeOut! Please check your internet connection.");
                        }
                    }
                });
        requestQueue.add(jsObjRequest);
    }
}