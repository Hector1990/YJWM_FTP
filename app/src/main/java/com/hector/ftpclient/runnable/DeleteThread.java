package com.hector.ftpclient.runnable;

import android.os.Handler;

import com.hector.ftpclient.Client;
import com.hector.ftpclient.Operation;
import com.hector.ftpclient.Status;

import it.sauronsoftware.ftp4j.FTPClient;

/**
 * Created by Hector on 15/12/22.
 */
public class DeleteThread extends Thread {

    private String mFileName;
    private Handler mHandler;

    public DeleteThread(Handler handler, String fileName) {
        this.mHandler = handler;
        this.mFileName = fileName;
    }

    @Override
    public void run() {
        super.run();
        FTPClient client = Client.newInstance();
        if (!client.isConnected()) {
            mHandler.sendEmptyMessage(Status.DISCONNECTED);
            return;
        }
        Operation operation = new Operation();
        if (operation.deleteFile(client, mFileName)) {
            mHandler.sendEmptyMessage(Status.DELETE_SUCCESS);
        } else {
            mHandler.sendEmptyMessage(Status.DELETE_FAIL);
        }
    }
}
