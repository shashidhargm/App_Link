package com.example.shashidhar.may17th_1;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

/**
 * Created by shashidhar on 28/5/16.
 */
public class Main extends Activity {
    private String mNames=null;
    private String mNumber=null;
    private boolean flag=false;
    DbHelper mydb;
    Context mContext1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void read (String no,Context context){
        String number=no;
        this.mContext1=context;
        getAccount();
        mydb = new DbHelper(mContext1);
        boolean f = getContact(no);
        if (f == false) {
            Intent intent;
            intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, Uri.parse("tel:" + number));
            intent.putExtra(ContactsContract.Intents.EXTRA_FORCE_CREATE, true);
            mContext1.startActivity(intent);
            finish();
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        f = getContact(number);
        System.out.println("after add new contact" +number);
        if (f == true) {
            if (mNames != null) {
                Cursor res = mydb.getData(number);
                if (res.getCount() == 0) {
                    System.out.println("Number is not there in Local DB");
                    ContactsManager.addContact(mContext1, mNumber, mNames);
                    mydb.insertData(mNumber, mNames);
                    System.out.println("Number is added to local DB");
                    return;
                }
            }
        }
    }

    //fetch contact's from device
    private boolean getContact(String no){
        ContentResolver cr = mContext1.getContentResolver();
        //Uri uri=ContactsContract.Contacts.CONTENT_URI;
        Uri uri= Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI,no);
        Cursor cursor = cr.query(uri, null, null, null, null);
        if(cursor.getCount()==0)
            return false;
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        mNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        mNames = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        flag=true;
                        break;
                    }
                    pCur.close();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        if(flag==false) {
            return false;
        }
        else {
            return true;
        }
    }
    public Account getAccount(){
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        AccountManager mAccountManager = AccountManager.get(mContext1);
        ContentResolver.setSyncAutomatically(account,Constants.ACCOUNT_TYPE,true);
        mAccountManager.addAccountExplicitly(account, null, null);
        return account;
    }
}
