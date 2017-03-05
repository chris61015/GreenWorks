package com.dartmouth.cs.greenworks.Timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dartmouth.cs.greenworks.Model.TimelineEntry;
import com.dartmouth.cs.greenworks.R;
import com.github.vipulasri.timelineview.TimelineView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chris61015 on 3/4/17.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> implements View.OnClickListener{

        private List<TimelineEntry> mFeedList;
        private Context mContext;
        private LayoutInflater mLayoutInflater;

        private OnRecyclerViewItemClickListener mOnItemClickListener = null;
        //define interface
        public static interface OnRecyclerViewItemClickListener {
            void onItemClick(View view, int position);
        }

        public TimeLineAdapter(List<TimelineEntry> feedList) {
            mFeedList = feedList;
        }

        @Override
        public int getItemViewType(int position) {
            return TimelineView.getTimeLineViewType(position,getItemCount());
        }

        @Override
        public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            mLayoutInflater = LayoutInflater.from(mContext);
            View view;
            view = mLayoutInflater.inflate(R.layout.item_timeline, parent, false);
            view.setOnClickListener(this);

            return new TimeLineViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(TimeLineViewHolder holder, int position) {

            TimelineEntry entry = mFeedList.get(position);

            Bitmap decodedByte;
            if (entry.photo == null){
                holder.mImgView.setImageResource(R.drawable.dartmouthpine);
            } else {
                byte[] decodedString = Base64.decode(entry.photo.replace(' ','+'), Base64.DEFAULT);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.mImgView.setImageBitmap(decodedByte);
            }

            SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = formatTime.format(new Date(entry.dateTime));

            holder.mName.setText(entry.name);
            holder.mDate.setText(time);
            holder.mComment.setText(entry.comment);

            //将数据保存在itemView的Tag中，以便点击时进行获取
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return (mFeedList!=null? mFeedList.size():0);
        }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}




