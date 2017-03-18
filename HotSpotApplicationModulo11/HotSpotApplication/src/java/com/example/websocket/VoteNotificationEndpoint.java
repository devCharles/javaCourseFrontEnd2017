/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.websocket;

import com.example.model.VoteSingleton;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author orugiho
 */
@ServerEndpoint("/votes")
public class VoteNotificationEndpoint {

    private final VoteSingleton vs = VoteSingleton.getInstance();
    
    @OnOpen
    public void onOpen(Session session) {
         vs.addSession(session);
    }

    @OnClose
    public void onClose(Session session) {
         vs.removeSession(session);
    }

}
