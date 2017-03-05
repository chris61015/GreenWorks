package com.dartmouth.cs.greenworks.Utils;

import android.util.Log;

import com.dartmouth.cs.greenworks.Activity.ServerUtilities;
import com.dartmouth.cs.greenworks.Model.TreeEntry;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chris61015 on 3/4/17.
 */

public class FetchUtils {
    final static private String TAG = "TAGG";
    public static String SERVER_ADDR = "http://127.0.0.1:8080";

    public static List<TreeEntry>  FetchMyTress(){
        List<TreeEntry> treeEntryList = new ArrayList<>();
        Map<String, String> data21 = new HashMap<>();
        data21.put("Registration ID", "");
        try {
            String myTrees = ServerUtilities.post(SERVER_ADDR + "/getmytrees.do", data21 );
            JSONArray jsonResult = new JSONArray(myTrees);


            for(int i=0;i<jsonResult.length();i++)
            {

                Gson gson = new Gson();
                TreeEntry treeEntry = gson.fromJson(jsonResult.getString(i),TreeEntry.class);
                treeEntryList.add(treeEntry);
                Log.e(TAG, treeEntry.comment);
            }   ////treeEntryList now contains all the TreeEntry Objects
        } catch (IOException e) {
            Log.e(TAG, "Sync failed: " + e.getCause());
            Log.e(TAG, "data posting error " + e);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSON error " + e);
        }

        return treeEntryList;

    }

}
