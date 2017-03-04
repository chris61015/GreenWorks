package com.dartmouth.cs.greenworks.Activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.dartmouth.cs.greenworks.R;

/**
 * Created by chris61015 on 3/4/17.
 */

public class TreeDetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_tree_details);
    }
}
