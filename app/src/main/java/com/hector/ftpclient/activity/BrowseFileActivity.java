package com.hector.ftpclient.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hector.ftpclient.Client;
import com.hector.ftpclient.util.CustomDialog;
import com.hector.ftpclient.adapter.FileListAdapter;
import com.hector.ftpclient.util.ProgressDialogUtil;
import com.hector.ftpclient.R;
import com.hector.ftpclient.Status;
import com.hector.ftpclient.runnable.CreateDirectoryThread;
import com.hector.ftpclient.runnable.DeleteThread;
import com.hector.ftpclient.runnable.GetFilesThread;
import com.hector.ftpclient.runnable.UpdateFileNameThread;
import com.hector.ftpclient.runnable.UploadFileThread;
import com.hector.recyclerview.HectorRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

public class BrowseFileActivity extends AppCompatActivity implements HectorRecyclerView.RefreshListener, FileListAdapter.OnItemClickListener {

    private HectorRecyclerView mRecyclerView;
    private List<String> mData;
    private FileListAdapter mAdapter;
    private String mCurrentDir;
    private Stack<String> mStack;
    private ProgressDialog mProgressDialog;
    private int mProgress;
    private long mFileSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_file);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.menu_back);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BrowseFileActivity.this, BrowseLocalActivity.class);
                startActivity(intent);
            }
        });

        initView();
        registerReceiver(mReceiver, new IntentFilter(Status.ACTION_DISCONNECTED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_browse_file, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_exit) {
            exit();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            new GetFilesThread(mHandler, null, 1).start();
            return true;
        }
        if (item.getItemId() == R.id.action_new_folder) {
            showInputDialog(getString(R.string.dialog_new_dir_title), "", 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void refresh() {
        new GetFilesThread(mHandler, null, 0).start();
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void onItemClick(int position) {
        String[] fileInfo = mData.get(position).split(",");
        String[] fileType = fileInfo[1].split("=");
        String[] fileName = fileInfo[0].split("=");
        if (fileType.length > 1 && fileType[1].equals("DIRECTORY")) {
            mCurrentDir = fileName[1];
            new GetFilesThread(mHandler, mCurrentDir, 0).start();
        }
    }

    @Override
    public void onItemLongClick(int position) {
        showLongClickDialog(position);
    }

    private void initView() {
        mRecyclerView = (HectorRecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setListener(this);
        mData = new ArrayList<>();
        mAdapter = new FileListAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLoadMoreEnabled(false);
        mStack = new Stack<>();
        new GetFilesThread(mHandler, null, 0).start();
    }

    private void exit() {
        FTPClient client = Client.newInstance();
        try {
            client.disconnect(false);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
        } catch (FTPException e) {
            e.printStackTrace();
        }
    }

    private void showInputDialog(String title, final String message, final int mode) {
        final CustomDialog customDialog = CustomDialog.newInstance(title, message,
                getString(R.string.dialog_ok), getString(R.string.dialog_cancel), CustomDialog.DIALOG_INPUT);
        customDialog.setListener(new CustomDialog.CustomDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialogFragment, String text) {
                customDialog.dismiss();
                if (mode == 0) {
                    mProgressDialog = ProgressDialogUtil.showSpinnerDialog(BrowseFileActivity.this, getString(R.string.progress_loading));
                    new CreateDirectoryThread(mHandler, text).start();
                } else {
                    new UpdateFileNameThread(mHandler, message, text).start();
                }
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialogFragment) {
                customDialog.dismiss();
            }
        });
        customDialog.show(getSupportFragmentManager(), "new folder");
    }

    private void showMessageDialog(String title, final String fileName, final int mode) {
        String message = "";
        if (mode == 0) {
            message = getString(R.string.message_delete) + " " + fileName + "?";
        } else if (mode == 2) {
            message = getString(R.string.message_download) + " " + fileName + "?";
        }
        final CustomDialog customDialog = CustomDialog.newInstance(title, message, getString(R.string.dialog_ok),
                getString(R.string.dialog_cancel), CustomDialog.DIALOG_MESSAGE);
        customDialog.setListener(new CustomDialog.CustomDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialogFragment, String text) {
                customDialog.dismiss();
                if (mode == 0) {
                    mProgressDialog = ProgressDialogUtil.showSpinnerDialog(BrowseFileActivity.this, getString(R.string.progress_loading));
                    new DeleteThread(mHandler, fileName).start();
                }
                if (mode == 2) {
                    new UploadFileThread(mHandler, fileName).start();
                }
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialogFragment) {
                customDialog.dismiss();
            }
        });
        customDialog.show(getSupportFragmentManager(), "message dialog");
    }

    private void showLongClickDialog(final int position) {
        String[] fileInfo = mData.get(position).split(",");
        String[] fileType = fileInfo[1].split("=");
        final String[] fileName = fileInfo[0].split("=");
        final String[] fileSize = fileInfo[2].split("=");
        String[] items;
        if (fileType.length > 1 && fileType[1].equals("DIRECTORY")) {
            items = new String[]{getString(R.string.tag_update)};
        } else {
            items = new String[]{getString(R.string.tag_update), getString(R.string.tag_delete), getString(R.string.tag_download)};
        }
        final CustomDialog customDialog = CustomDialog.newInstance(items);
        customDialog.setDialogListItemListener(new CustomDialog.DialogListItemListener() {
            @Override
            public void onDialogListItemClick(DialogFragment dialogFragment, int which) {
                customDialog.dismiss();
                switch (which) {
                    case 0:
                        showInputDialog(getString(R.string.dialog_update_title), fileName[1], 1);
                        break;
                    case 1:
                        showMessageDialog(getString(R.string.dialog_delete_title), fileName[1], 0);
                        break;
                    case 2:
                        showMessageDialog(getString(R.string.dialog_download_title), fileName[1], 2);
                        mFileSize = Long.parseLong(fileSize[1]);
                }
            }
        });
        customDialog.show(getSupportFragmentManager(), "long click");
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Status.ACTION_DISCONNECTED.equals(intent.getAction())) {
                finish();
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle;
            switch (msg.what) {
                case Status.GET_FILES_SUCCESS:
                    bundle = msg.getData();
                    mData = bundle.getStringArrayList("list");
                    mAdapter.refresh(mData);
                    mRecyclerView.refreshComplete();
                    if (msg.arg1 == 1) {
                        mStack.push("folder");
                    }
                    if (mStack.size() >= 1) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    } else {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                    break;
                case Status.BACK_SUCCESS:
                    bundle = msg.getData();
                    mData = bundle.getStringArrayList("list");
                    mAdapter.refresh(mData);
                    mRecyclerView.refreshComplete();
                    mStack.pop();
                    if (mStack.size() >= 1) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    } else {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                    break;
                case Status.NEW_DIR_SUCCESS:
                    Toast.makeText(BrowseFileActivity.this, getString(R.string.new_dir_success), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    mRecyclerView.autoRefresh();
                    break;
                case Status.NEW_DIR_FAIL:
                    Toast.makeText(BrowseFileActivity.this, getString(R.string.new_dir_fail), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    break;
                case Status.UPDATE_SUCCESS:
                    Toast.makeText(BrowseFileActivity.this, getString(R.string.update_success), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    mRecyclerView.autoRefresh();
                    break;
                case Status.UPDATE_FAIL:
                    Toast.makeText(BrowseFileActivity.this, getString(R.string.update_fail), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    break;
                case Status.DELETE_SUCCESS:
                    Toast.makeText(BrowseFileActivity.this, getString(R.string.delete_success), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    mRecyclerView.autoRefresh();
                    break;
                case Status.DELETE_FAIL:
                    Toast.makeText(BrowseFileActivity.this, getString(R.string.delete_fail), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    break;
                case Status.DOWNLOAD_START:
                    mProgressDialog = ProgressDialogUtil.showHorizontalDialog(BrowseFileActivity.this, getString(R.string.dialog_download_title), getString(R.string.progress_downloading));
                    break;
                case Status.DOWNLOAD_TRANSFER:
                    mProgress += msg.arg1;
                    mProgressDialog.setProgress((int) (((double) mProgress / (double) mFileSize) * 100));
                    break;
                case Status.DOWNLOAD_FAIL:
                    mProgressDialog.dismiss();
                    Toast.makeText(BrowseFileActivity.this, getString(R.string.download_fail), Toast.LENGTH_LONG).show();
                    break;
                case Status.DOWNLOAD_SUCCESS:
                    mProgressDialog.dismiss();
                    Toast.makeText(BrowseFileActivity.this, getString(R.string.download_success), Toast.LENGTH_LONG).show();
                    break;
                case Status.DISCONNECTED:
                    Intent intent = new Intent(BrowseFileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };


}
