/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.controller;

import com.example.bean.FileMediaBean;
import com.example.media.MediaItem;
import com.example.media.MediaOrder;
import com.example.media.MediaQualifier;
import com.example.media.MediaType;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author orugiho
 */
@WebServlet(name = "MediaController", urlPatterns = {"/MediaController"})
public class MediaController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        response.setContentType("text/html;charset=UTF-8");
        MediaQualifier mq;
        ServletContext context = getServletContext();
        String realPath = context.getRealPath("fxmedia");
        FileMediaBean fmm = new FileMediaBean(realPath);
        HttpSession session = request.getSession();
        if (session.isNew()) {
            mq = fmm.getQualifier();
            session.setAttribute("qualifierBean", mq); 
        }
        mq = (MediaQualifier) session.getAttribute("qualifierBean");
        String action = request.getParameter("action");
         if (action == null){
              action = "manager";
         }
         String address;
        switch (action) {
            case "manager":
                 fmm.loadData(mq);
                 request.setAttribute("fileBean", fmm);
                 address = "/WEB-INF/view/manager.jsp";
                break;
            case "item":
                String itemId = request.getParameter("itemId");
                MediaItem item = fmm.getMediaItem(itemId);
                request.setAttribute("itemBean", item);
                address = "/WEB-INF/media.jsp";
                break;
            case "settings":
                address = "/WEB-INF/settings.jsp";
                break;
            case "newSettings":
                String type = request.getParameter("type");
                String sortOrder = request.getParameter("sortOrder");
                if (type != null) {
                switch (type) {
                  case "images":
                    mq.setTypes(MediaType.IMAGE);
                    break;
                  case "videos":
                      mq.setTypes(MediaType.MP4_VIDEO, MediaType.OGV_VIDEO);
                      break;
                  default:
                    mq.setTypes(MediaType.IMAGE, MediaType.MP4_VIDEO, MediaType.OGV_VIDEO);
                 } 
                }
                if (sortOrder != null) {
                    mq.setSortOrder(MediaOrder.valueOf(sortOrder));
                }
                fmm.loadData(mq);
                request.setAttribute("fileBean", fmm);
                address = "/WEB-INF/view/manager.jsp";
                break; // break for case "newSettings"
            default:
                request.setAttribute("error", "Unknown action!");
                address = "/WEB-INF/unknown.jsp";
                break;
        }
        RequestDispatcher dispatcher =request.
         getRequestDispatcher(address);
        dispatcher.forward(request, response);
//        PrintWriter out = response.getWriter();
//        out.println("<h1>Servlet MediaController at "
//                + request.getContextPath() + "</h1>");
//        out.println(fmm.getPictureCount() + " Pictures, "
//                + fmm.getVideoCount() + " Videos</p>");
//        for (MediaGroup group : fmm.getGroups()) {
//            for (MediaItem item : group.getItems()) {
//                out.println(item.getTitle());
//                out.println("<br>");
//            }
//        }
//        out.println("<p><a href='" +
//                getServletContext().getContextPath() + 
//                "/upload.html'>Upload a new file</a>");
 
    }

        // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
        /**
         * Handles the HTTP <code>GET</code> method.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        @Override
        protected void doGet
        (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            processRequest(request, response);
        }

        /**
         * Handles the HTTP <code>POST</code> method.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        @Override
        protected void doPost
        (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            processRequest(request, response);
        }

        /**
         * Returns a short description of the servlet.
         *
         * @return a String containing servlet description
         */
        @Override
        public String getServletInfo
        
            () {
        return "Short description";
        }// </editor-fold>

    }
