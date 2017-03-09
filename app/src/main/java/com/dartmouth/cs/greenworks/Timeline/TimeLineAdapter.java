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
import java.util.ArrayList;
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
            void onItemClick(View view, TimelineEntry entry);
        }

        public TimeLineAdapter() {
            mFeedList = new ArrayList<TimelineEntry>();
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

            //decode photo and put it onto timeline
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

            //store the data into the tag of itemview, so we could retrieve it on click
            holder.itemView.setTag(entry);
        }

        @Override
        public int getItemCount() {
            return (mFeedList!=null? mFeedList.size():0);
        }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //listen to view.onclick and trigger another self-defined OnItemClickListener listener,
            // tricky part on implementing timeline!
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemClick(v, (TimelineEntry)v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void clear(){
        mFeedList.clear();
    }

    public void addAll(List<TimelineEntry> entries){
        mFeedList.addAll(entries);
    }
}




