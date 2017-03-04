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

public class GetMyTreesServlet  extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String RegistrationID = request.getParameter("Registration ID");
        int treeCounter; //used to limit results while sending back in pages

        //fetch my trees from data store
        TreeDataStore treeDataStore = new TreeDataStore();
        // TimelineDataStore timelineDataStore = new TimelineDataStore();
        ArrayList<TreeEntry> treeEntries = treeDataStore.queryMyTrees(RegistrationID);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONArray myTrees = new JSONArray();

        for(TreeEntry tempTree:treeEntries)
        {

            //for every TreeEntry add it into returning jsonarray
            myTrees.add(tempTree);
            //increment tree counter to limit page results
            //treeCounter++;
        }


        String json = new Gson().toJson(myTrees);

        response.getWriter().write(json);

    }
}
