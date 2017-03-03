package com.dartmouth.cs.greenworks.backend.servlets;

import com.dartmouth.cs.greenworks.backend.datastores.TimelineDataStore;
import com.dartmouth.cs.greenworks.backend.datastores.TimelineEntry;
import com.dartmouth.cs.greenworks.backend.datastores.TreeDataStore;
import com.dartmouth.cs.greenworks.backend.datastores.TreeEntry;
import com.google.appengine.api.datastore.GeoPt;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Xiaolei on 2/28/17.
 */

// this is for html display of trees + timelines.
public class ListAllTreesServlet extends HttpServlet {
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

        ArrayList<TreeEntry> treeEntries = treeDataStore.queryAll();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // header start
        out.write("<html>\n" +
                "<head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n" +
                "<title>GreenWorks</title>\n" +
                "</head>\n" +
                "<body>\n");
        out.write("<h1>GreenWorks</h1>\n");


        if (treeEntries != null) {
            for (TreeEntry treeEntry:treeEntries) {
                out.write(treeEntry.treeId + ". " +
                        "Name: " + treeEntry.name +
                        " Date Time: " + formatDatetime(treeEntry.dateTime) +
                        " GeoPt: " + locationToString(treeEntry.location) + "<br>\n");
                // All the '+' was replaced by space somehow during storage in datastore.
                out.write("<img src='data:img/jpg;base64," + treeEntry.photo.replace(' ', '+') + "' /><br>\n");
                out.write("&nbsp;&nbsp;&nbsp;&nbsp;" + "Timeline: <br>\n");
                ArrayList<TimelineEntry>timelineEntries = timelineDataStore
                        .queryUpdatesOfATree(treeEntry.treeId);
                for (TimelineEntry timelineEntry:timelineEntries) {
                    out.write("&nbsp;&nbsp;&nbsp;&nbsp;" +
                            timelineEntry.timelineId + "). " +
                            "Name: " + timelineEntry.name +
                            " Date Time: " + formatDatetime(timelineEntry.dateTime) + "<br>\n");
                }
            }
        }

        // header end.
        out.write("</body>\n" +
                "</html>");
    }

    private String formatDatetime(long ms) {
        Date date = new Date(ms);
        SimpleDateFormat formatter = new SimpleDateFormat("kk:mm:ss MMM dd yyyy");
        return formatter.format(date);
    }
    private String locationToString(GeoPt pt) {
        return "(" + pt.getLatitude() + ", " + pt.getLongitude() + ")";
    }

}
