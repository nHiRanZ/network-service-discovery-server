package io.apptizer.nsdchatapp;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.io.IOException;

public class RegisterService implements NsdManager.RegistrationListener {
    private static final String TAG = "RegisterService";
    private AsyncTaskCallback asyncTaskCallback;
    private Context mContext;
    private NsdManager mNsdManager;
    private int mPort = 0;
    private String mServiceName;
    private String mServiceType;

    public RegisterService(Context mContext, NsdManager mNsdManager, int mPort, String mServiceName, String mServiceType, AsyncTaskCallback asyncTaskCallback) {
        this.asyncTaskCallback = asyncTaskCallback;
        this.mContext = mContext;
        this.mNsdManager = mNsdManager;
        this.mPort = mPort;
        this.mServiceName = mServiceName;
        this.mServiceType = mServiceType;
    }

    @Override
    public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
        Log.d(TAG, "===============================================");
        Log.d(TAG, "Service Registration Failed: " + nsdServiceInfo);
        Log.d(TAG, "Service Registration Failed Error Code: " + i);
        Log.d(TAG, "===============================================");
    }

    @Override
    public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
        Log.d(TAG, "===============================================");
        Log.d(TAG, "Service Un-registration Failed: " + nsdServiceInfo);
        Log.d(TAG, "Service Un-registration Failed Error Code: " + i);
        Log.d(TAG, "===============================================");
    }

    @Override
    public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
        Log.d(TAG, "===============================================");
        Log.d(TAG, "Service Registered Successfully: " + nsdServiceInfo);
        Log.d(TAG, "===============================================");
        try {
            asyncTaskCallback.onTaskCompleted(nsdServiceInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
        Log.d(TAG, "===============================================");
        Log.d(TAG, "Service Un-registered Successfully: " + nsdServiceInfo);
        Log.d(TAG, "===============================================");
    }

    void initiate() {
        Log.d(TAG, "=======================registerService()========================");

        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType(mServiceType);
        serviceInfo.setPort(mPort);

        mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, this);
    }
}
