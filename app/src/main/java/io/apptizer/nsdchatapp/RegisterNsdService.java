package io.apptizer.nsdchatapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class RegisterNsdService extends Service {
    public static final String TAG = "RegisterNsdService";
    RegisterService registerService;
    NsdManager mNsdManager;
    Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "=======================Started========================");

        context = this;
        mNsdManager = (NsdManager) this.getSystemService(Context.NSD_SERVICE);
        String mServiceName = intent.getStringExtra("mServiceName");
        String mServiceType = intent.getStringExtra("mServiceType");
        int mPort = intent.getIntExtra("mPort", 0);

        registerService = new RegisterService(this, mNsdManager, mPort, mServiceName, mServiceType, new AsyncTaskCallback() {
            @Override
            public void onTaskCompleted(Object response) throws IOException {
                Log.d(TAG, "onTaskCompleted");
                Log.d(TAG, response.toString());

                Intent local = new Intent();
                local.setAction("update.action.button");
                local.putExtra("addContentButtonVisibility", true);
                context.sendBroadcast(local);

                Thread socketPoolThread = new Thread(SocketPoolThread.getSocketPoolThread());
                socketPoolThread.start();
            }
        });

        registerService.initiate();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
