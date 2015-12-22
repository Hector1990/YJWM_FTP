package com.hector.ftpclient.runnable;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.hector.ftpclient.Client;
import com.hector.ftpclient.Status;

import java.io.File;
import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

/**
 * Created by Hector on 15/12/22.
 */
public class UploadFileThread extends Thread {

    private File mFile;
    private Handler mHandler;
    private boolean mUpload;
    private String mFileName;

    public UploadFileThread(Handler handler, File file) {
        this.mFile = file;
        this.mHandler = handler;
        this.mUpload = true;
    }

    public UploadFileThread(Handler handler, String fileName) {
        this.mHandler = handler;
        this.mFileName = fileName;
        this.mUpload = false;
    }

    @Override
    public void run() {
        super.run();
        FTPClient client = Client.newInstance();
        if (!client.isConnected()) {
            mHandler.sendEmptyMessage(Status.DISCONNECTED);
            return;
        }
        try {
            if (mUpload) {
                client.upload(mFile, new FTPDataTransferListener() {
                    @Override
                    public void started() {
                        mHandler.sendEmptyMessage(Status.UPLOAD_START);
                    }

                    @Override
                    public void transferred(int i) {
                        Message message = new Message();
                        message.what = Status.UPLOAD_TRANSFER;
                        message.arg1 = i;
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void completed() {
                        mHandler.sendEmptyMessage(Status.UPLOAD_SUCCESS);
                    }

                    @Override
                    public void aborted() {
                        mHandler.sendEmptyMessage(Status.UPLOAD_FAIL);
                    }

                    @Override
                    public void failed() {
                        mHandler.sendEmptyMessage(Status.UPLOAD_FAIL);
                    }
                });
            } else {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), mFileName);
                client.download(mFileName, file, new FTPDataTransferListener() {
                    @Override
                    public void started() {
                        mHandler.sendEmptyMessage(Status.DOWNLOAD_START);
                    }

                    @Override
                    public void transferred(int i) {
                        Message message = new Message();
                        message.what = Status.DOWNLOAD_TRANSFER;
                        message.arg1 = i;
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void completed() {
                        mHandler.sendEmptyMessage(Status.DOWNLOAD_SUCCESS);
                    }

                    @Override
                    public void aborted() {
                        mHandler.sendEmptyMessage(Status.DOWNLOAD_FAIL);
                    }

                    @Override
                    public void failed() {
                        mHandler.sendEmptyMessage(Status.DOWNLOAD_FAIL);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            sendFailMessage();
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            sendFailMessage();
        } catch (FTPException e) {
            e.printStackTrace();
            sendFailMessage();
        } catch (FTPDataTransferException e) {
            e.printStackTrace();
            sendFailMessage();
        } catch (FTPAbortedException e) {
            e.printStackTrace();
            sendFailMessage();
        }
    }

    private void sendFailMessage() {
        if (mUpload) {
            mHandler.sendEmptyMessage(Status.UPLOAD_FAIL);
        } else {
            mHandler.sendEmptyMessage(Status.DOWNLOAD_FAIL);
        }
    }
}
