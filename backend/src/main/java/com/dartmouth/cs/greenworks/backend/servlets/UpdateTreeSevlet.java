package com.dartmouth.cs.greenworks.backend.servlets;

import com.dartmouth.cs.greenworks.backend.MessagingEndpoint;
import com.dartmouth.cs.greenworks.backend.datastores.TimelineDataStore;
import com.dartmouth.cs.greenworks.backend.datastores.TimelineEntry;
import com.dartmouth.cs.greenworks.backend.datastores.TreeDataStore;
import com.dartmouth.cs.greenworks.backend.datastores.TreeEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Xiaolei on 2/26/17.
 */

public class UpdateTreeSevlet extends HttpServlet {
    Logger logger = Logger.getLogger(TimelineEntry.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        TimelineDataStore timelineDataStore = new TimelineDataStore();
        TreeDataStore treeDataStore = new TreeDataStore();
        // get data from request, wrap into a entry object,
        // and insert to datastore.
        long timelineId = timelineDataStore.idGenerator();
        long treeId = Long.parseLong(request.getParameter("Tree ID"));
        long datetime = Long.parseLong(request.getParameter("Date Time"));
        String name = request.getParameter("Name");
        String regId = request.getParameter("Registration ID");
        String photo = request.getParameter("Photo");
        String comment = request.getParameter("Comment");


        TimelineEntry timelineEntry = new TimelineEntry(timelineId, treeId,
                datetime, name, regId, photo, comment);


        timelineDataStore.addEntry2Datastore(timelineEntry);

        // for pushing notification:
        ArrayList<String> regIds = new ArrayList<>();
        TreeEntry tree = treeDataStore.getEntryByIdentifier(treeId);
        regIds.add(tree.regId);

        ArrayList<TimelineEntry> timelines = timelineDataStore.queryUpdatesOfATree(treeId);
        for(TimelineEntry timeline:timelines) {
            regIds.add(timeline.regId);
        }
        new MessagingEndpoint().sendMessage("Tree " + treeId + " Updated", regIds);
    }
}
