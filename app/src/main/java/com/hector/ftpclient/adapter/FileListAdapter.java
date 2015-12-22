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

import java.util.List;

/**
 * Created by Hector on 15/12/21.
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    List<String> data;

    public FileListAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, parent.getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] fileInfo = data.get(position).split(",");
        String[] fileName = fileInfo[0].split("=");
        String[] fileType = fileInfo[1].split("=");
        String[] fileSize = fileInfo[2].split("=");
        String[] fileTime = fileInfo[3].split("=");
        holder.mFileTitleTv.setText(fileName[1]);
        holder.mFileSizeTv.setText(FileSizeUtil.customeFileSize(Long.parseLong(fileSize[1])));
        holder.mFileTimeTv.setText(DateUtil.parseDate(fileTime[1]));
        if (fileType.length == 1 || fileType[1].equals("FILE")) {
            holder.mFileIconIv.setImageResource(R.drawable.tag_file);
        }
        else {
            holder.mFileIconIv.setImageResource(R.drawable.tag_folder);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void refresh(List<String> data) {
        this.data = data;
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
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onItemLongClick(getLayoutPosition() - 1);
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
        public void onItemLongClick(int position);
    }
}
