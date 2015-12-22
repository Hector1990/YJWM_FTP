package com.hector.ftpclient.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.hector.ftpclient.util.CustomDialog;
import com.hector.ftpclient.util.FileSizeUtil;
import com.hector.ftpclient.adapter.LocalFileListAdapter;
import com.hector.ftpclient.util.ProgressDialogUtil;
import com.hector.ftpclient.R;
import com.hector.ftpclient.Status;
import com.hector.ftpclient.runnable.UploadFileThread;
import com.hector.recyclerview.HectorRecyclerView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BrowseLocalActivity extends AppCompatActivity implements LocalFileListAdapter.OnItemClickListener {

    private String mPath = "/";
    private String mOldPath;
    private HectorRecyclerView mRecyclerView;
    private List<File> mData;
    private LocalFileListAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private long mFileSize;
    private int mProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_local);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (HectorRecyclerView) findViewById(R.id.recycler_view);
        getFiles();
        mAdapter = new LocalFileListAdapter(mData);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setLoadMoreEnabled(false);
        mRecyclerView.setRefreshEnabled(false);
        mRecyclerView.setAdapter(mAdapter);
        registerReceiver(mReceiver, new IntentFilter(Status.ACTION_DISCONNECTED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mPath.equals("/")) {
                finish();
            } else {
                goToParent();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getFiles() {
        if (new File(mPath).list() != null) {
            mData = Arrays.asList(new File(mPath).listFiles());
        } else {
            mPath = mOldPath;
            Toast.makeText(this, getString(R.string.open_folder_fail), Toast.LENGTH_LONG).show();
        }
    }

    private void goToParent() {
        if (!mPath.equals("/")) {
            File file = new File(mPath);
            File newFile = file.getParentFile();
            if (newFile != null) {
                mPath = newFile.getAbsolutePath();
                getFiles();
                mAdapter.refresh(mData);
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        File file = mData.get(position);
        mOldPath = mPath;
        if (file.isDirectory()) {
            mPath = file.getAbsolutePath();
            getFiles();
            mAdapter.refresh(mData);
        } else {
            createMessageDialog(file, file.getName());
        }
    }

    private void createMessageDialog(final File file, String fileName) {
        CustomDialog customDialog = CustomDialog.newInstance(getString(R.string.dialog_upload_title),
                getString(R.string.message_upload) + " " + fileName + "?", getString(R.string.dialog_ok), getString(R.string.dialog_cancel), CustomDialog.DIALOG_MESSAGE);
        customDialog.setListener(new CustomDialog.CustomDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialogFragment, String text) {
                dialogFragment.dismiss();
                try {
                    mFileSize = FileSizeUtil.getFileSize(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new UploadFileThread(mHandler, file).start();
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialogFragment) {
                dialogFragment.dismiss();
            }
        });
        customDialog.show(getSupportFragmentManager(), "message");

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Status.UPLOAD_START:
                    mProgressDialog = ProgressDialogUtil.showHorizontalDialog(BrowseLocalActivity.this, getString(R.string.dialog_upload_title), getString(R.string.progress_uploading));
                    break;
                case Status.UPLOAD_TRANSFER:
                    mProgress += msg.arg1;
                    mProgressDialog.setProgress((int) (((double) mProgress / (double) mFileSize) * 100));

                    break;
                case Status.UPLOAD_SUCCESS:
                    mProgressDialog.dismiss();
                    Toast.makeText(BrowseLocalActivity.this, getString(R.string.upload_success), Toast.LENGTH_LONG).show();
                    break;
                case Status.UPLOAD_FAIL:
                    mProgressDialog.dismiss();
                    Toast.makeText(BrowseLocalActivity.this, getString(R.string.upload_fail), Toast.LENGTH_LONG).show();
                    break;
                case Status.DISCONNECTED:
                    Intent intent = new Intent(BrowseLocalActivity.this, MainActivity.class);
                    startActivity(intent);
                    sendBroadcast(new Intent(Status.ACTION_DISCONNECTED));
                    break;
            }
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Status.ACTION_DISCONNECTED.equals(intent.getAction())) {
                finish();
            }
        }
    };

}
