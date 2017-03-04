package com.dartmouth.cs.greenworks.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.dartmouth.cs.greenworks.Activity.PlantATreeActivity;
import com.dartmouth.cs.greenworks.R;

import java.util.Calendar;

/**
 * Created by chris61015 on 3/3/17.
 */

public class MyDialogFragment extends DialogFragment {
    // Different dialog IDs
    public static final int DIALOG_ID_ERROR = -1;
    public static final int DIALOG_ID_PHOTO_PICKER = 1;
    public static final int DIALOG_ID_MANUAL_INPUT_DATE = 2;
    public static final int DIALOG_ID_MANUAL_INPUT_TIME = 3;
    public static final int DIALOG_ID_MANUAL_INPUT_DURATION = 4;
    public static final int DIALOG_ID_MANUAL_INPUT_DISTANCE = 5;
    public static final int DIALOG_ID_MANUAL_INPUT_CALORIES = 6;
    public static final int DIALOG_ID_MANUAL_INPUT_HEARTRATE = 7;
    public static final int DIALOG_ID_MANUAL_INPUT_COMMENT = 8;

    // For photo picker selection:
    public static final int ID_PHOTO_PICKER_FROM_CAMERA = 0;
    public static final int ID_PHOTO_PICKER_FROM_GALLERY = 1;


    private static final String DIALOG_ID_KEY = "dialog_id";

    public static DialogFragment newInstance(int dialog_id) {
        DialogFragment frag = new DialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID_KEY, dialog_id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialog_id = getArguments().getInt(DIALOG_ID_KEY);

        final Activity parent = getActivity();

        // For initializing date/time related dialogs
        final Calendar now;
        int hour, minute, year, month, day;

        // For text input field
        final EditText textEntryView;


        // Settup dialog appearance and onClick Listeners
        switch (dialog_id) {
            case DIALOG_ID_PHOTO_PICKER:
                // Build picture picker dialog for choosing from camera or gallery
                AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                builder.setTitle(R.string.ui_profile_photo_picker_title);
                // Set up click listener, firing intents open camera or gallery based on
                // choice.
                builder.setItems(R.array.ui_profile_photo_picker_items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // Item can be: ID_PHOTO_PICKER_FROM_CAMERA
                                // or ID_PHOTO_PICKER_FROM_GALLERY
                                ((PlantATreeActivity) parent)
                                        .onPhotoPickerItemSelected(item);
                            }
                        });
                return builder.create();
            default:
                return null;
        }
    }
}
