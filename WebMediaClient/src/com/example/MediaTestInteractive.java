package com.example;

import com.example.media.MediaItem;
import com.example.rest.MediaList;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MediaTestInteractive {

    private static String[] mediaArray;

    public static void main(String[] args) {
        // Create a WebTarget object for our RESTful calls
        String baseURL = "http://localhost:8080/WebMediaManager/resources";
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseURL);

        boolean timeToQuit = false;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            do {
                timeToQuit = executeMenu(in, target);
            } while (!timeToQuit);
        } catch (IOException e) {
            System.out.println("Error " + e.getClass().getName() + " , quiting.");
            System.out.println("Message: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error closing resource " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
        }
    }

    public static boolean executeMenu(BufferedReader in, WebTarget target) throws IOException {
        MediaList list;
        String action;
        int id;

        System.out.println("\n\n[L]ist | [R]ead | [A]dd | [D]elete | [Q]uit: ");
        action = in.readLine();
        if (action.length() == 0) {
            System.out.println("Enter one of: L, R, D, A, Q");
            return false;
        } else if (action.toUpperCase().charAt(0) == 'Q') {
            return true;
        }

        switch (action.toUpperCase().charAt(0)) {
            // List all the media resources available using GET
            case 'L':
                list = target
                        .path("media")
                        .request(MediaType.APPLICATION_JSON)
                        .get(MediaList.class);
                mediaArray = list.mediaId.toArray(new String[list.mediaId.size()]);
                System.out.println("Media in the collection:");
                for (int i = 0; i < mediaArray.length; i++) {
                    System.out.println("(" + i + ") : " + mediaArray[i]);
                }
                break;

            // Display a media element (metadata) using a GET 
            case 'R':
                System.out.println("Enter media number to read: ");
                id = new Integer(in.readLine().trim());
                if (mediaArray != null && id <= mediaArray.length) {
                    MediaItem item = target
                            .path("media/" + mediaArray[id])
                            .request(MediaType.APPLICATION_JSON)
                            .get(MediaItem.class);
                    System.out.println("MediaItem:");
                    System.out.println(item.toString());
                } else {
                    System.out.println("Please choose a valid item number.");
                }
                break;

            // Delete a media item using DELETE
            case 'D':
                System.out.println("Enter media number to delete: ");
                id = new Integer(in.readLine().trim());
                if (mediaArray != null && id <= mediaArray.length) {
                    Response response = target
                            .path("media/" + mediaArray[id])
                            .request()
                            .delete();
                    if (response.getStatus()
                            == Response.Status.OK.getStatusCode()) {
                        System.out.println(
                                "Successfully deleted media itemnumber: " + id);
                    }
                } else {
                    System.out.println("Please choose a valid item number.");
                }
                break;

            // Add a new media item using a PUT 
            case 'A':
                System.out.println("Enter full path to new media item: ");
                String path = in.readLine();
                File file = new File(path);
                Path p = file.toPath();
                if (!Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS)) {
                    System.out.println("The file: " + path
                            + " either does not exist or is not a regular file.");
                    break;
                }
                String fileName = p.getFileName().toString();
                final Response response = target
                        .path("media/" + fileName)
                        .request()
                        .put(Entity.entity(file,
                                MediaType.APPLICATION_OCTET_STREAM));
                if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                    System.out.println("Successfully uploaded: " + path);
                }
                break;

            default:
                System.out.println("Enter one of: L, R, D, A, Q");
        }

        return false;
    }
}
