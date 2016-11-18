package com.webnish.reach.contacts;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;


/**
 * Created by shashidhar on 17/5/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private String q;
    private static String message1;
    static String url = "amqp://139.59.16.121:5672";


    private static final String TAG = "";
    private static Context mContext;
    private static boolean present;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.i(TAG, "Sync Adapter created.");
        mContext = context;
//        SharedPreferences pref = mContext.getSharedPreferences("CurrentUser", 0);
//        q = pref.getString("User_id", null);
//        if(q!=null)
//            ;
//        else
//        {
//            q = pref.getString("User_id_join", null);
//        }
//        vHost = pref.getString("vhost", null);
//        QUEUE_NAME=q;
//        try {
//            syncAllAccountsPeriodically(mContext,1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
//    public void syncAllAccountsPeriodically(Context contextAct, long seconds) throws Exception {
//        AccountManager manager = AccountManager.get(contextAct);
//        Account[] accounts = manager.getAccountsByType("com.example.shashidhar.may17th_1");
//        String accountName = "";
//        String accountType = "";
//        for (Account account : accounts) {
//            accountName = account.name;
//            accountType = account.type;
//            break;
//        }
//        Account a = new Account(accountName, accountType);
//        ContentResolver.addPeriodicSync(a, "com.example.shashidhar.may17th_1", new Bundle(), seconds*1000);
//    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Sync Adapter called." + account.name);
        System.out.println("Sync Adapter called");
//       responseString=ob.gg();
        Sender sender = new Sender();
        JSONObject o = new JSONObject();
        JSONObject oo = new JSONObject();
        DbHelper mydb = new DbHelper(mContext);
        q = mydb.get_lib_user_id(mContext);
        if (q != null) {
            try {
                oo.put("userid", q);
                o.put("type", "showdata");
                o.put("data", oo);
            } catch (Exception e) {
            }
            message1 = o.toString();
            sender.execute();
        }
    }

    class Sender extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println("[SYNC LIB] After sending request ");
        }

        @Override
        protected Void doInBackground(String... params) {
            System.out.println("Main sender");
            try {
                if (message1 != null) {
                    ConnectionFactory factory = new ConnectionFactory();
                    try {
                        factory.setUri(url);
                        factory.setUsername("kreatio");
                        factory.setPassword("kreatio");
                        factory.setVirtualHost("androidapp");
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();
                        try {
                            channel.basicPublish("", "addnote", null, message1.getBytes());
                            System.out.println(" [x] Sent '" + message1 + "'");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (ConnectException e) {
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
