package com.dartmouth.cs.greenworks.backend.servlets;

import com.dartmouth.cs.greenworks.backend.datastores.TreeDataStore;
import com.dartmouth.cs.greenworks.backend.datastores.TreeEntry;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.appengine.repackaged.com.google.gson.reflect.TypeToken;
import com.google.appengine.repackaged.com.google.type.LatLng;

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

        // get data from request, wrap into a entry object,
        // and insert to datastore.
//        long treeId = Long.parseLong(request.getParameter("Tree ID"));
        long treeId = TreeDataStore.idCounter++;
        long datetime = Long.parseLong(request.getParameter("Date Time"));
        String location = request.getParameter("Location");
        String name = request.getParameter("Name");
        String city = request.getParameter("City");
        String regId = request.getParameter("Registration ID");
        String photo = request.getParameter("Photo");
        String comment = request.getParameter("Comment");

        Blob photoBlob = new Blob(photo.getBytes());

        Gson gson = new Gson();
        LatLng locLatLng = gson.fromJson(location, new TypeToken<LatLng>(){}.getType());
        GeoPt locGeoPt = new GeoPt((float)locLatLng.getLatitude(),
                (float)locLatLng.getLongitude());

        TreeEntry treeEntry = new TreeEntry(treeId, datetime,
                locGeoPt, name, city, regId, photoBlob, comment);

        TreeDataStore treeDataStore = new TreeDataStore();
        treeDataStore.addEntry2Datastore(treeEntry);
        response.sendRedirect("/listalltrees.do");
    }
}
