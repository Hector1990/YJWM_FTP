package com.hector.ftpclient;

/**
 * Created by Hector on 15/12/21.
 */
public interface Status {

    public final static int LOGIN_SUCCESS = 0;
    public final static int LOGIN_FAIL = 1;
    public final static int GET_FILES_SUCCESS = 2;
    public final static int GET_FILES_FAIL = 3;
    public final static int BACK_SUCCESS = 4;
    public final static int NEW_DIR_SUCCESS = 5;
    public final static int NEW_DIR_FAIL = 6;
    public final static int UPDATE_SUCCESS = 7;
    public final static int UPDATE_FAIL = 8;
    public final static int DELETE_SUCCESS = 9;
    public final static int DELETE_FAIL = 10;
    public final static int UPLOAD_START = 11;
    public final static int UPLOAD_TRANSFER = 12;
    public final static int UPLOAD_FAIL = 13;
    public final static int UPLOAD_SUCCESS = 14;
    public final static int DOWNLOAD_START = 15;
    public final static int DOWNLOAD_TRANSFER = 16;
    public final static int DOWNLOAD_FAIL = 17;
    public final static int DOWNLOAD_SUCCESS = 18;
    public final static int DISCONNECTED = 19;

    public final static String ACTION_DISCONNECTED = "com.hector.ftpclient.disconnected";

}
