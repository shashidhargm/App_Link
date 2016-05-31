package com.example.shashidhar.may17th_1;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashidhar on 17/5/16.
 */
public class ContactsManager extends Activity {
    private static String MIMETYPE = "vnd.android.cursor.item/com.example.shashidhar.may17th_1";
    public static final String ACCOUNT_TYPE = "com.example.shashidhar.may17th_1";
    public static final String ACCOUNT_NAME = "Webnish";

    public static void addContact(Context context, String number,String name){

        ContentResolver resolver = context.getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            // insert account name and account type
            ops.add(ContentProviderOperation
                    .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.RawContacts.CONTENT_URI, true))
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, ACCOUNT_NAME)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, ACCOUNT_TYPE)
                    .withValue(ContactsContract.RawContacts.AGGREGATION_MODE, ContactsContract.RawContacts.AGGREGATION_MODE_DEFAULT)
                    .build());
            // insert contact number
            ops.add(ContentProviderOperation
                    .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true))
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .build());
            //insert contact name
            ops.add(ContentProviderOperation
                    .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true))
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());
            // insert mime-type data
            ops.add(ContentProviderOperation
                    .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true))
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, MIMETYPE)
                    .withValue(ContactsContract.Data.DATA1, 12345)
                    .withValue(ContactsContract.Data.DATA2, "user")
                    .withValue(ContactsContract.Data.DATA3, "Webnish")
                    .build());
            try {
                resolver.applyBatch(ContactsContract.AUTHORITY, ops);
                System.out.println("Link is added");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    private static Uri addCallerIsSyncAdapterParameter(Uri uri, boolean isSyncOperation) {
        if (isSyncOperation) {
            return uri.buildUpon()
                    .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
                    .build();
        }
        return uri;
    }
}
