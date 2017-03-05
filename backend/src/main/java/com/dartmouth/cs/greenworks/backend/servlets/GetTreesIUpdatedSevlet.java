package com.dartmouth.cs.greenworks.backend.servlets;

import com.dartmouth.cs.greenworks.backend.datastores.TreeDataStore;
import com.dartmouth.cs.greenworks.backend.datastores.TreeEntry;
import com.google.appengine.repackaged.com.google.gson.Gson;

import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Xiaolei on 2/26/17.
 */

public class GetTreesIUpdatedSevlet  extends HttpServlet  {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        int treeCounter; //used to limit results while sending back in pages
        String RegisrationID = request.getParameter("Registration ID");
        //fetch my trees from data store
        TreeDataStore treeDataStore = new TreeDataStore();
        ArrayList<TreeEntry> treeEntries = treeDataStore.queryTreesIUpdated(RegisrationID);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONArray myTrees = new JSONArray();

        for(TreeEntry treeEntry:treeEntries)
        {

            //for every TreeEntry add it into returning jsonarray
            myTrees.add(treeEntry);
            //increment tree counter to limit page results
            //treeCounter++;
        }

        String jsonResult = new Gson().toJson(myTrees);
        response.getWriter().write(jsonResult);

    }
}
