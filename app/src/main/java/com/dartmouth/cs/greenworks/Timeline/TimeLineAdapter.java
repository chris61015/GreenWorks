package com.dartmouth.cs.greenworks.Timeline;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dartmouth.cs.greenworks.Model.TimeLineModel;
import com.dartmouth.cs.greenworks.R;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

/**
 * Created by chris61015 on 3/4/17.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> implements View.OnClickListener{

        private List<TimeLineModel> mFeedList;
        private Context mContext;
        private LayoutInflater mLayoutInflater;

        private OnRecyclerViewItemClickListener mOnItemClickListener = null;
        //define interface
        public static interface OnRecyclerViewItemClickListener {
            void onItemClick(View view, int position);
        }

        public TimeLineAdapter(List<TimeLineModel> feedList) {
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

            TimeLineModel timeLineModel = mFeedList.get(position);
            holder.mMessage.setText(timeLineModel.getMessage());
            //将数据保存在itemView的Tag中，以便点击时进行获取
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return (mFeedList!=null? mFeedList.size():0);
        }

    @Override
    public void onClick(View v) {
        Log.d("DBUGE","QQQQQQ");
        if (mOnItemClickListener != null) {
            if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}




