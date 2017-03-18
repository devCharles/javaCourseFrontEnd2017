/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 *
 * @author orugiho
 */
@Entity
public class MediaContent implements Serializable {

    @Id
    private String id;
    @Lob
    private byte[] media;

    public MediaContent() {
    }

    public MediaContent(String id, byte[] media) {
        this.id = id;
        this.media = media;
    }

    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getMedia() {
        return media;
    }

    public void setMedia(byte[] media) {
        this.media = media;
    }
}
