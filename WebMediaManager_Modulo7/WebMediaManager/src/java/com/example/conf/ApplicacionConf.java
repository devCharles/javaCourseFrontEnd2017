/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.conf;

import com.example.rest.MediaList;
import com.example.rest.MediaResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author orugiho
 */
@ApplicationPath("resources")
public class ApplicacionConf extends Application{

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set =new HashSet<>();
        set.add(MediaResource.class);
        return set; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
