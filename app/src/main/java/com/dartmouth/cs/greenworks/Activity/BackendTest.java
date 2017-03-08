package com.dartmouth.cs.greenworks.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.dartmouth.cs.greenworks.Utils.ActivityEntriesAdapter;
import com.dartmouth.cs.greenworks.Model.TimelineEntry;
import com.dartmouth.cs.greenworks.Model.TreeEntry;
import com.dartmouth.cs.greenworks.Timeline.TimeLineAdapter;
import com.dartmouth.cs.greenworks.Utils.ServerUtilities;
import com.dartmouth.cs.greenworks.backend.registration.Registration;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.dartmouth.cs.greenworks.Fragment.MyTreesFragment.ENTRY;

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
    public static final int GET_TREE_BY_ID = 7;

    public static final int MAX_SIZE = 400;

    public static final String TAG = "BackendTest";

    public static final String REG_FILE = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + "regId.csv";

    public static boolean isFinish = true;


    // Server stuff
//    public static String SERVER_ADDR = "https://lateral-avatar-160118.appspot.com";
      public static String SERVER_ADDR = "http://127.0.0.1:8080";


    public boolean registerTest(Context context) {

        checkPlayServices(context);



        File file = new File(REG_FILE);

        if (!file.exists()) {   // create file and try to register and block.
            Log.d(TAG, "Register for the first time");

            doRegister(context, true);

        }
        else {  // load from file first. Also try register.
            try {
                Log.d(TAG, "Loading ID from file");
                FileInputStream fis = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                String line = reader.readLine();
                if (line == null || line.length() == 0) {
                    Log.d(TAG, "Error loading ID from file: " + line);
                    doRegister(context, true);
                }
                else {
                    Log.d(TAG, "Successfully loading ID from file: " + line);
                    MainActivity.myRegId = line;
                    doRegister(context, false);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "Finally, RegID: " + MainActivity.myRegId);
        if (MainActivity.myRegId.length() == 0) {
            return false;
        }
        else {
            return true;
        }
    }

    public void doRegister(Context context, boolean block) {
        if (block) {
            try {
                new GcmRegistrationAsyncTask(context).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        else {
            new GcmRegistrationAsyncTask(context).execute();
        }
    }

    private boolean checkPlayServices(Context context) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                Log.d(TAG, "Some error.");
            } else {
                Log.d(TAG, "This device is not supported.");

            }
            return false;
        }
        else {
            Log.d(TAG, "Player service good to go");
        }
        return true;
    }

    public void getTreesAroundMeTest(Context context)
    {
        int radius =2;
        double longitude = 43.7069;
        double lattitude = -72.2869;

        new DatastoreTask().execute(GET_TREES_AROUND_ME,radius, longitude, lattitude);
    }

    public void getMyTreesTest(Context context)
    {
        new DatastoreTask().execute(GET_MY_TREES, MainActivity.myRegId);
    }

    public void getTimelineTest(Context context, Long treeID)
    {
        new DatastoreTask().execute(GET_TIMELINE,treeID);
    }

    public void getTreesIUpdatedTest(Context context)
    {
        new DatastoreTask().execute(GET_MY_UPDATED_TREES,MainActivity.myRegId);
    }


    public void updateTree(Context context, String filename, long treeId) {
        String encodedImage = photoToString (context, filename);
        TimelineEntry timelineEntry = new TimelineEntry(0, treeId,
                System.currentTimeMillis(), "Xiaolei_up_" + treeId,
                MainActivity.myRegId, encodedImage, "update on tree " + treeId) ;
        new DatastoreTask().execute(UPDATE_TREE, timelineEntry);
    }

    public void addTreeTest(Context context, String filename) {

        String encodedImage = photoToString(context, filename);

        TreeEntry tree = new TreeEntry(0, System.currentTimeMillis(),
                new LatLng(43.7069, -72.2869), "Rohan", "Sudikoff",
                MainActivity.myRegId, encodedImage, "Tree in Sudikoff");
        new DatastoreTask().execute(ADD_TREE, tree);
    }

    public void addTreeTest(TreeEntry entry) {
        new DatastoreTask().execute(ADD_TREE, entry);
    }

    public void getTreeByIDTest(long treeID, Intent intent)
    {
        try {
            new DatastoreTask().execute(GET_TREE_BY_ID,treeID,intent).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
            bm = getResizedBitmap(bm, MAX_SIZE);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();

            return Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            Log.d(TAG, "Load file error: " + filename);
            return null;
        }
    }

    // code source:
    // http://stackoverflow.com/questions/16954109/
    // reduce-the-size-of-a-bitmap-to-a-specified-size-in-android
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static class DatastoreTask extends AsyncTask<Object, Void, Void> {
        private int operMode;
        private List<TreeEntry> mTreeEntryList;
        private List<TimelineEntry> mTimelineList;
        private ActivityEntriesAdapter mAdapter;
        private TimeLineAdapter mTimeLineAdapter;

        public DatastoreTask(){
        }

        public DatastoreTask(ArrayList<TreeEntry> list){
            mTreeEntryList = list;
        }

        public DatastoreTask(ActivityEntriesAdapter adapter, List<TreeEntry> entryList){
            mTreeEntryList = entryList;
            mAdapter = adapter;
        }

        public DatastoreTask(TimeLineAdapter timeLineAdapter, List<TimelineEntry> entryList){
            mTimelineList = entryList;
            mTimeLineAdapter = timeLineAdapter;
        }

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

                case GET_TREES_AROUND_ME:

                    Map<String, String> data1 = new HashMap<>();
                    //data1.put("Date Time", Long.toString(tree1.dateTime));
                    data1.put("Radius", Integer.toString((Integer)params[1]));
                    data1.put("Longitude", Double.toString((Double)params[2]));
                    data1.put("Lattitude", Double.toString((Double)params[3]));

                    //data1.put("Name", tree1.name);
                    //data1.put("City", tree1.city);
                    //data1.put("Registration ID", tree1.regId);
                    //data1.put("Photo", tree1.photo);
                    //data1.put("Comment", tree1.comment);
                    try {

                        String treesAroundMe = ServerUtilities.post(SERVER_ADDR + "/gettreesaroundme.do", data1);
                        JSONArray treesAroundMeJSON = new JSONArray(treesAroundMe);
                        //List<TreeEntry> treeEntryList = new ArrayList<>();

                        // A hack for getting all trees.
                        if (mAdapter != null && mTreeEntryList != null) {
                            mTreeEntryList.clear();
                            for(int i=0;i<treesAroundMeJSON.length();i++)
                            {
                                Gson gson = new Gson();
                                TreeEntry treeEntry = gson.fromJson(treesAroundMeJSON.getString(i),TreeEntry.class);
                                mTreeEntryList.add(treeEntry);
                                Log.e("In GetTreesAroundMe ", treeEntry.comment);
                            }
                        }

                    } catch (IOException e) {
                        Log.e(TAG, "Sync failed: " + e.getCause());
                        Log.e(TAG, "data posting error " + e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case GET_MY_TREES:
                    Map<String, String> data21 = new HashMap<>();
                    data21.put("Registration ID", (String)params[1]);
                    try {
                        String myTrees = ServerUtilities.post(SERVER_ADDR + "/getmytrees.do", data21 );
                        JSONArray jsonResult = new JSONArray(myTrees);
//                        List<TreeEntry> treeEntryList = new ArrayList<>()
                        mTreeEntryList.clear();
                        for(int i=0;i<jsonResult.length();i++)
                        {

                            Gson gson = new Gson();
                            TreeEntry treeEntry = gson.fromJson(jsonResult.getString(i),TreeEntry.class);
                            mTreeEntryList.add(treeEntry);
                            Log.e(TAG, treeEntry.comment);
                        }   ////treeEntryList now contains all the TreeEntry Objects
                    } catch (IOException e) {
                        Log.e(TAG, "Sync failed: " + e.getCause());
                        Log.e(TAG, "data posting error " + e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON error " + e);
                    }



                    break;

                case GET_MY_UPDATED_TREES:
                    Map<String, String> data3 = new HashMap<>();
                    data3.put("Registration ID", (String)params[1]);
                    try {
                        String myTrees = ServerUtilities.post(SERVER_ADDR + "/gettreesiupdated.do", data3 );
                        JSONArray jsonResult = new JSONArray(myTrees);
                        //List<TreeEntry> treeEntryList = new ArrayList<>();
                        mTreeEntryList.clear();
                        for(int i=0;i<jsonResult.length();i++)
                        {

                            Gson gson = new Gson();
                            TreeEntry treeEntry = gson.fromJson(jsonResult.getString(i),TreeEntry.class);
                            mTreeEntryList.add(treeEntry);
                            Log.e("In GetTreesIUpdated", "Tree:"+treeEntry.treeId+" "+treeEntry.regId);
                        }///treeEntryList now contains all my updates treeEntry objects
                    } catch (IOException e) {
                        Log.e(TAG, "Sync failed: " + e.getCause());
                        Log.e(TAG, "data posting error " + e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case GET_TIMELINE:
                    Map<String, String> data4 = new HashMap<>();
                    data4.put("Tree ID", ((String.valueOf(params[1]))));
                    try {
                        String myTrees = ServerUtilities.post(SERVER_ADDR + "/gettimeline.do", data4 );
                        JSONArray jsonResult = new JSONArray(myTrees);
//                        List<TimelineEntry> treeEntryList = new ArrayList<>();
                        mTimelineList.clear();
                        for(int i=0;i<jsonResult.length();i++)
                        {

                            Gson gson = new Gson();
                            TimelineEntry tempTimelineEntry = gson.fromJson(jsonResult.getString(i),TimelineEntry.class);
                            mTimelineList.add(tempTimelineEntry);
                            Log.e("In GetTimeline", Long.toString(tempTimelineEntry.timelineId));
                        }///treeEntryList now contains all my updates treeEntry objects
                    } catch (IOException e) {
                        Log.e(TAG, "Sync failed: " + e.getCause());
                        Log.e(TAG, "data posting error " + e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case GET_TREE_BY_ID:
                    Map<String, String> data5 = new HashMap<>();
                    data5.put("Tree ID", ((String.valueOf(params[1]))));
                    try {
                        String myTree = ServerUtilities.post(SERVER_ADDR + "/gettreebyid.do", data5 );
                        JSONArray jsonResult = new JSONArray(myTree);
                        //List<TimelineEntry> treeEntryList = new ArrayList<>();
                        Gson gson = new Gson();
                        Intent intent = (Intent)params[2];
                        TreeEntry treeByID = gson.fromJson(jsonResult.getString(0),TreeEntry.class);

                        Bundle extras = new Bundle();
                        extras.putParcelable(ENTRY,treeByID);
                        intent.putExtras(extras);

                        Log.e("In TreeByID",String.valueOf(treeByID.treeId)+" "+treeByID.comment);
                    } catch (IOException e) {
                        Log.e(TAG, "Sync failed: " + e.getCause());
                        Log.e(TAG, "data posting error " + e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch(operMode) {
                case GET_MY_TREES:
                    mAdapter.clear();
                    mAdapter.addAll(mTreeEntryList);
                    mAdapter.notifyDataSetChanged();
                    break;
                case GET_MY_UPDATED_TREES:
                    mAdapter.clear();
                    mAdapter.addAll(mTreeEntryList);
                    mAdapter.notifyDataSetChanged();
                    break;
                case GET_TREES_AROUND_ME:
                    if (mAdapter != null && mTreeEntryList != null) {
                        mAdapter.clear();
                        mAdapter.addAll(mTreeEntryList);
                        mAdapter.notifyDataSetChanged();
                    }
                    isFinish = true;
                    break;
                case GET_TIMELINE:
                    mTimeLineAdapter.clear();
                    mTimeLineAdapter.addAll(mTimelineList);
                    mTimeLineAdapter.notifyDataSetChanged();
                    break;
            }
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

            String regId = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regId = gcm.register(SENDER_ID);
                MainActivity.myRegId = regId;

                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.

                regService.register(regId).execute();


            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return regId; //msg;
        }

        @Override
        protected void onPostExecute(String id) {
            if (id.length() == 0) {
                Logger.getLogger("REGISTRATION").log(Level.INFO, "Error");
            }
            else {
                MainActivity.myRegId = id;
                Toast.makeText(context, "New Reg ID = " + id, Toast.LENGTH_LONG).show();
                Log.d(TAG, "New ID = " + id);
                try {   // write to file. Delete the old one and write to new one.
                    File file = new File(REG_FILE);
                    file.delete();
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FileWriter fileWriter = new FileWriter(file, true);
                    BufferedWriter bfWriter = new BufferedWriter(fileWriter);
                    bfWriter.write(id);
                    bfWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
