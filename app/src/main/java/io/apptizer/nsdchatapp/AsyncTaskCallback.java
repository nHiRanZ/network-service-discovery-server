package io.apptizer.nsdchatapp;

import java.io.IOException;

public interface AsyncTaskCallback {
    void onTaskCompleted(Object response) throws IOException;

//    void onTaskCompleted(Object response);
}
