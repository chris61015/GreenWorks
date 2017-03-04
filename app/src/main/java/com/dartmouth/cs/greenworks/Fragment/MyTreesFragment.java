package com.dartmouth.cs.greenworks.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dartmouth.cs.greenworks.R;
import com.dartmouth.cs.greenworks.TreeEntry;

import java.util.ArrayList;

public class MyTreesFragment extends ListFragment {
    public Context mContext; // context pointed to parent activity
    public ActivityEntriesAdapter mAdapter; // customized adapter for displaying

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }
    // retrieve records from the database and display them in the list view
    public void updateTreeEntries() {
        ArrayList<TreeEntry> entryList = new ArrayList<TreeEntry>(); //TODO:Get MyTrees
        mAdapter.clear();
        mAdapter.addAll(entryList);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onResume() {
        super.onResume();
        // Requery in case the data base has changed.
//        updateTreeEntries();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Click event
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(); // The intent to launch the activity after
        // click.
        Bundle extras = new Bundle(); // The extra information needed pass
        // through to next activity.

        // get the ExerciseEntry corresponding to user's selection
        TreeEntry entry = mAdapter.getItem(position);
//        // Task type is display history, versus create new as in
//        // StartTabFragment.java
//        extras.putInt(Globals.KEY_TASK_TYPE, Globals.TASK_TYPE_HISTORY);
//
//        // Write row id into extras.
//        extras.putLong(Globals.KEY_ROWID, entry.getId());
//
//        // Read the input type: Manual, GPS, or automatic
//        int inputType = entry.getInputType();
//        // Write the input type

//        // Based on different input type, launching different activities
//        switch (inputType) {
//
//            case Globals.INPUT_TYPE_GPS:
//            case Globals.INPUT_TYPE_AUTOMATIC:
//                // GPS and Automatic require MapDisplayAcvitity
//                intent.setClass(mContext, MapDisplayActivity.class);
//                break;
//
//            case Globals.INPUT_TYPE_MANUAL: // Manual mode
//
//                // Passing information for display in the DisaplayEntryActivity.
//                extras.putString(Globals.KEY_ACTIVITY_TYPE,
//                        Utils.parseActivityType(entry.getActivityType(), mContext));
//                extras.putString(Globals.KEY_DATE_TIME,
//                        Utils.parseTime(entry.getDateTimeInMillis(), mContext));
//                extras.putString(Globals.KEY_DURATION,
//                        Utils.parseDuration(entry.getDuration(), mContext));
//                extras.putString(Globals.KEY_DISTANCE,
//                        Utils.parseDistance(entry.getDistance(), mContext));
//
//                // Manual mode requires DisplayEntryActivity
//                intent.setClass(mContext, DisplayEntryActivity.class);
//                break;
//            default:
//                return;
//        }

        // start the activity
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_trees, container, false);
    }
    // Subclass of ArrayAdapter to display interpreted database row values in
    // customized list view.
    private class ActivityEntriesAdapter extends ArrayAdapter<TreeEntry> {

        public ActivityEntriesAdapter(Context context) {
            // set layout to show two lines for each item
            super(context, android.R.layout.two_line_list_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View listItemView = convertView;
            if (null == convertView) {
                // we need to check if the convertView is null. If it's null,
                // then inflate it.
                listItemView = inflater.inflate(
                        android.R.layout.two_line_list_item, parent, false);
            }

            // Setting up view's text1 is main title, text2 is sub-title.
            TextView titleView = (TextView) listItemView
                    .findViewById(android.R.id.text1);
            TextView summaryView = (TextView) listItemView
                    .findViewById(android.R.id.text2);

            // get the corresponding ExerciseEntry
            TreeEntry entry = getItem(position);

            //parse data to readable format
//            String activityTypeString = Utils.parseActivityType(
//                    entry.getActivityType(), mContext);
//
//            String dateString = Utils.parseTime(entry.getDateTimeInMillis(),
//                    mContext);
//            String distanceString = Utils.parseDistance(entry.getDistance(),
//                    mContext);
//            String durationString = Utils.parseDuration(entry.getDuration(),
//                    mContext);

            // Set text on the view.
//            titleView.setText(activityTypeString + ", " + dateString);
//            summaryView.setText(distanceString + ", " + durationString);

            return listItemView;
        }
    }
}
