package com.webnish.reach.contacts;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by shashidhar on 17/5/16.
 */
public class Authenticator extends AbstractAccountAuthenticator {

    private final Context mContext;
    private final Handler handler = new Handler();

    public Authenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
//        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
//        if (account.type.equals("com.webnish.reach.contacts")) {
//
//        }

        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        AccountManager mAccountManager = (AccountManager) mContext.getSystemService(Context.ACCOUNT_SERVICE);
//        AccountManager mAccountManager =   AccountManager.get(mContext1);
//        if(mAccountManager.addAccountExplicitly(account, null, null)) {
        if (mAccountManager.addAccountExplicitly(account, null, null)) {
            Main main = new Main();
            main.getAccount(mContext);
        } else
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Webnish account is added already", Toast.LENGTH_SHORT).show();
                }
            });

//            Toast.makeText(mContext,"Account is already present",Toast.LENGTH_SHORT).show();
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }
}
