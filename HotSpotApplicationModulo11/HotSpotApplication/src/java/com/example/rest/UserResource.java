/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.rest;

import java.security.Principal;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

@Path("/user")
public class UserResource {
@RolesAllowed({"associate", "professional"})
@GET
public String getUserName(@Context SecurityContext sc) {
        Principal p = sc.getUserPrincipal();
        if (p != null) {
            return p.getName();
        } else {
            return "Anonymous";
        }
} }