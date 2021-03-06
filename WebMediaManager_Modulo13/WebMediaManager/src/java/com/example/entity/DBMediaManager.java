package com.example.entity;

import com.example.media.MediaGroup;
import com.example.media.MediaItem;
import com.example.media.MediaManager;
import com.example.media.MediaOrder;
import static com.example.media.MediaOrder.DATE_ASC;
import static com.example.media.MediaOrder.DATE_DESC;
import static com.example.media.MediaOrder.TITLE_ASC;
import static com.example.media.MediaOrder.TITLE_DESC;
import com.example.media.MediaQualifier;
import com.example.media.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Stateless
@LocalBean
public class DBMediaManager implements MediaManager {

    private static final Logger logger = Logger.getLogger("com.example.entity.DBMediaManager");
    @PersistenceContext(name = "WebMediaManagerPU")
    private EntityManager em;

    public DBMediaManager() {
    }

    @Override
    public void createMediaItem(MediaItem item, InputStream content) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            int bytesRead;
            byte[] buffer = new byte[8192]; // 8K blocks
            while ((bytesRead = content.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            logger.log(Level.FINE, "Creating media item with a byte length of {0}", bos.size());
            MediaContent contentItem = new MediaContent(item.getId(), bos.toByteArray());
            try {
                em.persist(contentItem);
                em.persist(item);
            } catch (ConstraintViolationException cve) {
                logger.log(Level.WARNING, "Constraint Violation Exceptions during createMediaItem ", cve);
                for (ConstraintViolation cv : cve.getConstraintViolations()) {
                    logger.log(Level.WARNING, "Violation: {0}", cv.getMessage());
                }
                throw new IOException("Failed to store the media file.");
            }
        }
    }

    @Override
    public MediaItem getMediaItem(String id) throws FileNotFoundException {
        // TODO: find and return a MediaItem entity that mathces this id
        MediaItem mediaItem = em.find(MediaItem.class, id);
        if (mediaItem == null) {
            throw new FileNotFoundException(id + " not found");
        }
        return mediaItem;
    }

    @Override
    public byte[] getMediaContent(String id) throws IOException {
        // TODO: find and return a MediaContent entity that matches this id
        MediaContent media = em.find(MediaContent.class, id);
        if (media == null) {
            throw new FileNotFoundException(id + " not found");
        }
        return media.getMedia();
    }

    @Override
    public void updateMediaItem(MediaItem item) throws FileNotFoundException {
        // After any method invocation in this class, the entities are "detached"
        // Get the current entity for this id and then update the entity
        MediaItem entityItem = em.find(MediaItem.class, item.getId());
        if (entityItem == null) {
            throw new FileNotFoundException(item.getId() + " not found");
        }
        entityItem.setTitle(item.getTitle());
        entityItem.setDate(item.getDate());
    }

    @Override
    public void deleteMediaItem(String id) {
        // Find the entity and content with this id
        MediaItem item = em.find(MediaItem.class, id);
        MediaContent content = em.find(MediaContent.class, id);

        em.remove(item);
        em.remove(content);
    }

    @Override
    public List<MediaGroup> listMediaItems(MediaQualifier filter) throws FileNotFoundException {
        StringBuilder queryText = new StringBuilder(
                "SELECT item FROM MediaItem as item WHERE ");
        List<MediaType> types = filter.getTypes();
        for (MediaType type : types) {
            switch (type) {
                case OGV_VIDEO:
                    queryText.append("LOWER(item.id) LIKE '%.ogv' OR ");
                    break;
                case MP4_VIDEO:
                    queryText.append("LOWER(item.id) LIKE '%.mpg4' OR LOWER(item.id) LIKE '%.mp4' OR LOWER(item.id) LIKE '%.m4v' OR ");
                    break;
                case IMAGE:
                    queryText.append("LOWER(item.id) LIKE '%.jpg' OR LOWER(item.id) LIKE '%.jpeg' OR LOWER(item.id) LIKE '%.png' OR LOWER(item.id) LIKE '%.gif' OR ");
                    break;
            }
        }
        queryText.delete(queryText.lastIndexOf("OR"), Integer.MAX_VALUE);
        MediaOrder order = filter.getSortOrder();
        switch (order) {
            case DATE_ASC:
                queryText.append(" ORDER BY item.date, UPPER(item.title)");
                break;
            case DATE_DESC:
                queryText.append(
                        " ORDER BY item.date DESC,UPPER(item.title)");
                break;
            case TITLE_ASC:
                queryText.append(" ORDER BY UPPER(item.title), item.date");
                break;
            case TITLE_DESC:
                queryText.append(
                        " ORDER BY UPPER(item.title) DESC,item.date");
                break;
        }
        TypedQuery<MediaItem> query = em.createQuery(queryText.toString(), MediaItem.class);
        List<MediaItem> allItems = query.getResultList();
        List<MediaGroup> groups = new ArrayList<>();
        switch (filter.getSortOrder()) {
            case DATE_DESC:
            case DATE_ASC:
                groupItemsByDate(groups, allItems);
                break;
            case TITLE_DESC:
            case TITLE_ASC:
                groupItemsByTitle(groups, allItems);
                break;
        }
        return groups;
    }

    private void groupItemsByTitle(List<MediaGroup> groups, List<MediaItem> items) {
        char previousLetter = '\u0000';
        MediaGroup group = null;
        for (MediaItem mediaItem : items) {
            char letter = mediaItem.getTitle().toUpperCase().charAt(0);
            if (previousLetter != letter) {
                previousLetter = letter;
                group = new MediaGroup(Character.toString(letter));
                groups.add(group);
            }
            group.getItems().add(mediaItem);
        }
    }

    private void groupItemsByDate(List<MediaGroup> groups, List<MediaItem> items) {
        String previousDate = "notequal";
        MediaGroup group = null;
        for (MediaItem mediaItem : items) {
            String date = SimpleDateFormat.getDateInstance().format(mediaItem.getDate());
            if (!previousDate.equals(date)) {
                previousDate = date;
                group = new MediaGroup(date);
                groups.add(group);
            }
            group.getItems().add(mediaItem);
        }
    }
}
