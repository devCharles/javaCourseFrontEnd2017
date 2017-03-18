package com.example.rest;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

/**
 *
 * @author mheimer
 */
@ApplicationPath("/resources")
public class HotSpotApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(HotSpotResource.class);
        classes.add(RolesAllowedDynamicFeature.class);
        classes.add(UserResource.class);
        return classes;
    }
    
    
    
}
