package com.dartmouth.cs.greenworks.backend.servlets;

import com.dartmouth.cs.greenworks.backend.datastores.TimelineDataStore;
import com.dartmouth.cs.greenworks.backend.datastores.TreeDataStore;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Xiaolei on 3/3/17.
 */

// Warning: this will wipe both datastore clean.
    // Use it only when you want a clean slate
public class ClearAllServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        TreeDataStore treeDataStore = new TreeDataStore();

        TimelineDataStore timelineDataStore = new TimelineDataStore();

        treeDataStore.deleteAll();
        timelineDataStore.deleteAll();
        response.sendRedirect("/listalltrees.do");
    }
}
