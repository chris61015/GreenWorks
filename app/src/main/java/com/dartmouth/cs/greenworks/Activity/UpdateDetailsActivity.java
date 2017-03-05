package com.dartmouth.cs.greenworks.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dartmouth.cs.greenworks.Model.TimelineEntry;
import com.dartmouth.cs.greenworks.R;

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

        initView();
    }

    void initView(){
        ((EditText) findViewById(R.id.etTimelineName)).setText(mEntry.name);

        ((EditText) findViewById(R.id.etTimelineComment)).setText(mEntry.comment);

        ImageView imgView = (ImageView) findViewById(R.id.detailImageProfile);

        Bitmap decodedByte;
        if (mEntry.photo == null){
            imgView.setImageResource(R.drawable.dartmouthpine);
        } else {
            byte[] decodedString = Base64.decode(mEntry.photo.replace(' ','+'), Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgView.setImageBitmap(decodedByte);
        }
    }

    public void onGetFullTimelineClicked(View v){
        onBackPressed();
    }
}
