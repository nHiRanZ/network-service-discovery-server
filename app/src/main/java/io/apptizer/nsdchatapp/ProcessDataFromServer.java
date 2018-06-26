package io.apptizer.nsdchatapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.LinearLayout;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ProcessDataFromServer extends AsyncTask<String, Activity, Object> {
    private StringBuilder ordersString;
    private ServerSocket serverSocket;
    private Socket socket;
    private OutputStream output;
    private PrintWriter writer;

    public ProcessDataFromServer(StringBuilder ordersString, ServerSocket serverSocket) {
        this.ordersString = ordersString;
        this.serverSocket = serverSocket;
    }

    @Override
    protected Void doInBackground(String... strings) {
        PrintWriter out = null;
        try {
            for (Socket socket : SocketPoolThread.getSocketList()) {
                output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
                writer.println(ordersString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
