<%-- 
    Document   : manager
    Created on : 04-mar-2017, 8:39:01
    Author     : orugiho
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Media Manager</title>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial- scale=1">
        <link href="/WebMediaManager/css/bootstrap.min.css" rel="stylesheet">
        <link href="/WebMediaManager/css/media-manager.css" rel="stylesheet">
    </head>
    <body>
        <jsp:useBean id="fileBean" type="com.example.bean.FileMediaBean" 
                     scope="request" />
        <div class="container">
            <div class="page-header">
                <h1>Media Manager <small class="hidden- xs">${fileBean.videoCount} Videos, ${fileBean.pictureCount} Pictures</small></h1>
            </div>
        </div>
        <c:forEach var="mediaGroup" items="${fileBean.groups}"> 
            <div class="row">
                <div class="col-sm-1">
                    <h3 class="group-header">${mediaGroup.title}</h3>
                </div>
                <div class="col-sm-11">
                    <ul class="list-inline">
                        <c:forEach var="mediaItem"
                                   items="${mediaGroup.items}">
                            <li> <div>
                                    <a href="/WebMediaManager/MediaController?action=item&itemId=${mediaItem.id}">
                                        <c:choose>
                                            <c:when test="${mediaItem.type == 'IMAGE'}">
                                                <img src="${mediaItem.uri}"
                                                     class="thumbnail"/>
                                            </c:when>
                                            <c:otherwise>
                                                <img src="/WebMediaManager/images/play.png"
                                                     class="thumbnail"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <h5>${mediaItem.title}</h5>
                                    </a>
                                </div>
                            </li>
                        </c:forEach>
                    </ul> </div>
            </div>
        </c:forEach>
    <li>
    <a href="/WebMediaManager/upload.html">Upload</a></li>
    <li>
    <a

        href="/WebMediaManager/MediaController?action=settings">Settings
    </a></li>
</body>
</html>
