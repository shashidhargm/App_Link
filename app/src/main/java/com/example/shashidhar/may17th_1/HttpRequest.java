package com.example.shashidhar.may17th_1;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;

/**
 * Created by shashidhar on 28/5/16.
 */
public class  HttpRequest {
    private static String url = "http://192.168.216.5/api/v1/contacts";
    private String responseString = null;

    public String gg() {

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = null;
        {
            try {
                response = httpclient.execute(new HttpGet(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    response.getEntity().writeTo(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                responseString = out.toString();
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    response.getEntity().getContent().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    throw new IOException(statusLine.getReasonPhrase());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseString;
    }

}