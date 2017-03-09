package com.dartmouth.cs.greenworks.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.dartmouth.cs.greenworks.Activity.AddTimelineActivity;
import com.dartmouth.cs.greenworks.Activity.PlantATreeActivity;
import com.dartmouth.cs.greenworks.R;

/**
 * Created by chris61015 on 3/3/17.
 */

public class MyDialogFragment extends DialogFragment {
    // Different dialog IDs
    public static final int DIALOG_ID_PHOTO_PICKER = 1;
    public static final int DIALOG_ID_ADD_TIMELINE = 2;

    private static final String DIALOG_ID_KEY = "dialog_id";

    public static MyDialogFragment newInstance(int dialog_id) {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID_KEY, dialog_id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialog_id = getArguments().getInt(DIALOG_ID_KEY);

        final Activity parent = getActivity();

        // Build picture picker dialog for choosing from camera or gallery
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);

        // Settup dialog appearance and onClick Listeners
        switch (dialog_id) {
            case DIALOG_ID_PHOTO_PICKER:
                builder.setTitle(R.string.ui_profile_photo_picker_title);
                // Set up click listener
                builder.setItems(R.array.ui_profile_photo_picker_items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                ((PlantATreeActivity) parent)
                                        .onPhotoPickerItemSelected(item);
                            }
                        });
                return builder.create();
            case DIALOG_ID_ADD_TIMELINE:
                builder.setTitle(R.string.ui_profile_photo_picker_title);
                // Set up click listener
                builder.setItems(R.array.ui_profile_photo_picker_items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                ((AddTimelineActivity) parent)
                                        .onPhotoPickerItemSelected(item);
                            }
                        });
                return builder.create();
            default:
                return null;
        }
    }
}
