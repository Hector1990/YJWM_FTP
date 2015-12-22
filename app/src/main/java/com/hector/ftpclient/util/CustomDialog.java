package com.hector.ftpclient.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hector.ftpclient.R;

/**
 * Created by Hector on 15/12/21.
 */
public class CustomDialog extends DialogFragment implements View.OnClickListener {

    public final static int DIALOG_MESSAGE = 0;
    public final static int DIALOG_INPUT = 1;
    private final static int DIALOG_LIST = 2;

    private int mMode;

    private CustomDialogListener mListener;
    private DialogListItemListener mDialogListItemListener;
    private EditText mEditText;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_negative:
                if (mListener != null) {
                    mListener.onDialogNegativeClick(this);
                }
                break;
            case R.id.bt_positive:
                if (mEditText == null) {
                    mListener.onDialogPositiveClick(this, null);
                } else {
                    if (TextUtils.isEmpty(mEditText.getText().toString())) {
                        return;
                    }
                    if (mListener != null) {
                        mListener.onDialogPositiveClick(this, mEditText.getText().toString());
                    }
                }
                break;
        }
    }

    public interface CustomDialogListener {
        void onDialogPositiveClick(DialogFragment dialogFragment, String text);

        void onDialogNegativeClick(DialogFragment dialogFragment);
    }

    public interface DialogListItemListener {
        void onDialogListItemClick(DialogFragment dialogFragment, int which);
    }

    public static CustomDialog newInstance(String mTitle, String mMessage, String positiveButtonText, String negativeButtonText, int mMode) {
        CustomDialog customDialog = new CustomDialog();
        Bundle args = new Bundle();
        args.putString("title", mTitle);
        args.putString("message", mMessage);
        args.putString("positive", positiveButtonText);
        args.putInt("mode", mMode);
        if (negativeButtonText != null) {
            args.putString("negative", negativeButtonText);
            args.putInt("count", 2);
        } else {
            args.putInt("count", 1);
        }
        customDialog.setArguments(args);
        return customDialog;
    }

    public static CustomDialog newInstance(String[] items) {
        CustomDialog customDialog = new CustomDialog();
        Bundle args = new Bundle();
        args.putInt("mode", DIALOG_LIST);
        args.putStringArray("items", items);
        customDialog.setArguments(args);
        return customDialog;
    }

    public CustomDialog() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mMode = getArguments().getInt("mode");
        switch (mMode) {
            case DIALOG_LIST:
                String[] items = getArguments().getStringArray("items");
                return createListDialog(builder, items);
            case DIALOG_INPUT:
                return createInputDialog(builder);
            default:
                return createMessageDialog(builder);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CustomDialogListener) activity;
        } catch (ClassCastException e) {
        }
    }

    public void setListener(CustomDialogListener listener) {
        mListener = listener;
    }

    public void setDialogListItemListener(DialogListItemListener listener) {
        mDialogListItemListener = listener;
    }


    public Dialog createListDialog(AlertDialog.Builder builder, String[] items) {
//        builder.setInverseBackgroundForced(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mDialogListItemListener != null) {
                    mDialogListItemListener.onDialogListItemClick(CustomDialog.this, which);
                }
            }
        });
        AlertDialog dialog = builder.create();
        int width = (getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth() / 6) * 5;
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public Dialog createMessageDialog(AlertDialog.Builder builder) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_custom_message, null);
        int buttonCount = getArguments().getInt("count");
        TextView positiveButton = (TextView) view.findViewById(R.id.bt_positive);
        positiveButton.setText(getArguments().getString("positive"));
        positiveButton.setOnClickListener(this);
        if (buttonCount == 1) {
            view.findViewById(R.id.bt_negative).setVisibility(View.GONE);
        } else {
            TextView negativeButton = (TextView) view.findViewById(R.id.bt_negative);
            negativeButton.setVisibility(View.VISIBLE);
            negativeButton.setText(getArguments().getString("negative"));
            negativeButton.setOnClickListener(this);
        }
        ((TextView) view.findViewById(R.id.dialog_title)).setText(getArguments().getString("title"));
        TextView messageTv = (TextView) view.findViewById(R.id.dialog_message);
        messageTv.setText(getArguments().getString("message"));
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        int width = (getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth() / 6) * 5;
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    ;

    public Dialog createInputDialog(AlertDialog.Builder builder) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_custom_input, null);
        int buttonCount = getArguments().getInt("count");
        TextView positiveButton = (TextView) view.findViewById(R.id.bt_positive);
        positiveButton.setText(getArguments().getString("positive"));
        positiveButton.setOnClickListener(this);
        if (buttonCount == 1) {
            view.findViewById(R.id.bt_negative).setVisibility(View.GONE);
        } else {
            TextView negativeButton = (TextView) view.findViewById(R.id.bt_negative);
            negativeButton.setVisibility(View.VISIBLE);
            negativeButton.setText(getArguments().getString("negative"));
            negativeButton.setOnClickListener(this);
        }
        ((TextView) view.findViewById(R.id.dialog_title)).setText(getArguments().getString("title"));
        String content = getArguments().getString("message");
        mEditText = (EditText) view.findViewById(R.id.dialog_message);
        if (!TextUtils.isEmpty(content)) {
            mEditText.setText(content);
            mEditText.setSelection(content.length());
        }
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        int width = (getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth() / 6) * 5;
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

}
