package com.webnish.reach.contacts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by shashidhar on 28/5/16.
 */
public class Main extends Activity {
    private String AUTHORITY="com.android.contacts";

    Context mContext1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public Account getAccount(Context context){
        this.mContext1 = context;
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        AccountManager mAccountManager = (AccountManager) mContext1.getSystemService(Context.ACCOUNT_SERVICE);
        if(mAccountManager.addAccountExplicitly(account, null, null)){
            System.out.println("Account has been created");
            ContentResolver.setMasterSyncAutomatically(true);
            ContentResolver.setSyncAutomatically(account,AUTHORITY,true);
            ContentResolver.setIsSyncable(account,AUTHORITY,1);
        }
        return account;
    }
}
