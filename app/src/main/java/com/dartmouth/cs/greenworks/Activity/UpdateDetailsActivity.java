package com.dartmouth.cs.greenworks.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dartmouth.cs.greenworks.Model.TimelineEntry;
import com.dartmouth.cs.greenworks.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.dartmouth.cs.greenworks.Activity.ShowTimelineActivity.TIMELINE;

public class UpdateDetailsActivity extends AppCompatActivity {

    private TimelineEntry mEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mEntry = bundle.getParcelable(TIMELINE);

        //Prepare to show information
        initView();
    }


    void initView(){

        EditText text1 = ((EditText) findViewById(R.id.etUpdateDetailName));
        EditText text3 = ((EditText) findViewById(R.id.etUpdateDetailComment));
        EditText text4 = ((EditText) findViewById(R.id.etUpdateDetailDateTime));

        //Show the information
        text1.setText(mEntry.name);
        text3.setText(mEntry.comment);

        text1.setKeyListener(null);
        text3.setKeyListener(null);

        SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatTime.format(new Date(mEntry.dateTime));

        text4.setText(time);
        text4.setKeyListener(null);

        ImageView imgView = (ImageView) findViewById(R.id.UpdateDetailImageProfile);

        //decode the Base64 string and turn it back into photo
        Bitmap decodedByte;
        if (mEntry.photo == null){
            imgView.setImageResource(R.drawable.dartmouthpine);
        } else {
            byte[] decodedString = Base64.decode(mEntry.photo.replace(' ','+'), Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgView.setImageBitmap(decodedByte);
        }
    }

    // Click GetFullTimeline and back to ShowTimeline Activity
    public void onGetFullTimelineClicked(View v){
        onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_update_detail);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Update");
    }
}
