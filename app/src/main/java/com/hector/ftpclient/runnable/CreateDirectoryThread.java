package com.hector.ftpclient.runnable;

import android.os.Handler;

import com.hector.ftpclient.Client;
import com.hector.ftpclient.Operation;
import com.hector.ftpclient.Status;

import it.sauronsoftware.ftp4j.FTPClient;

/**
 * Created by Hector on 15/12/21.
 */
public class CreateDirectoryThread extends Thread {

    private String mDirName;
    private Handler mHandler;

    public CreateDirectoryThread(Handler handler, String dirName) {
        this.mHandler = handler;
        this.mDirName = dirName;
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
        if (operation.newDir(client, mDirName)) {
            mHandler.sendEmptyMessage(Status.NEW_DIR_SUCCESS);
        } else {
            mHandler.sendEmptyMessage(Status.NEW_DIR_FAIL);
        }
    }
}
