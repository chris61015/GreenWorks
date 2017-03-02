package com.dartmouth.cs.greenworks.backend.datastores;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

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
    public static long idCounter = 0;

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
                (Blob)entity.getProperty(TreeEntry.PROPERTY_PHOTO),
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
        entity.setProperty(TreeEntry.PROPERTY_PHOTO, entry.photo);
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
        //TODO
        return ret;
    }

    // get trees around me using geographic data.
    public ArrayList<TreeEntry> queryNearbyTrees (long miles, GeoPt center) {
        ArrayList<TreeEntry> ret = new ArrayList<>();
        //TODO
        return ret;
    }
}
