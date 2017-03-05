package com.dartmouth.cs.greenworks.Timeline;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dartmouth.cs.greenworks.R;
import com.github.vipulasri.timelineview.TimelineView;

/**
 * Created by chris61015 on 3/4/17.
 */

public class TimeLineViewHolder extends RecyclerView.ViewHolder{
    public TextView mDate, mMessage;
    public TimelineView mTimelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        mDate = (TextView) itemView.findViewById(R.id.text_timeline_date);
        mMessage = (TextView) itemView.findViewById(R.id.text_timeline_title);
        mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        mTimelineView.initLine(viewType);
    }
}
