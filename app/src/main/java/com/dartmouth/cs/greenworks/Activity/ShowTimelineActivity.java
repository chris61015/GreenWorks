package com.dartmouth.cs.greenworks.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dartmouth.cs.greenworks.Model.TimelineEntry;
import com.dartmouth.cs.greenworks.R;
import com.dartmouth.cs.greenworks.Timeline.TimeLineAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.dartmouth.cs.greenworks.Activity.BackendTest.GET_TIMELINE;

public class ShowTimelineActivity extends AppCompatActivity implements TimeLineAdapter.OnRecyclerViewItemClickListener {
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimelineEntry> mDataList = new ArrayList<>();
    public static String TIMELINE = "timeline";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_timeline);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);


        initView();
    }

    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    private void initView() {
        setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mDataList);
        mTimeLineAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }

    private void setDataListItems(){
        new BackendTest.DatastoreTask(mDataList).execute(GET_TIMELINE,"");
    }


    @Override
    public void onItemClick(View view, int position) {

        Intent intent = new Intent(this, UpdateDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(TIMELINE,mDataList.get(position));

        intent.putExtras(bundle);

        startActivity(intent);
    }
}
