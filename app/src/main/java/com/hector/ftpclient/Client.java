package com.hector.ftpclient;

import it.sauronsoftware.ftp4j.FTPClient;

/**
 * Created by Hector on 15/12/21.
 */
public class Client {

    private static FTPClient mClient;

    private Client() {
    }

    public static FTPClient newInstance() {
        if (mClient == null) {
            synchronized (FTPClient.class) {
                if (mClient == null) {
                    mClient = new FTPClient();
                }
            }
        }
        return mClient;
    }

}
