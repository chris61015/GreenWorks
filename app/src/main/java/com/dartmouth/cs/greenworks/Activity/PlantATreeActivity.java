package com.dartmouth.cs.greenworks.Activity;

import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dartmouth.cs.greenworks.Fragment.MapFragment;
import com.dartmouth.cs.greenworks.Fragment.MyDialogFragment;
import com.dartmouth.cs.greenworks.Model.TreeEntry;
import com.dartmouth.cs.greenworks.R;
import com.dartmouth.cs.greenworks.Utils.BackendTest;
import com.google.android.gms.maps.model.LatLng;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.Date;

/**
 * Created by chris61015 on 2/25/17.
 */

public class PlantATreeActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_SELECT_FROM_GALLERY = 1;

    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private static final String URI_INSTANCE_STATE_KEY_TEMP = "saved_uri_temp";
    private static final String CAMERA_CLICKED_KEY = "clicked";

    private Uri mImageCaptureUri, mTempUri;
    private Boolean stateChanged = false, cameraClicked = false, clickedFromCam = false;

    private ImageView m_ImgView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //set layout for this activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_a_tree);

        m_ImgView = (ImageView) findViewById(R.id.imageProfile);

        //initializing components on screen
        ((EditText) findViewById(R.id.etName)).setInputType(InputType.TYPE_CLASS_TEXT);
        ((EditText) findViewById(R.id.etCity)).setInputType(InputType.TYPE_CLASS_TEXT);
        ((EditText) findViewById(R.id.etComment)).setInputType(InputType.TYPE_CLASS_TEXT);
        loadProfile();

        // To avoid pictures dissapearing from rotate the cell phone
        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState
                    .getParcelable(URI_INSTANCE_STATE_KEY);
            mTempUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY_TEMP);
            cameraClicked = savedInstanceState.getBoolean(CAMERA_CLICKED_KEY);
            stateChanged = true;
            // If configuration changed after cropping photo,
            // retain the photo
            m_ImgView.setImageURI(mTempUri);
            if (m_ImgView.getDrawable() == null) {
                Log.d("Plant a tree", "no file to load. call load photo");
                loadProfileImage();
            }
        } else {
            loadProfileImage();
        }
    }

    public void onSaveClicked(View v) throws Exception {
        BackendTest test = new BackendTest();

        //Make a Treeentry object
        long treeId = 2;
        long dateTime = new Date().getTime();
        LatLng location = new LatLng(MapFragment.getLocation().getLatitude(), MapFragment.getLocation().getLongitude());
        String name = ((EditText) findViewById(R.id.etName)).getText().toString();
        String city = ((EditText) findViewById(R.id.etCity)).getText().toString();
        //String regId = ""; // unique Id to identify use. Hack for login.
        String photo = test.photoToString(this, getString(R.string.ui_plant_tree_photo_file_name));
        String comment = ((EditText) findViewById(R.id.etComment)).getText().toString();

        TreeEntry entry = new TreeEntry(treeId, dateTime, location, name, city,
                MainActivity.myRegId, photo, comment);
        test.addTreeTest(entry);
        finish();
    }

    public void onCancelClicked(View v) {
        finish();
    }

    public void changeImage() {
        Intent intent;
        // Take photo from camera，
        // Construct an intent with action
        // MediaStore.ACTION_IMAGE_CAPTURE
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Construct temporary image path and name to save the takenphoto
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        mImageCaptureUri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                mImageCaptureUri);
        intent.putExtra("return-data", true);
        try {
            // Start a camera capturing activity
            startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onChangePhotoClicked(View v) {
        DialogFragment fragment = MyDialogFragment.newInstance(MyDialogFragment.DIALOG_ID_PHOTO_PICKER);
        fragment.show(getFragmentManager(), "Photo Picker");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Send image taken from camera for cropping
                beginCrop(mImageCaptureUri);
                clickedFromCam = true;
                break;

            case REQUEST_CODE_SELECT_FROM_GALLERY:
                Uri srcUri = data.getData();
                beginCrop(srcUri);
                break;

            case Crop.REQUEST_CROP:
                // Update image view after image crop
                // Set the picture image in UI
                handleCrop(resultCode, data);

                // Delete temporary image taken by camera after crop.
                if (clickedFromCam) {
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists())
                        f.delete();
                    clickedFromCam = false;
                }

                break;
        }
    }

    //Method to start Crop activity using the library
    private void beginCrop(Uri source) {
        String filepath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + getString(R.string.ui_plant_tree_photo_file_name);
        mTempUri = Uri.fromFile(new File(filepath));
        Crop.of(source, mTempUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mTempUri = Crop.getOutput(result);
            m_ImgView.setImageResource(0);
            m_ImgView.setImageURI(mTempUri);
            cameraClicked = true;
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the image capture uri before the activity goes into background
        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
        outState.putParcelable(URI_INSTANCE_STATE_KEY_TEMP, mTempUri);
        outState.putBoolean(CAMERA_CLICKED_KEY, cameraClicked);
        saveProfile();
    }

    public void onPhotoPickerItemSelected(int item) {
        Intent intent;
        switch (item) {
            case 0:
                changeImage();
                break;
            case 1:
                intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_FROM_GALLERY);
                break;

            default:
                return;
        }
    }

    private void loadProfileImage() {
        // Load profile photo from externel storage
        if (stateChanged && cameraClicked) {
            if (!Uri.EMPTY.equals(mTempUri)) {
                m_ImgView.setImageURI(mTempUri);
                stateChanged = false;
            }
        } else {
            String filepath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + getString(R.string.ui_plant_tree_photo_file_name);
            mTempUri = Uri.fromFile(new File(filepath));
            m_ImgView.setImageURI(mTempUri);
            if (m_ImgView.getDrawable() == null) {
                m_ImgView.setImageResource(R.drawable.dartmouthpine);
            }
        }
    }

    public void saveProfile() {
        String key, val;

        key = getString(R.string.preference_name);
        SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Write screen contents into corresponding editor fields.
        key = getString(R.string.preference_key_tree_name);
        val = ((EditText) findViewById(R.id.etName)).getText().toString();
        editor.putString(key, val);

        key = getString(R.string.preference_key_tree_city);
        val = ((EditText) findViewById(R.id.etCity)).getText()
                .toString();
        editor.putString(key, val);

        key = getString(R.string.preference_key_tree_comment);
        val = ((EditText) findViewById(R.id.etComment)).getText()
                .toString();
        editor.putString(key, val);
        editor.apply();
    }

    public void loadProfile() {
        String key, str_val;

        key = getString(R.string.preference_name);
        SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);

        key = getString(R.string.preference_key_tree_name);
        str_val = prefs.getString(key, "");
        ((EditText) findViewById(R.id.etName)).setText(str_val);

        key = getString(R.string.preference_key_tree_city);
        str_val = prefs.getString(key, "");
        ((EditText) findViewById(R.id.etCity)).setText(str_val);

        key = getString(R.string.preference_key_tree_comment);
        str_val = prefs.getString(key, "");
        ((EditText) findViewById(R.id.etComment)).setText(str_val);
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_plant);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Plant A Tree");
    }
}
