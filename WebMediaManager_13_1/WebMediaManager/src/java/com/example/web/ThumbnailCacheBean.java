/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.web;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

/**
 *
 * @author orugiho
 */
@Singleton
public class ThumbnailCacheBean {

    private Map<String, byte[]> thumbNails = new HashMap<>();

    @Lock(LockType.READ)
    public byte[] getCachedThumbnail(String id) {
        return thumbNails.get(id);
    }

    @Lock(LockType.WRITE)
    public void setCachedThumbnail(String id, byte[] bytes) {
        thumbNails.put(id, bytes);
    }
}
