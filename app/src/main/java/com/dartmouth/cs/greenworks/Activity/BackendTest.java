package com.dartmouth.cs.greenworks.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.dartmouth.cs.greenworks.Model.TimelineEntry;
import com.dartmouth.cs.greenworks.Model.TreeEntry;
import com.dartmouth.cs.greenworks.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Xiaolei on 2/27/17.
 */

public class BackendTest {

    public static final int ADD_TREE = 1;
    public static final int UPDATE_TREE = 2;
    public static final int GET_TREES_AROUND_ME = 3;
    public static final int GET_MY_TREES = 4;
    public static final int GET_MY_UPDATED_TREES = 5;
    public static final int GET_TIMELINE = 6;

    public static final String TAG = "BackendTest";


    public String myRegId = "";

    // Server stuff
//    public static String SERVER_ADDR = "https://lateral-avatar-160118.appspot.com";
    public static String SERVER_ADDR = "http://127.0.0.1:8080";


    public String registerTest(Context context) {
        try {
            myRegId = new GcmRegistrationAsyncTask(context).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "RegID: " + myRegId);
        return myRegId;
    }

    public void updateTree(Context context, String filename, long treeId) {
        String encodedImage = photoToString (context, filename);
        TimelineEntry timelineEntry = new TimelineEntry(0, treeId,
                System.currentTimeMillis(), "Xiaolei_up_" + treeId,
                myRegId, encodedImage, "update on tree " + treeId) ;
        new DatastoreTask().execute(UPDATE_TREE, timelineEntry);

    }

    public void addTreeTest(Context context, String filename) {

        String encodedImage = photoToString(context, filename);

        TreeEntry tree = new TreeEntry(0, System.currentTimeMillis(),
                new LatLng(12.4, 11.2), "Xiaolei", "West Leb",
                myRegId, encodedImage, "Hello World!");
        new DatastoreTask().execute(ADD_TREE, tree);
    }

    public String photoToString(Context context, String filename) {

        String filepath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + filename;
        Log.d(TAG, "abs file path: " + filepath);

        try {
            File f = new File(filepath);
            if (!f.exists()) {
                Log.d(TAG, "File doesn't exist");
                return null;
            }

            Bitmap bm = BitmapFactory.decodeFile(filepath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();

            return Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            Log.d(TAG, "Load file error: " + filename);
            return null;
        }
    }

    public class DatastoreTask extends AsyncTask<Object, Void, Void> {
        private int operMode;
        @Override
        protected Void doInBackground(Object... params) {
            operMode = (int)params[0];
            switch(operMode) {
                case ADD_TREE:
                    TreeEntry tree = (TreeEntry)params[1];
                    Map<String, String>data = new HashMap<>();
                    data.put("Date Time", Long.toString(tree.dateTime));

                    data.put("Latitude", Double.toString(tree.location.latitude));
                    data.put("Longitude", Double.toString(tree.location.longitude));
                    data.put("Name", tree.name);
                    data.put("City", tree.city);
                    data.put("Registration ID", tree.regId);
                    data.put("Photo", tree.photo);
                    data.put("Comment", tree.comment);
                    try {
                        ServerUtilities.post(SERVER_ADDR + "/addtree.do", data);
                    } catch (IOException e) {
                        Log.e(TAG, "Add tree Sync failed: " + e.getCause());
                        Log.e(TAG, "data posting error " + e);
                    }
                    break;
                case UPDATE_TREE:
                    TimelineEntry timelineEntry = (TimelineEntry) params[1];
                    Map<String, String>data2 = new HashMap();
                    data2.put("Tree ID", Long.toString(timelineEntry.treeId));
                    data2.put("Name", timelineEntry.name);
                    data2.put("Date Time", Long.toString(timelineEntry.dateTime));
                    data2.put("Photo", timelineEntry.photo);
                    data2.put("Registration ID", timelineEntry.regId);
                    data2.put("Comment", timelineEntry.comment);
                    try {
                        ServerUtilities.post(SERVER_ADDR + "/updatetree.do", data2);
                    } catch (IOException e) {
                        Log.e(TAG, "Update tree Sync failed: " + e.getCause());
                        Log.e(TAG, "data posting error " + e);
                    }
                    break;
            }
            return null;

        }
    }

    public class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
        private Registration regService = null;
        private GoogleCloudMessaging gcm;
        private Context context;

        private static final String SENDER_ID = "643884535238";

        public GcmRegistrationAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (regService == null) {
                Registration.Builder builder =
                        new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // Need setRootUrl and setGoogleClientRequestInitializer
                        // only for local testing,
                        // otherwise they can be skipped
                        .setRootUrl(SERVER_ADDR+"/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                    throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end of optional local run code

                //XD: newCompatibleTransport - returns a new thread-safe HTTP transport
                // instance that is compatible with Android SDKs prior to * Gingerbread.
                //XD: use this builder instead if you deploy your backend to the cloud
                // (e.g. https://abstract-arc-123122.appspot.com)
//                Registration.Builder builder =
//                        new Registration.Builder(AndroidHttp.newCompatibleTransport(),
//                                new AndroidJsonFactory(), null)
//                                .setRootUrl(SERVER_ADDR +"/_ah/api/");
                regService = builder.build();
            }

            String msg = "";
            String regId = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regId = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regId;

                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.
                regService.register(regId).execute();

            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error: " + ex.getMessage();
            }
            return regId; //msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
        }
    }

}
