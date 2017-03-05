package com.dartmouth.cs.greenworks.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dartmouth.cs.greenworks.Model.TimeLineModel;
import com.dartmouth.cs.greenworks.R;
import com.dartmouth.cs.greenworks.Timeline.TimeLineAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShowTimelineActivity extends AppCompatActivity implements TimeLineAdapter.OnRecyclerViewItemClickListener {
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();

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
        mDataList.add(new TimeLineModel("Item successfully delivered", ""));
        mDataList.add(new TimeLineModel("Courier is out to delivery your order", "2017-02-12 08:00"));
        mDataList.add(new TimeLineModel("Item has reached courier facility at New Delhi", "2017-02-11 21:00"));
        mDataList.add(new TimeLineModel("Item has been given to the courier", "2017-02-11 18:00"));
        mDataList.add(new TimeLineModel("Item is packed and will dispatch soon", "2017-02-11 09:30"));
        mDataList.add(new TimeLineModel("Order is being readied for dispatch", "2017-02-11 08:00"));
        mDataList.add(new TimeLineModel("Order processing initiated", "2017-02-10 15:00"));
        mDataList.add(new TimeLineModel("Order confirmed by seller", "2017-02-10 14:30"));
        mDataList.add(new TimeLineModel("Order placed successfully", "2017-02-10 14:00"));
    }


    @Override
    public void onItemClick(View view, int position) {
        mDataList.get(position);
        Intent intent = new Intent(this, UpdateDetailsActivity.class);
        startActivity(intent);
    }
}
