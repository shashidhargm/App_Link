package com.example.shashidhar.may17th_1;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by shashidhar on 17/5/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    DbHelper mydb;
    String jno = null;
    String jname = null;
    String name=null;
    boolean flag=false;
    HttpRequest ob=new HttpRequest();

    private static final String TAG = "";
    private Context mContext;
    private String responseString=null;
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.i(TAG, "Sync Adapter created.");
        mContext = context;
        mydb=new DbHelper(mContext);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Sync Adapter called." + account.name);
       responseString=ob.gg();
        try {
            responseString="{\"data\":"+responseString+"}";
            JSONObject object = new JSONObject(responseString);
            JSONArray array = object.getJSONArray("data");
            for (int i=0;i< array.length(); i++)
            {
                JSONObject json = array.getJSONObject(i);
                jno = json.getString("phone_number");
                Cursor res = mydb.getData(jno);
                res.moveToFirst();
                System.out.println("Yes");
                if (res.getCount() == 0) {
                    System.out.println("no");
                    flag=getNumber(jno);
                    if(flag==true)
                        jname=name;
                    else
                      jname = json.getString("name");
                    mydb.insertData(jno, jname);
                    ContactsManager.addContact(mContext,jno, jname);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean getNumber(String no) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, no);
        Cursor c = contentResolver.query(uri, null, null, null, null);
        if(c.getCount()==0)
            return false;
        while (c.moveToNext()) {
            name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        c.close();
        if(name!=null)
            return true;
        else
            return false;
    }
    }

