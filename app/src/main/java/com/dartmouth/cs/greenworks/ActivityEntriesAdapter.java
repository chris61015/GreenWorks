package com.dartmouth.cs.greenworks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dartmouth.cs.greenworks.Model.TreeEntry;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chris61015 on 3/4/17.
 */

// Subclass of ArrayAdapter to display interpreted database row values in
// customized list view.
public class ActivityEntriesAdapter extends ArrayAdapter<TreeEntry> {

    public ActivityEntriesAdapter(Context context) {
        // set layout to show two lines for each item
        super(context, android.R.layout.activity_list_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("DEBUG!!!!!!!!!!!!!!","You know I am debugging");

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View listItemView = convertView;
        if (null == convertView) {
            // we need to check if the convertView is null. If it's null,
            // then inflate it.
            listItemView = inflater.inflate(
                    android.R.layout.activity_list_item, parent, false);
        }


        // Setting up view's text1 is main title, text2 is sub-title.
        ImageView imgView = (ImageView) listItemView
                .findViewById(android.R.id.icon);
        imgView.getLayoutParams().height = 200;
        imgView.getLayoutParams().width = 200;

        TextView txtView = (TextView) listItemView
                .findViewById(android.R.id.text1);

        // get the corresponding ExerciseEntry
        TreeEntry entry = getItem(position);

        Log.d("DEBUG", entry.toString());

        Bitmap decodedByte;
        if (entry.photo == null){
            imgView.setImageResource(R.drawable.dartmouthpine);
        } else {
            Log.d("DEBUG!!", entry.photo);
            byte[] decodedString = Base64.decode(entry.photo.replace(' ','+'), Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgView.setImageBitmap(decodedByte);
        }

        SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatTime.format(new Date(entry.dateTime));

        StringBuilder sb = new StringBuilder();
        sb.append(entry.name).append(time).append(" ").append(entry.city);

        // Set text on the view.
        txtView.setText(sb);

        return listItemView;
    }
}