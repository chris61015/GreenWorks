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
import com.google.android.gms.maps.model.LatLng;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.Date;

/**
 * Created by chris61015 on 2/25/17.
 */

//TODO: Figure out the bug when change orientation, pic become smaller
public class PlantATreeActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_SELECT_FROM_GALLERY = 1;
    public static final int REQUEST_CODE_CROP_PHOTO = 2;

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private static final String URI_INSTANCE_STATE_KEY_TEMP = "saved_uri_temp";
    private static final String CAMERA_CLICKED_KEY = "clicked";

    private Uri mImageCaptureUri, mTempUri;
    private Boolean stateChanged = false, cameraClicked = false,clickedFromCam=false;

    private ImageView m_ImgView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_a_tree);


        m_ImgView = (ImageView)findViewById(R.id.imageProfile);

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
    }

    public void onSaveClicked(View v) throws Exception {
        //TODO Get Location
//        Toast.makeText(getApplicationContext(),
//                getString(R.string.ui_profile_toast_save_text),
//                Toast.LENGTH_SHORT).show();

//        saveProfileImage();

        BackendTest test = new BackendTest();
//        test.registerTest(this);
        long treeId = 2;
        long dateTime = new Date().getTime();
        LatLng location = new LatLng(MapFragment.getLocation().getLatitude(), MapFragment.getLocation().getLongitude());
        String name = ((EditText) findViewById(R.id.etName)).getText().toString();
        String city = ((EditText) findViewById(R.id.etCity)).getText().toString();
        String regId = ""; // unique Id to identify use. Hack for login.
        String photo = test.photoToString(this,getString(R.string.ui_plant_tree_photo_file_name));
        String comment =((EditText) findViewById(R.id.etComment)).getText().toString();;

        TreeEntry entry = new TreeEntry(treeId, dateTime,location,name,city,
                MainActivity.myRegId,photo,comment);
        test.addTreeTest(entry);
        finish();
    }

    public void onCancelClicked(View v){
        finish();
    }

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

    public void onChangePhotoClicked(View v){
        DialogFragment fragment = MyDialogFragment.newInstance(MyDialogFragment.DIALOG_ID_PHOTO_PICKER);
        fragment.show(getFragmentManager(), "Photo Picker");;
    }

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
    private void beginCrop(Uri source) {
//        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
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
            cameraClicked=true;
//            saveProfileImage();

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

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
//                .getAbsolutePath() + File.separator + getString(R.string.ui_plant_tree_photo_file_name);
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

    private void loadProfileImage() {
        // Load profile photo from internal storage
//        try {
//            FileInputStream fis;

            if(stateChanged && cameraClicked){
                if(!Uri.EMPTY.equals(mTempUri)) {
                    m_ImgView.setImageURI(mTempUri);
                    stateChanged = false;
                }
            } else {
                String filepath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + getString(R.string.ui_plant_tree_photo_file_name);
                mTempUri = Uri.fromFile(new File(filepath));
                m_ImgView.setImageURI(mTempUri);
                if(m_ImgView.getDrawable() == null) {
                    m_ImgView.setImageResource(R.drawable.dartmouthpine);
                }
//                fis = new FileInputStream(filepath);
//                Bitmap bmap = BitmapFactory.decodeStream(fis);
//                m_ImgView.setImageBitmap(bmap);
//
//                mTempUri = Uri.fromFile(new File(filepath));
//
//                fis.close();
            }

//        } catch (IOException e) {
//            // Default profile photo if no photo saved before.
//            m_ImgView.setImageResource(R.drawable.dartmouthpine);
//        }
    }

    public void saveProfile(){
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

        //TODO: Check this out
        editor.apply();
    }

    public void loadProfile(){
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
