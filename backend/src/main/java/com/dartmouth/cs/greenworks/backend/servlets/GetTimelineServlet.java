package com.dartmouth.cs.greenworks.backend.servlets;

import com.dartmouth.cs.greenworks.backend.datastores.TimelineDataStore;
import com.dartmouth.cs.greenworks.backend.datastores.TimelineEntry;
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

public class GetTimelineServlet  extends HttpServlet {
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
        long treeID = Long.parseLong(request.getParameter("Tree ID"));
        //fetch my trees from data store
        TimelineDataStore timelineDataStore = new TimelineDataStore();
        // TimelineDataStore timelineDataStore = new TimelineDataStore();
        ArrayList<TimelineEntry> timelineEntries = timelineDataStore.queryUpdatesOfATree(treeID);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONArray myTrees = new JSONArray();

        for(TimelineEntry tempTimelineEntry:timelineEntries)
        {

            //for every TreeEntry add it into returning jsonarray
            myTrees.add(tempTimelineEntry);
            //increment tree counter to limit page results
            //treeCounter++;
        }

        String jsonResult = new Gson().toJson(timelineEntries);
        response.getWriter().write(jsonResult);

    }
}
