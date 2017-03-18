package com.example.model;

import java.io.StringWriter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.Session;

/**
 *
 * @author mheimer
 */
public class VoteSingleton {

    private static final VoteSingleton instance = new VoteSingleton();
    private final ConcurrentMap<String, Integer> votes = new ConcurrentHashMap<>();
    private final Set<Session> sessions = new CopyOnWriteArraySet<>();

    private VoteSingleton() {

    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    private void sendMessage(String id, Integer total) {
        StringWriter sw = new StringWriter();
        try (JsonWriter jw = Json.createWriter(sw)) {
        JsonObject item = Json.createObjectBuilder()
                .add("id", id)
                .add("votes", total)
                .build();
        jw.writeObject(item);
        }
        sessions.stream().forEach((session) -> {
            session.getAsyncRemote().sendText(sw.toString());
        });
    }

    public static VoteSingleton getInstance() {
        return instance;
    }

    public Integer getVotes(String id) {
        return votes.getOrDefault(id, 0);
    }

    public Integer like(String id) {
        Integer total = addVote(id, 1);
        sendMessage(id, total);
        return total;
    }

    public Integer dislike(String id) {
        Integer total = addVote(id, -1);
        sendMessage(id, total);
        return total;
    }

    private Integer addVote(String id, Integer value) {
        Integer result = votes.putIfAbsent(id, value);
        if (result == null) {
            return value;
        } else {
            while (!votes.replace(id, result, result + value)) {
                result = votes.get(id);
            }
            return result + value;
        }
    }

}
