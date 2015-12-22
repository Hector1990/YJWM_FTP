package com.hector.ftpclient.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hector.ftpclient.util.ProgressDialogUtil;
import com.hector.ftpclient.R;
import com.hector.ftpclient.Status;
import com.hector.ftpclient.runnable.LoginThread;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText mIpAddressEt, mPortEt, mUsernameEt, mPasswordEt;
    private String mIp, mUsername, mPassword;
    private int mPort = 21;
    private LoginThread mLoginThread;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initConfig();
        initView();
    }

    private void initView() {
        mIpAddressEt = (EditText) findViewById(R.id.et_ip);
        mPortEt = (EditText) findViewById(R.id.et_port);
        mUsernameEt = (EditText) findViewById(R.id.et_username);
        mPasswordEt = (EditText) findViewById(R.id.et_password);
        findViewById(R.id.bt_login).setOnClickListener(this);
        findViewById(R.id.bt_reset).setOnClickListener(this);
        mIpAddressEt.requestFocus();
        mIpAddressEt.setText(mIp);
        mPortEt.setText(mPort + "");
        mUsernameEt.setText(mUsername);
        mPasswordEt.setText(mPassword);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                login();
                break;
            case R.id.bt_reset:
                reset();
                break;
        }
    }

    private void login() {
        if (TextUtils.isEmpty(mIpAddressEt.getText())) {
            return;
        }
        if (!TextUtils.isDigitsOnly(mPortEt.getText())) {
            return;
        }
        if (!TextUtils.isEmpty(mPortEt.getText())) {
            mPort = Integer.parseInt(mPortEt.getText().toString());
        }
        mIp = mIpAddressEt.getText().toString();
        mUsername = mUsernameEt.getText().toString();
        mPassword = mPasswordEt.getText().toString();
        saveConfig();
        mProgressDialog = ProgressDialogUtil.showSpinnerDialog(this, getString(R.string.login_in_progress));
        mLoginThread = new LoginThread(mHandler, mIp, mPort, mUsername, mPassword);
        mLoginThread.start();
    }

    private void reset() {
        mIpAddressEt.setText("");
        mPortEt.setText("");
        mUsernameEt.setText("");
        mPasswordEt.setText("");
    }

    private void initConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        mIp = sharedPreferences.getString("ip", "");
        mPort = sharedPreferences.getInt("port", 21);
        mUsername = sharedPreferences.getString("username", "");
        mPassword = sharedPreferences.getString("password", "");
    }

    private void saveConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        sharedPreferences.edit().putString("ip", mIp).commit();
        sharedPreferences.edit().putInt("port", mPort).commit();
        sharedPreferences.edit().putString("username", mUsername).commit();
        sharedPreferences.edit().putString("password", mPassword).commit();
    }

    private Handler mHandler = new Handler(
    ) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Status.LOGIN_SUCCESS:
                    mProgressDialog.cancel();
                    Intent intent = new Intent(MainActivity.this, BrowseFileActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case Status.LOGIN_FAIL:
                    mProgressDialog.cancel();
                    Toast.makeText(MainActivity.this, getString(R.string.login_fail_toast), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
}
