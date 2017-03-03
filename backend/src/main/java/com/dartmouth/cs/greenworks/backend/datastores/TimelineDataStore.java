package com.dartmouth.cs.greenworks.backend.datastores;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Xiaolei on 2/26/17.
 */

public class TimelineDataStore {
    private Logger logger;
    private DatastoreService datastoreService;
    public static long idCounter = 1;

    public TimelineDataStore () {
        logger = Logger.getLogger(TimelineEntry.class.getName());
        datastoreService = DatastoreServiceFactory.getDatastoreService();
    }

    // create parent key based on PARENT KIND and IDENTIFIER.
    public Key getParentKey() {
        Key retKey = KeyFactory.createKey(TimelineEntry.PARENT_KIND,
                TimelineEntry.PARENT_IDENTIFIER);
        return retKey;
    }

    public TimelineEntry getEntryByIdentifier (long timelineId) {
        Entity entity = null;
        Key key = KeyFactory.createKey(getParentKey(),
                TimelineEntry.ENTRY_ENTITY_KIND, timelineId);
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
    private TimelineEntry convertEntity2Entry(Entity entity) {
        TimelineEntry ret = new TimelineEntry(
                (long)entity.getProperty(TimelineEntry.PROPERTY_TIMELINE_ID),
                (long)entity.getProperty(TimelineEntry.PROPERTY_TREE_ID),
                (long)entity.getProperty(TimelineEntry.PROPERTY_DATETIME),
                (String)entity.getProperty(TimelineEntry.PROPERTY_NAME),
                (String)entity.getProperty(TimelineEntry.PROPERTY_REG_ID),
                ((Text)entity.getProperty(TimelineEntry.PROPERTY_PHOTO)).getValue(),
                (String)entity.getProperty(TimelineEntry.PROPERTY_COMMENT)
        );
        return ret;
    }

    public void addEntry2Datastore(TimelineEntry entry) {
        // kind, key field, parent key are needed for new entity
        Entity entity = new Entity(TimelineEntry.ENTRY_ENTITY_KIND,
                entry.timelineId, getParentKey());
        entity.setProperty(TimelineEntry.PROPERTY_TIMELINE_ID, entry.timelineId);
        entity.setProperty(TimelineEntry.PROPERTY_TREE_ID, entry.treeId);
        entity.setProperty(TimelineEntry.PROPERTY_DATETIME, entry.dateTime);
        entity.setProperty(TimelineEntry.PROPERTY_NAME, entry.name);
        entity.setProperty(TimelineEntry.PROPERTY_REG_ID, entry.regId);
        entity.setProperty(TimelineEntry.PROPERTY_PHOTO, new Text(entry.photo));
        entity.setProperty(TimelineEntry.PROPERTY_COMMENT, entry.comment);
        datastoreService.put(entity);
    }


    // maybe useless.
    public ArrayList<TimelineEntry> queryAll () {
        ArrayList<TimelineEntry> ret = new ArrayList<>();
        Iterator<Entity> entities = getAllEntities();
        while(entities.hasNext()) {
            Entity temp = entities.next();
            if (temp != null) {
                ret.add(convertEntity2Entry(temp));
            }
        }
        return ret;
    }

    // maybe useless.
    private Iterator<Entity> getAllEntities() {
        Query query = new Query(TimelineEntry.ENTRY_ENTITY_KIND);
        query.setFilter(null);
        query.setAncestor(getParentKey());
        PreparedQuery preparedQuery = datastoreService.prepare(query);
        return preparedQuery.asIterator();
    }

    public ArrayList<TimelineEntry> queryUpdatesOfATree (long treeId) {
        ArrayList<TimelineEntry> ret = new ArrayList<> ();

        Query.Filter filter = new Query.FilterPredicate(TimelineEntry.PROPERTY_TREE_ID,
                Query.FilterOperator.EQUAL, treeId);
        Query query = new Query(TimelineEntry.ENTRY_ENTITY_KIND);

        query.setFilter(filter);
        // get the newest ones first.
        query.addSort(TimelineEntry.PROPERTY_DATETIME, Query.SortDirection.DESCENDING);
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

    // get my timeline updates.
    // note it will return the time line, not the trees.
    public ArrayList<TimelineEntry> queryMyUpdate (String regId) {
        ArrayList<TimelineEntry> ret = new ArrayList<> ();

        Query.Filter filter = new Query.FilterPredicate(TimelineEntry.PROPERTY_REG_ID,
                Query.FilterOperator.EQUAL, regId);
        Query query = new Query(TimelineEntry.ENTRY_ENTITY_KIND);

        query.setFilter(filter);
        // get the newest ones first.
        query.addSort(TimelineEntry.PROPERTY_DATETIME, Query.SortDirection.DESCENDING);
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
}
