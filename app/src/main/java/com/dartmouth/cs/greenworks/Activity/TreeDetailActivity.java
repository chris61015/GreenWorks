package com.dartmouth.cs.greenworks.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dartmouth.cs.greenworks.Model.TreeEntry;
import com.dartmouth.cs.greenworks.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.dartmouth.cs.greenworks.Activity.AddTimelineActivity.TREEID;
import static com.dartmouth.cs.greenworks.Fragment.MyTreesFragment.ENTRY;

/**
 * Created by chris61015 on 3/4/17.
 */

public class TreeDetailActivity extends AppCompatActivity {

    private TreeEntry mEntry;
    final public static String TREEIFNO = "TreeInfo";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_details);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mEntry = bundle.getParcelable(ENTRY);

        initView();

    }

    void initView(){
        EditText text1 = ((EditText) findViewById(R.id.etDetailName));

        EditText text2 = ((EditText) findViewById(R.id.etDetailCity));

        EditText text3 = ((EditText) findViewById(R.id.etDetailComment));

        EditText text4 = ((EditText) findViewById(R.id.etTreeDetailDateTime));

        text1.setText(mEntry.name);
        text2.setText(mEntry.city);
        text3.setText(mEntry.comment);

        text1.setKeyListener(null);
        text2.setKeyListener(null);
        text3.setKeyListener(null);

        SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatTime.format(new Date(mEntry.dateTime));

        text4.setText(time);
        text4.setKeyListener(null);

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

    public void onShowTimelineClicked(View v){
        Intent intent = new Intent(this, ShowTimelineActivity.class);
        intent.putExtra(TREEID, mEntry.treeId);
        startActivity(intent);
    }

    public void onUpdateClicked(View v){
        Intent intent = new Intent(this, AddTimelineActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable(ENTRY,mEntry);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_tree);
//        setSupportActionBar(mActionBarToolbar);
//        getSupportActionBar().setTitle("Tree");
    }
}
