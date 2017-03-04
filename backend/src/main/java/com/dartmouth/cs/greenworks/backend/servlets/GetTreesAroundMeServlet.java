package com.dartmouth.cs.greenworks.backend.servlets;

import com.dartmouth.cs.greenworks.backend.datastores.TreeDataStore;
import com.dartmouth.cs.greenworks.backend.datastores.TreeEntry;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.repackaged.com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Xiaolei on 2/26/17.
 */

public class GetTreesAroundMeServlet  extends HttpServlet  {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        int treeCounter; //used to limit results while sending back in pages
        int distanceRadius = Integer.parseInt(request.getParameter("Radius"));
        double longitude = Double.parseDouble(request.getParameter("Longitude"));
        double lattitude = Double.parseDouble(request.getParameter("Lattitude"));
        GeoPt location = new GeoPt((float)lattitude,(float)longitude);
        //fetch my trees from data store
        TreeDataStore treeDataStore = new TreeDataStore();
        ArrayList<TreeEntry> treesNearMe = new ArrayList<>();

        treesNearMe = treeDataStore.queryNearbyTrees(distanceRadius, location);

        String result = new Gson().toJson(treesNearMe);
        response.getWriter().write(result);
    }
}
