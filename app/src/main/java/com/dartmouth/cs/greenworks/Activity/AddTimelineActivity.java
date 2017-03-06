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
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dartmouth.cs.greenworks.Fragment.MyDialogFragment;
import com.dartmouth.cs.greenworks.Fragment.MyTreesFragment;
import com.dartmouth.cs.greenworks.Model.TimelineEntry;
import com.dartmouth.cs.greenworks.Model.TreeEntry;
import com.dartmouth.cs.greenworks.R;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.Date;

import static com.dartmouth.cs.greenworks.Activity.BackendTest.UPDATE_TREE;

public class AddTimelineActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_SELECT_FROM_GALLERY = 1;

    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private static final String URI_INSTANCE_STATE_KEY_TEMP = "saved_uri_temp";
    private static final String CAMERA_CLICKED_KEY = "clicked";
    public static final String TREEID = "TreeId";

    private Uri mImageCaptureUri, mTempUri;
    private Boolean stateChanged = false, cameraClicked = false,clickedFromCam=false;

    private ImageView m_ImgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timeline);

        m_ImgView = (ImageView)findViewById(R.id.AddTimelineImageProfile);
        ((EditText) findViewById(R.id.etAddTimelineName)).setInputType(InputType.TYPE_CLASS_TEXT);
        ((EditText) findViewById(R.id.etAddTimelineComment)).setInputType(InputType.TYPE_CLASS_TEXT);

        // To avoid pictures dissapearing from rotate the cell phone
        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState
                    .getParcelable(URI_INSTANCE_STATE_KEY);
            mTempUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY_TEMP);
            cameraClicked = savedInstanceState.getBoolean(CAMERA_CLICKED_KEY);
            stateChanged=true;
            // If configuration changed after cropping photo,
            // retain the photo
            m_ImgView.setImageURI(mTempUri);
            if(m_ImgView.getDrawable() == null) {
                Log.d("Plant a tree", "no file to load. call load photo");
                loadProfileImage();
            }
        }
        else {
            loadProfileImage();
        }



        loadProfile();

    }
    // When you click save button, a new tree info was saved and add a new item to list
    public void onAddTimelineSaveClicked(View v){
        //TODO Get Location
//        Toast.makeText(getApplicationContext(),
//                getString(R.string.ui_profile_toast_save_text),
//                Toast.LENGTH_SHORT).show();
//        saveProfileImage();

    // Get value from Bundle, and put value into buffer, then start Timeline activity
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        TreeEntry entry = bundle.getParcelable(MyTreesFragment.ENTRY);

        long dateTime = new Date().getTime();
        String name = ((EditText) findViewById(R.id.etAddTimelineName)).getText().toString();
        String regId = entry.regId; // unique Id to identify use. Hack for login.
        String photo = new BackendTest().photoToString(this,getString(R.string.ui_add_timeline_photo_file_name));
        String comment =((EditText) findViewById(R.id.etAddTimelineComment)).getText().toString();;

        TimelineEntry timelineEntry = new TimelineEntry(0, entry.treeId, dateTime,name,MainActivity.myRegId,photo,comment);
        new BackendTest.DatastoreTask().execute(UPDATE_TREE,timelineEntry);

        Intent showTimelineIntent = new Intent(this, ShowTimelineActivity.class);
        showTimelineIntent.putExtra(TREEID, entry.treeId);
        startActivity(showTimelineIntent);
    }

    // Cancel Button clicked and then back to original page
    public void onAddTimelineCancelClicked(View v){
        finish();
    }

    // Change the tree's profile photo
    public void changeImage(){
        Intent intent;
        // Take photo from camera，
        // Construct an intent with action
        // MediaStore.ACTION_IMAGE_CAPTURE
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Construct temporary image path and name to save the taken
        // photo
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        mImageCaptureUri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                mImageCaptureUri);
        intent.putExtra("return-data", true);
        try {
            // Start a camera capturing activity
            // REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
            // defined to identify the activity in onActivityResult()
            // when it returns
            startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    // When you click the photo button, show the fragment
    public void onChangePhotoClicked(View v){
        DialogFragment fragment = MyDialogFragment.newInstance(MyDialogFragment.DIALOG_ID_ADD_TIMELINE);
        fragment.show(getFragmentManager(), "Photo Picker");;
    }

    // Choose photo from Gallery or take a new photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Send image taken from camera for cropping
                beginCrop(mImageCaptureUri);
                clickedFromCam=true;
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
                if(clickedFromCam) {
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists())
                        f.delete();
                    clickedFromCam=false;
                }

                break;
        }
    }
    /** Method to start Crop activity using the library
     *	Earlier the code used to start a new intent to crop the image,
     *	but here the library is handling the creation of an Intent, so you don't
     * have to.
     *  **/

    // Begin Crop a photo and get its Uri
    private void beginCrop(Uri source) {
//        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        String filepath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + getString(R.string.ui_add_timeline_photo_file_name);
        mTempUri = Uri.fromFile(new File(filepath));
        Crop.of(source, mTempUri).asSquare().start(this);

    }

    // Crop the photo to a square one
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mTempUri = Crop.getOutput(result);
            m_ImgView.setImageResource(0);
            m_ImgView.setImageURI(mTempUri);
            cameraClicked=true;
