package io.apptizer.nsdchatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.apptizer.nsdchatapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "ServerApp";
    String mServiceName = "BIZ_1whe199y29f_PA";
    final String SERVICE_TYPE = "_http._tcp.";

    Context mContext;
    NsdManager mNsdManager;
    ServerSocket mServerSocket = null;
    int mPort = 0;

    RegisterService registerService;

    NsdServiceInfo mNsdServiceInfo = null;

    int count = 0;
    ArrayList<Order> orders = new ArrayList<>();
    ActivityMainBinding activityMainBinding;

    private Socket socket;

    private ServerSocket serverSocket;

    BroadcastReceiver broadcastReceiver;

    OrderResponse orderResponse = new OrderResponse();

    Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initializeRegistration();
        } catch (IOException e) {
            e.printStackTrace();
        }

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction("update.action.button");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra("addContentButtonVisibility", false)) {
                    activityMainBinding.addContentButton.setVisibility(View.VISIBLE);
                } else {
                    activityMainBinding.addContentButton.setVisibility(View.GONE);
                }
            }
        };
        registerReceiver(broadcastReceiver,filter);
    }

    @Override
    protected void onPause() {
        tearDown();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        tearDown();
        super.onDestroy();
    }

    public void addContent(View v)  {
        Log.d(TAG, "=======================addContent()========================");
        Order order = new Order(Integer.toString(count), "READY");
        orders.add(order);
        count++;

        orderResponse.setOrders(orders);

        String jsonInString = gson.toJson(orderResponse);

        StringBuilder ordersString = new StringBuilder(jsonInString);

//        for (Order selectedOrder : orders) {
//            ordersString.append(selectedOrder.toString()).append("\n");
//        }

        ProcessDataFromServer processDataFromServer = new ProcessDataFromServer(ordersString, serverSocket);
        processDataFromServer.execute();

        Log.d(TAG, "ordersString: " + ordersString);
        Log.d(TAG, "===============================================");

        activityMainBinding.contentContainer.setText(ordersString);
    }

    public void clearContent(View v) {
        Log.d(TAG, "=======================clearContent()========================");
        orders.clear();
        count = 0;

        orderResponse.setOrders(orders);

        String jsonInString = gson.toJson(orderResponse);

        StringBuilder ordersString = new StringBuilder(jsonInString);

        ProcessDataFromServer processDataFromServer = new ProcessDataFromServer(ordersString, serverSocket);
        processDataFromServer.execute();

        ordersString = new StringBuilder("Server App");
        Log.d(TAG, "===============================================");

        activityMainBinding.contentContainer.setText(ordersString);
    }

    /**
     * Initializes Registration of Service
     *
     * @throws IOException
     */
    public void initializeRegistration() throws IOException {
        Log.d(TAG, "=======================initializeRegistration()========================");
        mContext = this.getApplicationContext();
        mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
        mServerSocket = new ServerSocket(0);
        mPort = mServerSocket.getLocalPort();
        mServerSocket.close();

        String ip = getIPAddress(true);
        Log.d(TAG, "IP: " + ip);
        Log.d(TAG, "Port: " + mPort);
        InetAddress serverAddr = InetAddress.getByName(ip);

        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(serverAddr, mPort));

        SocketPoolThread.setServerSocket(serverSocket);

        tearDown();
        Intent intent = new Intent(this, RegisterNsdService.class);
        intent.putExtra("mServiceName", mServiceName);
        intent.putExtra("mServiceType", SERVICE_TYPE);
        intent.putExtra("mPort", mPort);
        this.startService(intent);
//        registerService = new RegisterService(mContext, mNsdManager, mPort, mServiceName, SERVICE_TYPE, new AsyncTaskCallback() {
//            @Override
//            public void onTaskCompleted(Object response) throws IOException {
//                Log.d(TAG, "onTaskCompleted");
//                Log.d(TAG, response.toString());
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        activityMainBinding.addContentButton.setVisibility(View.VISIBLE);
//                    }
//                });
//
//                Intent socketPoolService = new Intent(mContext, RegisterNsdService.class);
//                mContext.startService(socketPoolService);
//            }
//        });
//
//        registerService.initiate();
    }

    public String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    /**
     * Unregisters any existing RegistrationListener
     */
    public void tearDown() {
        Log.d(TAG, "=======================tearDown()========================");
        if (registerService != null) {
            try {
                mNsdManager.unregisterService(registerService);
            } catch (Exception e) {
                Log.d(TAG, "===============================================");
                Log.d(TAG, "Error Occurred: " + e.getMessage());
                Log.d(TAG, "===============================================");
            }
            registerService = null;
        }
    }
}
