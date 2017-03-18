/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.rest;

import com.example.bean.FileMediaBean;
import com.example.media.MediaGroup;
import com.example.media.MediaItem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.PUT;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 *
 * @author orugiho
 */
@Path("media")
public class MediaResource {

    @Context
    private ServletContext context;
    private static final Logger logger
            = Logger.getLogger("com.example.rest.MediaResource");
    private FileMediaBean fmm;

    @PostConstruct
    private void init() {
        String realPath = context.getRealPath("fxmedia");
        fmm = new FileMediaBean(realPath);
        fmm.loadData();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public MediaList getMedia() {
        MediaList mediaList = new MediaList();
        String realPath = context.getRealPath("fxmedia");
        List<MediaGroup> groups = fmm.getGroups();
        for (MediaGroup group : groups) {
            for (MediaItem item : group.getItems()) {
                mediaList.mediaId.add(item.getId());
            }
        }
        return mediaList;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public MediaItem getMediaItem(@PathParam("id") String id) {
        MediaItem item = null;
        try {
            item = fmm.getMediaManager().getMediaItem(id);
            item.setUri("http://localhost:8080/WebMediaManager/fxmedia/"
                    + item.getId());
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE,
                    "FileMediaManager did not find a media item with id: "
                    + id, ex);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return item;
    }

    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteMediaItem(@PathParam("id") String id) {
        ResponseBuilder responseBuilder;
        MediaItem item = null;
        try {
            fmm.getMediaManager().deleteMediaItem(id);
            responseBuilder = Response.ok("Deleted: " + id);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE,
                    "FileMediaManager did not find a media item with id: "
                    + id, ex);
            responseBuilder = Response.status(Response.Status.NOT_FOUND);
        }
        return responseBuilder.build();
    }

    @PUT
    @Path("{fileName}")
    public Response addMediaItem(
            @PathParam("fileName") String fileName,
            File file) {
        ResponseBuilder responseBuilder;
        int periodIndex = fileName.lastIndexOf(".");
        String title = fileName.substring(0, periodIndex);
        MediaItem item = new MediaItem(title, fileName, new Date());
        try {
            fmm.getMediaManager().createMediaItem(item, 
                    new FileInputStream(file));
            responseBuilder = Response.ok("Uploaded: " + fileName);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, 
                    "FileMediaManager failed to access the file: " + 
                            fileName, ex);
            responseBuilder = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
        return responseBuilder.build();
    }
}