//            saveProfileImage();

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // deal with the rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the image capture uri before the activity goes into background
        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
        outState.putParcelable(URI_INSTANCE_STATE_KEY_TEMP,mTempUri);
        outState.putBoolean(CAMERA_CLICKED_KEY,cameraClicked);
        saveProfile();
//        saveProfileImage();
    }

    // Get Photo from Gallery or get a new photo
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


//    private void saveProfileImage() {
//
//        // Commit all the changes into preference file
//        // Save profile image into internal storage.
//        m_ImgView.buildDrawingCache();
//        Bitmap bmap = m_ImgView.getDrawingCache();
//
//        String filepath = Environment.getExternalStorageDirectory()
//                .getAbsolutePath() + File.separator + getString(R.string.ui_add_timeline_photo_file_name);
//        Log.d("DEBUG", "abs file path: " + filepath);
//
//        try {
//            File f = new File(filepath);
//            FileOutputStream fos = new FileOutputStream(f,false);
//            bmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }

    // load profile image from internal storage
    private void loadProfileImage() {
//        try {
//            FileInputStream fis;

            if(stateChanged && cameraClicked){
                if(!Uri.EMPTY.equals(mTempUri)) {
                    m_ImgView.setImageURI(mTempUri);
                    stateChanged = false;
                }
            } else {
                String filepath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + getString(R.string.ui_add_timeline_photo_file_name);

                mTempUri = Uri.fromFile(new File(filepath));
                m_ImgView.setImageURI(mTempUri);
                if(m_ImgView.getDrawable() == null) {
                    m_ImgView.setImageResource(R.drawable.dartmouthpine);
                }
//                fis = new FileInputStream(filepath);
//                Bitmap bmap = BitmapFactory.decodeStream(fis);
//                m_ImgView.setImageBitmap(bmap);

//                fis.close();
            }

//        } catch (IOException e) {
//            // Default profile photo if no photo saved before.
//            m_ImgView.setImageResource(R.drawable.dartmouthpine);
//        }
    }

    // Use SharedPreference to save tree's info
    public void saveProfile(){
        String key, val;

        key = getString(R.string.timeline_preference_name);
        SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Write screen contents into corresponding editor fields.
        key = getString(R.string.preference_key_tree_name);
        val = ((EditText) findViewById(R.id.etAddTimelineName)).getText().toString();
        editor.putString(key, val);

        key = getString(R.string.preference_key_tree_comment);
        val = ((EditText) findViewById(R.id.etAddTimelineComment)).getText()
                .toString();
        editor.putString(key, val);

        //TODO: Check this out
        editor.apply();
    }

    // When you start the activity, load the info saved before
    public void loadProfile(){
        String key, str_val;

        key = getString(R.string.timeline_preference_name);
        SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);

        key = getString(R.string.preference_key_tree_name);
        str_val = prefs.getString(key, "");
        ((EditText) findViewById(R.id.etAddTimelineName)).setText(str_val);

        key = getString(R.string.preference_key_tree_comment);
        str_val = prefs.getString(key, "");
        ((EditText) findViewById(R.id.etAddTimelineComment)).setText(str_val);
    }
}
