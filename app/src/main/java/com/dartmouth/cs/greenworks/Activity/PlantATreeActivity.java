package com.dartmouth.cs.greenworks.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dartmouth.cs.greenworks.R;

/**
 * Created by chris61015 on 2/25/17.
 */

public class PlantATreeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_a_tree);
        Log.d("DEBUG", "PlantATreeActivity");
    }


}