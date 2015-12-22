package com.hector.ftpclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hector.ftpclient.util.DateUtil;
import com.hector.ftpclient.util.FileSizeUtil;
import com.hector.ftpclient.R;

import java.io.File;
import java.util.List;

/**
 * Created by Hector on 15/12/22.
 */
public class LocalFileListAdapter extends RecyclerView.Adapter<LocalFileListAdapter.ViewHolder> {

    private List<File> mData;

    public LocalFileListAdapter(List<File> mData) {
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, parent.getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File file = mData.get(position);
        if (file.isDirectory()) {
            holder.mFileIconIv.setImageResource(R.drawable.tag_folder);
            holder.mFileSizeTv.setText("");
        }
        else {
            holder.mFileIconIv.setImageResource(R.drawable.tag_file);
            try {
                holder.mFileSizeTv.setText(FileSizeUtil.customeFileSize(FileSizeUtil.getFileSize(file)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        holder.mFileTitleTv.setText(file.getName());

        holder.mFileTimeTv.setText(DateUtil.parseDate(file.lastModified()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void refresh(List<File> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mFileTitleTv, mFileSizeTv, mFileTimeTv;
        public ImageView mFileIconIv;

        public ViewHolder(View view, Context context){
            super(view);
            mFileIconIv = (ImageView) view.findViewById(R.id.file_icon_iv);
            mFileTitleTv = (TextView) view.findViewById(R.id.file_title_tv);
            mFileSizeTv = (TextView) view.findViewById(R.id.file_size_tv);
            mFileTimeTv = (TextView) view.findViewById(R.id.file_time_tv);
            final OnItemClickListener onItemClickListener = (OnItemClickListener) context;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getLayoutPosition() - 1);
                }
            });
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
    }
}
