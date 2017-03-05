package com.dartmouth.cs.greenworks.backend.servlets;

import com.dartmouth.cs.greenworks.backend.datastores.TreeDataStore;
import com.dartmouth.cs.greenworks.backend.datastores.TreeEntry;
import com.google.appengine.repackaged.com.google.gson.Gson;

import org.json.simple.JSONArray;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Xiaolei on 3/4/17.
 */

public class GetTreeByIdServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        long treeID = Long.parseLong(request.getParameter("Tree ID"));
        int treeCounter; //used to limit results while sending back in pages

        //fetch my trees from data store
        TreeDataStore treeDataStore = new TreeDataStore();
        // TimelineDataStore timelineDataStore = new TimelineDataStore();
        TreeEntry treeEntry = treeDataStore.getEntryByIdentifier(treeID);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONArray tree = new JSONArray();
        tree.add(treeEntry);

        String json = new Gson().toJson(tree);

        response.getWriter().write(json);
    }
}
