package com.hector.ftpclient.runnable;

import android.os.Handler;

import com.hector.ftpclient.Client;
import com.hector.ftpclient.Operation;
import com.hector.ftpclient.Status;

import it.sauronsoftware.ftp4j.FTPClient;

/**
 * Created by Hector on 15/12/22.
 */
public class UpdateFileNameThread extends Thread {

    private String oldName, newName;
    private Handler mHandler;

    public UpdateFileNameThread(Handler handler, String oldName, String newName) {
        this.mHandler = handler;
        this.oldName = oldName;
        this.newName = newName;
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
        if (operation.updateName(client, oldName, newName)) {
            mHandler.sendEmptyMessage(Status.UPDATE_SUCCESS);
        } else {
            mHandler.sendEmptyMessage(Status.UPDATE_FAIL);
        }
    }


}
