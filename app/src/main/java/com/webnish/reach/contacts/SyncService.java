package com.webnish.reach.contacts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by shashidhar on 17/5/16.
 */
public class SyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static final String TAG = "";
    private static SyncAdapter mSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.i(TAG,"Sync Service created.");
        synchronized (sSyncAdapterLock){
            if(mSyncAdapter == null){
                mSyncAdapter = new SyncAdapter(getApplicationContext(),true);
            }
    }
}
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"Sync Service binded.");
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
