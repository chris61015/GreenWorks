package com.dartmouth.cs.greenworks.backend.servlets;

import com.dartmouth.cs.greenworks.backend.datastores.TreeDataStore;
import com.dartmouth.cs.greenworks.backend.datastores.TreeEntry;
import com.google.appengine.api.datastore.GeoPt;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Xiaolei on 2/26/17.
 */

public class AddTreeServlet extends HttpServlet {
    Logger logger = Logger.getLogger(TreeEntry.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        TreeDataStore treeDataStore = new TreeDataStore();

        // get data from request, wrap into a entry object,
        // and insert to datastore.
        long treeId = treeDataStore.idGenerator();
        long datetime = Long.parseLong(request.getParameter("Date Time"));
        String lat = request.getParameter("Latitude");
        String lng = request.getParameter("Longitude");
        String name = request.getParameter("Name");
        String city = request.getParameter("City");
        String regId = request.getParameter("Registration ID");
        String photo = request.getParameter("Photo");
        String comment = request.getParameter("Comment");


        TreeEntry treeEntry = new TreeEntry(treeId, datetime,
                new GeoPt(Float.parseFloat(lat), Float.parseFloat(lng)), name, city, regId, photo, comment);


        treeDataStore.addEntry2Datastore(treeEntry);
        response.sendRedirect("/listalltrees.do");
    }
}
