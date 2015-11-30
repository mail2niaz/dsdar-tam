/**
 * Copyright (c) 2014 Dsdar Inc.
 * <p/>
 * All rights reserved. For use only with Dsdar Inc.
 * This software is the confidential and proprietary information of
 * Dsdar Inc, ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Dsdar Inc.
 */
package com.dsdar.thosearoundme.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility class for Async methods
 *
 * @author senthil_kumaran
 */
public class AppAsyncTask extends AsyncTask<String, Void, String> {
    private Context itsContext;
    public ProgressDialog itsProgressDialog;
    private AsyncTaskListener itsAsyncTaskListener;
    private String itsApiName, itsApiType, itsProgressMsg;

    public interface AsyncTaskListener {
        void onPreExecuteConcluded();

        void onPostExecuteConcluded(String theResult);
    }

    public AppAsyncTask(Context theContext, String theApiName,
                        String theApiType, String theProgressMsg) {
        itsApiName = theApiName;
        itsApiType = theApiType;
        itsContext = theContext;
        itsProgressMsg = theProgressMsg;
        itsProgressDialog = new ProgressDialog(theContext);
    }

    public AppAsyncTask(Context theContext, String theApiName, String theApiType) {
        itsApiName = theApiName;
        itsApiType = theApiType;
        itsContext = theContext;
    }

    final public void setListener(AsyncTaskListener theListener) {
        itsAsyncTaskListener = theListener;
    }

    @Override
    final protected void onPreExecute() {
        Activity activity = (Activity) itsContext;
        if (!activity.isFinishing()) {
            if (itsProgressDialog != null) {
//				itsProgressDialog.setCancelable(false);
                itsProgressDialog.setMessage(itsProgressMsg);
                itsProgressDialog
                        .setProgressStyle(ProgressDialog.STYLE_SPINNER);
                itsProgressDialog.setProgress(0);
                itsProgressDialog.show();
            }
            if (itsAsyncTaskListener != null)
                itsAsyncTaskListener.onPreExecuteConcluded();
        }
    }

    @Override
    final protected String doInBackground(String... theParams) {
        String aResponse = null;
        try {
            SharedPreferences aPreference = itsContext.getSharedPreferences(
                    MyAppConstants.APP_PREFERENCE, Context.MODE_PRIVATE);
            URL aApiUrl = new URL(aPreference.getString(
                    MyAppConstants.SERVER_URL, "") + itsApiName);

            HttpURLConnection aConnection = (HttpURLConnection) aApiUrl
                    .openConnection();
            aConnection.setRequestMethod(itsApiType);
            aConnection.setRequestProperty(MyAppConstants.API_CONTENT_TYPE,
                    MyAppConstants.API_JSON_CONTENT_TYPE);

            if (itsApiType.equals("DELETE")) {
                aResponse = Integer.toString(aConnection.getResponseCode());
                BufferedReader aReader = new BufferedReader(
                        new InputStreamReader(aConnection.getInputStream()));
                aResponse = aReader.readLine();
                aReader.close();
            } else {
                aConnection.setDoOutput(true);
                PrintStream aPrintStream = new PrintStream(
                        aConnection.getOutputStream());
                aPrintStream.print(new JSONObject(theParams[0]));
                aPrintStream.close();

                BufferedReader aReader = new BufferedReader(
                        new InputStreamReader(aConnection.getInputStream()));
                aResponse = aReader.readLine();
                aReader.close();
            }
        } catch (MalformedURLException theMalformedURLException) {
            theMalformedURLException.printStackTrace();
            Log.e(itsApiName,
                    "MalformedURLException when calling webservices in AsyncTask");
        } catch (IOException theIoException) {
            theIoException.printStackTrace();
            Log.e(itsApiName,
                    "IOException when calling webservices in AsyncTask");
        } catch (JSONException theJSONException) {
            theJSONException.printStackTrace();
            Log.e(itsApiName,
                    "JSONException when calling webservices in AsyncTask");
        } catch (Exception theException) {
            theException.printStackTrace();
            Log.e(itsApiName, "Exception when calling webservices in AsyncTask");
        }
        return aResponse;
    }

    @Override
    final protected void onPostExecute(String theResult) {
        if (itsProgressDialog != null) {
            itsProgressDialog.cancel();
        }
        if (theResult != null) {
            if (itsAsyncTaskListener != null)
                itsAsyncTaskListener.onPostExecuteConcluded(theResult);
        } else {
            Toast.makeText(itsContext.getApplicationContext(),
                    MyAppConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
        }
    }
}