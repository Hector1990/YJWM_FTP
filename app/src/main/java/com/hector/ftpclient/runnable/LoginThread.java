package com.hector.ftpclient.runnable;

import android.os.Handler;

import com.hector.ftpclient.Client;
import com.hector.ftpclient.Operation;
import com.hector.ftpclient.Status;

import it.sauronsoftware.ftp4j.FTPClient;


public class LoginThread extends Thread {

    private String mIp, mUsername, mPassword;
    private int mPort;
    private Handler mHandler;

    public LoginThread(Handler handler, String ip, int port, String username, String password) {
        this.mHandler = handler;
        this.mIp = ip;
        this.mPort = port;
        this.mUsername = username;
        this.mPassword = password;
    }

    @Override
    public void run() {
        super.run();
        FTPClient client = Client.newInstance();
        Operation operation = new Operation();
        if (operation.login(client, mIp, mPort, mUsername, mPassword)) {
            mHandler.sendEmptyMessage(Status.LOGIN_SUCCESS);
        } else {
            mHandler.sendEmptyMessage(Status.LOGIN_FAIL);
        }
    }
}
