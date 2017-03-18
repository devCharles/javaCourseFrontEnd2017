package com.example.controller;

import com.example.bean.FileMediaBean;
import com.example.media.ImageResizer;
import com.example.web.ThumbnailCacheBean;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DownloadServlet", urlPatterns = {"/download/*"})
public class DownloadServlet extends HttpServlet {

    @EJB
    private FileMediaBean fmm;
    @EJB
    private ThumbnailCacheBean tCache;

    // Called for both GET and POST requests
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext context = getServletContext();
        //ServletContext ctx = getServletContext();
        String mediaId = request.getPathInfo().replaceFirst("/", "");

        try {
            String type = context.getMimeType(mediaId);
            response.setContentType(type);
            byte [] imageContent = 
                    fmm.getMediaManager().getMediaContent(mediaId);
            if (request.getParameter("thumb") != null && ImageResizer.canResize(type)) {
                ServletOutputStream sout = response.getOutputStream();
                byte[] thumbBytes = tCache.getCachedThumbnail(mediaId);
                if (thumbBytes == null) {
                    ByteArrayInputStream bais = 
                            new ByteArrayInputStream(imageContent);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageResizer.resize(120, bais, baos, type);
                    thumbBytes = baos.toByteArray();
                    tCache.setCachedThumbnail(mediaId, thumbBytes);
                }
                sout.write(thumbBytes);
            } else {
                response.setContentLength((int) imageContent.length);
                ServletOutputStream sout = response.getOutputStream();
                sout.write(imageContent, 0, imageContent.length);
            }
        } catch (FileNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

//    @Override
//    public long getLastModified(HttpServletRequest request) {
//        String mediaId = request.getPathInfo().replaceFirst("/", "");
//        try {
//            MediaItem mediaItem = mm.getMediaItem(mediaId);
//            //must be rounded down to the nearest second
//            return mediaItem.getDate().getTime() / 1000 * 1000;
//        } catch (FileNotFoundException e) {
//            return -1;
//        }
//    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);

    }
}
