package com.dartmouth.cs.greenworks.Activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.dartmouth.cs.greenworks.R;

/**
 * Created by chris61015 on 2/25/17.
 */

public class PlantATreeActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_plant_a_tree);
    }
}
