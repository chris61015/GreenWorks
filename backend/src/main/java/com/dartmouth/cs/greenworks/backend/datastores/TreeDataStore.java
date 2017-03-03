package com.dartmouth.cs.greenworks.backend.datastores;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Xiaolei on 2/26/17.
 */

public class TreeDataStore {
    private Logger logger;
    private DatastoreService datastoreService;
    public static long idCounter = 1;

    public TreeDataStore () {
        logger = Logger.getLogger(TreeEntry.class.getName());
        datastoreService = DatastoreServiceFactory.getDatastoreService();
    }

    // create parent key based on PARENT KIND and IDENTIFIER.
    public Key getParentKey() {
        Key retKey = KeyFactory.createKey(TreeEntry.PARENT_KIND,
                TreeEntry.PARENT_IDENTIFIER);
        return retKey;
    }

    public TreeEntry getEntryByIdentifier (long treeId) {
        Entity entity = null;
        Key key = KeyFactory.createKey(getParentKey(),
                TreeEntry.ENTRY_ENTITY_KIND, treeId);
        try {
            entity = datastoreService.get(key);
        } catch (Exception e) {
            logger.log(Level.INFO, "Error in retrieving timeline entry");
        }

        // found the entry
        if (entity != null) {
            return convertEntity2Entry(entity);
        }
        else {  // didn't find the entry
            return null;
        }
    }

    // get data from entity and wrap them to timeline entry to serve user.
    private TreeEntry convertEntity2Entry(Entity entity) {
        TreeEntry ret = new TreeEntry(
                (long)entity.getProperty(TreeEntry.PROPERTY_TREE_ID),
                (long)entity.getProperty(TreeEntry.PROPERTY_DATETIME),
                (GeoPt)entity.getProperty(TreeEntry.PROPERTY_LOCATION),
                (String)entity.getProperty(TreeEntry.PROPERTY_NAME),
                (String)entity.getProperty(TreeEntry.PROPERTY_CITY),
                (String)entity.getProperty(TreeEntry.PROPERTY_REG_ID),
                ((Text)entity.getProperty(TreeEntry.PROPERTY_PHOTO)).getValue(),
                (String)entity.getProperty(TreeEntry.PROPERTY_COMMENT)
        );
        return ret;
    }

    // Add tree entry to datastore
    public void addEntry2Datastore(TreeEntry entry) {
        // kind, key field, parent key are needed for new entity
        Entity entity = new Entity(TreeEntry.ENTRY_ENTITY_KIND,
                entry.treeId, getParentKey());
        entity.setProperty(TreeEntry.PROPERTY_TREE_ID, entry.treeId);
        entity.setProperty(TreeEntry.PROPERTY_DATETIME, entry.dateTime);
        entity.setProperty(TreeEntry.PROPERTY_NAME, entry.name);
        entity.setProperty(TreeEntry.PROPERTY_LOCATION, entry.location);
        entity.setProperty(TreeEntry.PROPERTY_CITY, entry.city);
        entity.setProperty(TreeEntry.PROPERTY_REG_ID, entry.regId);
        entity.setProperty(TreeEntry.PROPERTY_PHOTO, new Text(entry.photo));
        entity.setProperty(TreeEntry.PROPERTY_COMMENT, entry.comment);
        datastoreService.put(entity);
    }

    // get all tree entries from data store
    public ArrayList<TreeEntry> queryAll () {
        ArrayList<TreeEntry> ret = new ArrayList<>();
        Iterator<Entity> entities = getAllEntities();
        while(entities.hasNext()) {
            Entity temp = entities.next();
            if (temp != null) {
                ret.add(convertEntity2Entry(temp));
            }
        }
        return ret;
    }

    // helper function to get trees from datastore:
    // get entities first.
    private Iterator<Entity> getAllEntities() {
        Query query = new Query(TreeEntry.ENTRY_ENTITY_KIND);
        query.setFilter(null);
        query.setAncestor(getParentKey());
        PreparedQuery preparedQuery = datastoreService.prepare(query);
        return preparedQuery.asIterator();
    }

    // get my trees using registration ID as identifier.
    public ArrayList<TreeEntry> queryMyTrees (String regId) {
        ArrayList<TreeEntry> ret = new ArrayList<>();
        Filter filter = new Query.FilterPredicate(TreeEntry.PROPERTY_REG_ID,
                Query.FilterOperator.EQUAL, regId);
        Query query = new Query(TreeEntry.ENTRY_ENTITY_KIND);

        query.setFilter(filter);
        // get the newest ones first.
        query.addSort(TreeEntry.PROPERTY_DATETIME, Query.SortDirection.DESCENDING);
        PreparedQuery preparedQuery = datastoreService.prepare(query);
        Iterator<Entity> iter = preparedQuery.asIterator();
        while(iter.hasNext()) {
            Entity temp = iter.next();
            if (temp != null) {
                ret.add(convertEntity2Entry(temp));
            }
        }
        return ret;
    }

    // get trees around me using geographic data.
    public ArrayList<TreeEntry> queryNearbyTrees (double miles, GeoPt center) {

        ArrayList<TreeEntry> sorted = new ArrayList<>();

        // get all entries
        ArrayList<TreeEntry> allEntries = queryAll();

        for (TreeEntry entry:allEntries) {
            GeoPt loca = entry.location;
            entry.distanceToOrigin = distance(center.getLatitude(), center.getLongitude(),
                    loca.getLatitude(), loca.getLongitude());
        }

        // sort the list in ascending order of distance
        // by removing the closest tree from original list and
        // adding to the new list.
        while (allEntries != null && allEntries.size() > 0) {
            int index = closest (allEntries);
            if (allEntries.get(index).distanceToOrigin <= miles) {
                sorted.add(allEntries.get(index));
            }
            allEntries.remove(index);
        }

        // return a sorted list of all trees.
        // Let the caller choose which page it wants.
        return sorted;
    }

    public ArrayList<TreeEntry> queryTreesIUpdated(String regId) {
        TimelineDataStore timelineDataStore = new TimelineDataStore();

        ArrayList<TreeEntry> treeEntries = new ArrayList<>();

        // get all my updates from timeline datastore.
        ArrayList<TimelineEntry> myUpdates = timelineDataStore.queryMyUpdate(regId);

        // get tree IDs from my updates.
        ArrayList<Long> treeIds = new ArrayList<>();
        if (myUpdates != null) {
            for (TimelineEntry update:myUpdates) {
                long treeId = update.treeId;
                if (!treeIds.contains(treeId)) {
                    treeIds.add(treeId);
                }
            }
        }

        // get trees from tree datastore using tree IDs.
        for (long treeId:treeIds) {
            TreeEntry temp = getEntryByIdentifier(treeId);
            if (temp != null) {
                treeEntries.add(temp);
            }
        }
        return treeEntries;

    }


    // find the index of the entries with the smallest distance.
    private int closest(ArrayList<TreeEntry> entries) {

        double minDistance = entries.get(0).distanceToOrigin;
        int index = 0;

        for (int i = 1; i < entries.size(); i++) {
            if (entries.get(i).distanceToOrigin < minDistance) {
                minDistance = entries.get(i).distanceToOrigin;
                index = i;
            }
        }
        return index;
    }


    // code source: http://www.geodatasource.com/developers/java
    private double distance(double lat1, double lon1,
                            double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;  // in miles
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians			:*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees			:*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
