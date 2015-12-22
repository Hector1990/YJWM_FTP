package com.hector.ftpclient.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Hector on 15/12/21.
 */
public class ProgressDialogUtil {

    public static ProgressDialog showSpinnerDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.show();
        return progressDialog;
    }

    public static ProgressDialog showHorizontalDialog(Context context, String title, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        return progressDialog;
    }

}
