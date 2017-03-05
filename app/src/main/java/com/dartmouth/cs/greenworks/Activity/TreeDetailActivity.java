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
        ((EditText) findViewById(R.id.etDetailName)).setText(mEntry.name);

        ((EditText) findViewById(R.id.etDetailCity)).setText(mEntry.city);

        ((EditText) findViewById(R.id.etDetailComment)).setText(mEntry.comment);

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
}
