/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.bean;

import com.example.entity.DBMediaManager;
import com.example.media.MediaGroup;
import com.example.media.MediaItem;
import com.example.media.MediaManager;
import com.example.media.MediaOrder;
import com.example.media.MediaQualifier;
import com.example.media.MediaType;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author tmcginn
 */
@Stateless
@LocalBean
public class FileMediaBean {

    private static final Logger logger = Logger.getLogger("com.example.FileMediaBean");
    private final String appURL = "http://localhost:8080/WebMediaManager/fxmedia/";
    @EJB
    private DBMediaManager mm;

    private int pictureCount, videoCount;
    private List<MediaGroup> groups = new ArrayList<>();
    private MediaQualifier mq = new MediaQualifier();

    public FileMediaBean() {
        mq.setSortOrder(MediaOrder.TITLE_ASC);
        mq.setTypes(MediaType.IMAGE, MediaType.OGV_VIDEO, MediaType.MP4_VIDEO);
    }

    // Use the default MediaQualifier object
    public void loadData() {
        loadData(mq);
    }

    public void loadData(MediaQualifier mq) {
        pictureCount = 0;
        videoCount = 0;
        try {
            groups = mm.listMediaItems(mq);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "FileMediaManager failed to access its files", ex);
        }
        // Update the counts
        for (MediaGroup group : groups) {
            for (MediaItem mediaItem : group.getItems()) {
                switch (mediaItem.getType()) {
                    case IMAGE:
                        pictureCount++;
                        break;
                    case MP4_VIDEO:
                    case OGV_VIDEO:
                        videoCount++;
                        break;
                }
                mediaItem.setUri(appURL + mediaItem.getId());
            }
        }
    }

    public int getPictureCount() {
        return pictureCount;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public List<MediaGroup> getGroups() {
        return groups;
    }

    public MediaItem getMediaItem(String id) throws FileNotFoundException {
        MediaItem item = mm.getMediaItem(id);
        item.setUri(appURL + item.getId());
        return item;
    }

    public MediaQualifier getQualifier() {
        return mq;
    }

    public MediaManager getMediaManager() {
        return mm;
    }
}
