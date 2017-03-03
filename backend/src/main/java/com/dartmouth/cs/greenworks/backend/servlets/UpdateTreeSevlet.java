package com.dartmouth.cs.greenworks.backend.servlets;

import com.dartmouth.cs.greenworks.backend.datastores.TimelineDataStore;
import com.dartmouth.cs.greenworks.backend.datastores.TimelineEntry;

import java.io.IOException;
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

        // get data from request, wrap into a entry object,
        // and insert to datastore.
//        long timelineId = Long.parseLong(request.getParameter("Timeline ID"));
        long timelineId = TimelineDataStore.idCounter++;
        long treeId = Long.parseLong(request.getParameter("Tree ID"));
        long datetime = Long.parseLong(request.getParameter("Date Time"));
        String name = request.getParameter("Name");
        String regId = request.getParameter("Registration ID");
        String photo = request.getParameter("Photo");
        String comment = request.getParameter("Comment");


        TimelineEntry timelineEntry = new TimelineEntry(treeId, timelineId,
                datetime, name, regId, photo, comment);

        TimelineDataStore timelineDataStore = new TimelineDataStore();
        timelineDataStore.addEntry2Datastore(timelineEntry);
        response.sendRedirect("/listalltrees.do");
    }
}
