package com.webnish.reach.contacts;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by shashidhar on 17/5/16.
 */
public class ContactsManager extends Activity {

    static Context c=null;
    public static void addContact(Context context, String number,String name,byte[] photo){
        c=context;
        InputStream is=null;

        if(hasAccount(c,number));
        else {
            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
            ContentResolver resolver = context.getContentResolver();

            //  Create our RawContact
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(ContactsContract.RawContacts.CONTENT_URI, true));
            builder.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, Constants.ACCOUNT_NAME);
            builder.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
            operationList.add(builder.build());

            //  Create a Data record of common type 'Phone' for our RawContact
            builder = ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true));
            builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
            builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
            operationList.add(builder.build());


            if (name != null && !(name.length() == 0)) {
                System.out.println("New Contact");
                builder = ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true));
                builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
                builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
                operationList.add(builder.build());
            }
            if(photo!=null && !(photo.length==0) ){
                builder = ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true));
                builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
                builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
                builder.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, photo);
                operationList.add(builder.build());
            }else{
                byte[] p=null;
                is=null;
                long id=0;
                id=getId(number,context);
                if(id>0) {
                    is = openDisplayPhoto(context, id);
                    System.out.println("[LIBRARY] 1 ");
                    if (is != null) {
                        System.out.println("LARGE IMAGE ");
                        p = InputStreamToByte(is);
//                    p=Base64.decode(p, Base64.DEFAULT);
                        builder = ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true));
                        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
                        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
                        builder.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, p);
                        operationList.add(builder.build());
                    } else {
                        System.out.println("[LIBRARY] 2 ");
                        is = openPhoto(context, id);
                        if (is != null) {
                            System.out.println("CACHE IMAGE ");
                            p = InputStreamToByte(is);
//                        p=Base64.decode(p, Base64.DEFAULT);
                            builder = ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true));
                            builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
                            builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
                            builder.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, p);
                            operationList.add(builder.build());
                        }
                    }
                }
            }

            String num=null;
            num=FormatPhoneNumber(number);
            if(num!=null)
                number=num;
            System.out.println("NUM "+num);
            //Create a Data record of custom type "vnd.android.cursor.item/vnd.be.ourservice.profile" to display a link to our     profile
            builder = ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true));
            builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
            builder.withValue(ContactsContract.Data.MIMETYPE, Constants.MIMETYPE);
            builder.withValue(ContactsContract.Data.DATA1, number);
            builder.withValue(ContactsContract.Data.DATA5, "Webnish");
            builder.withValue(ContactsContract.Data.DATA3, Constants.ACCOUNT_NAME + " " + number);
            operationList.add(builder.build());

            try {
                resolver.applyBatch(ContactsContract.AUTHORITY, operationList);
                System.out.println("addContact batch applied");
            } catch (Exception e) {
                System.out.println("Something went wrong during creation! " + e);
                e.printStackTrace();
            }
        }
    }

    public static long getId(String contactNumber,Context context)
    {
        contactNumber = Uri.encode(contactNumber);
        int phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,contactNumber),new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        while(contactLookupCursor.moveToNext()){
            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();
        return phoneContactID;
    }

    public static InputStream openPhoto(Context context,long cid) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, cid);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }
    public static InputStream openDisplayPhoto(Context context,long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =context.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return fd.createInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public static byte[] InputStreamToByte(InputStream is){
        byte[] bytes=null;
        try {
            bytes = IOUtils.toByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private static Uri addCallerIsSyncAdapterParameter(Uri uri, boolean isSyncOperation) {
        if (isSyncOperation) {
            return uri.buildUpon()
                    .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
                    .build();
        }
        return uri;
    }


    public static String FormatPhoneNumber(String no) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(no, "IN");
            if(phoneUtil.isValidNumber(swissNumberProto)) {
                no=phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                return no;
            }
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
        return null;
    }
//    public static String getContactPhoneNumber(Context context,String number) {
//        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
//        String name = "?",phone_number=null;
//
//        ContentResolver contentResolver = context.getContentResolver();
//        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
//                ContactsContract.CommonDataKinds.Phone.NUMBER }, null, null, null);
//
//        try {
//            if (contactLookup != null && contactLookup.getCount() > 0) {
//                contactLookup.moveToNext();
////                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
//                switch (contactLookup.getInt(contactLookup.getColumnIndex("data2"))) {
//                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                        phone_number = contactLookup.getString(contactLookup.getColumnIndex("data1"));
//                        break;
//                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                        phone_number = contactLookup.getString(contactLookup.getColumnIndex("data1"));
//                        break;
//                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                        phone_number = contactLookup.getString(contactLookup.getColumnIndex("data1"));
//                        break;
//                }
//                if(PhoneNumberUtils.compare(number,phone_number))
//                    return phone_number;
//                else
//                    return number;
//                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
//            }
//        } finally {
//            if (contactLookup != null) {
//                contactLookup.close();
//            }
//        }
//
//        return null;
//    }

    private static boolean hasAccount(Context context, String no){

        Cursor c = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[] { ContactsContract.RawContacts.CONTACT_ID, ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY },
                ContactsContract.RawContacts.ACCOUNT_TYPE + " = ? AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[] {Constants.ACCOUNT_TYPE,no},
                null);
        if(c!=null && c.moveToFirst())
            return true;
        if(c!=null)
            c.close();
        return false;
    }
}

