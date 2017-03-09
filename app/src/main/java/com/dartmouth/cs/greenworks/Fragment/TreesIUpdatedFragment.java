package com.dartmouth.cs.greenworks.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dartmouth.cs.greenworks.Activity.MainActivity;
import com.dartmouth.cs.greenworks.Activity.TreeDetailActivity;
import com.dartmouth.cs.greenworks.Model.TreeEntry;
import com.dartmouth.cs.greenworks.R;
import com.dartmouth.cs.greenworks.Utils.ActivityEntriesAdapter;
import com.dartmouth.cs.greenworks.Utils.BackendTest;

import java.util.ArrayList;
import java.util.List;


public class TreesIUpdatedFragment extends ListFragment {

    private ActivityEntriesAdapter mAdapter; // customized adapter for displaying
    private List<TreeEntry> mTreeEntryList;
    public static String ENTRY = "entry";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ActivityEntriesAdapter(getActivity());
        mTreeEntryList = new ArrayList<TreeEntry>();

        setListAdapter(mAdapter);
        updateTreeEntries();
    }

    // retrieve records from the database and display them in the list view
    public void updateTreeEntries() {
        new BackendTest.DatastoreTask(mAdapter, mTreeEntryList).
                execute(BackendTest.GET_MY_UPDATED_TREES, MainActivity.myRegId);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Trees I Updated");
        // Requery in case the data base has changed.
        updateTreeEntries();
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
        Intent intent = new Intent(getActivity(), TreeDetailActivity.class); // The intent to launch the activity after click.
        Bundle extras = new Bundle(); // The extra information needed pass
        // through to next activity.

        // get the ExerciseEntry corresponding to user's selection
        TreeEntry entry = mAdapter.getItem(position);
        // Task type is display history, versus create new as in
        extras.putParcelable(ENTRY, entry);

        // start the activity
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trees_iupdated, container, false);
    }
}
