package com.hector.ftpclient.runnable;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hector.ftpclient.Client;
import com.hector.ftpclient.Operation;
import com.hector.ftpclient.Status;

import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPFile;

/**
 * Created by Hector on 15/12/21.
 */
public class GetFilesThread extends Thread {

    private String mDirectoryName;
    private Handler mHandler;
    private int mMode;

    public GetFilesThread(Handler handler, String directoryName, int mode) {
        this.mHandler = handler;
        this.mDirectoryName = directoryName;
        this.mMode = mode;
    }

    @Override
    public void run() {
        FTPClient client = Client.newInstance();
        if (!client.isConnected()) {
            mHandler.sendEmptyMessage(Status.DISCONNECTED);
            return;
        }
        Operation operation = new Operation();
        Message message = new Message();
        List<FTPFile> files = new ArrayList<>();
        if (mMode == 0) {
            if (mDirectoryName == null) {
                files = operation.getFiles(client);
            } else {
                files = operation.getFiles(client, mDirectoryName);
            }
        } else {
            files = operation.getParentFiles(client);
        }
        if (files != null) {
            if (mMode == 0) {
                message.what = Status.GET_FILES_SUCCESS;
                if (mDirectoryName == null) {
                    message.arg1 = 0;
                } else {
                    message.arg1 = 1;
                }
            } else {
                message.what = Status.BACK_SUCCESS;
            }
            ArrayList<String> list = new ArrayList<>();
            for (FTPFile file : files) {
                list.add(file.toString());
            }
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("list", list);
            message.setData(bundle);
            mHandler.sendMessage(message);
        } else {
            mHandler.sendEmptyMessage(Status.GET_FILES_FAIL);
        }
    }
}
