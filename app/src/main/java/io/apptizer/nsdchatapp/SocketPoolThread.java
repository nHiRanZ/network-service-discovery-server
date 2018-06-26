package io.apptizer.nsdchatapp;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketPoolThread extends Thread {
    private static List<Socket> socketList = new ArrayList<>();
    private static SocketPoolThread socketPoolThread = null;
    private static ServerSocket serverSocket = null;

    private SocketPoolThread() {
        Log.d("SocketPoolThread", "=======================Started========================");
        while (true) {
            if (getServerSocket() != null && !getServerSocket().isClosed()) {
                try {
                    List<Socket> sockets = getSocketList();
                    sockets.add(getServerSocket().accept());
                    setSocketList(sockets);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void setServerSocket(ServerSocket serverSocket) {
        SocketPoolThread.serverSocket = serverSocket;
    }

    public static SocketPoolThread getSocketPoolThread() {
        if (socketPoolThread == null) {
            socketPoolThread = new SocketPoolThread();
        }

        return socketPoolThread;
    }

    public static ServerSocket getServerSocket() {
        return serverSocket;
    }

    public static List<Socket> getSocketList() {
        return socketList;
    }

    public void setSocketList(List<Socket> socketList) {
        this.socketList = socketList;
    }

    @Override
    public synchronized void start() {
        super.start();


    }
}
