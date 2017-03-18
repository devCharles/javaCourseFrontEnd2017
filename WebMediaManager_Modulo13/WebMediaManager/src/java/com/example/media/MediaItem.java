package com.example.media;

import java.io.Serializable;
import java.util.Date;
import javax.activation.MimetypesFileTypeMap;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "MEDIAINFO")
public class MediaItem implements Serializable {

    private String title;
    @Column(name = "MEDIADATE")
    @Temporal(TemporalType.DATE)
    private Date date;
    @Id
    private String id;
    @Transient
    private String uri;

    public MediaItem() {}
    
    public MediaItem(String title, String id, Date date) {
        this.title = title;
        this.date = date;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public MediaType getType() {
        String type = new MimetypesFileTypeMap().getContentType(id); 
        if (type.startsWith("image")) {
            return MediaType.IMAGE;
        } else if (type.contains("application/ogg") || type.contains("video/ogg")) {
            return MediaType.OGV_VIDEO;
        } else if (type.contains("video/mp4")) {
            return MediaType.MP4_VIDEO;
        } else if (type.contains("video/x-flv")) {
            return MediaType.FLASH_VIDEO;
        } else {
            return MediaType.OTHER;
        }
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format(
                "Title: %s\n"
                + "Type:  %s\n"
                + "Date:  %s\n"
                + "Id:    %s\n"
                + "URI:   %s", getTitle(), getType(), getDate(), getId(), getUri());
    }
}