package io.apptizer.nsdchatapp;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
    protected Object doInBackground(String... strings) {
        PrintWriter out = null;
        try {
            this.socket = serverSocket.accept();

            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            writer.println(ordersString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }
}
