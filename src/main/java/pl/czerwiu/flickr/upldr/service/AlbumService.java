package pl.czerwiu.flickr.upldr.service;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.Photosets;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.czerwiu.flickr.upldr.config.FlickrProperties;
import pl.czerwiu.flickr.upldr.exception.FlickrUploadException;

/**
 * Service for managing Flickr albums (photosets).
 * Handles album creation and photo addition to albums.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {

    private final Flickr flickrClient;
    private final FlickrProperties flickrProperties;

    /**
     * Ensures album exists, creates it if necessary.
     * Searches for album by name (case-insensitive), creates if not found.
     *
     * @param albumName name of the album
     * @param primaryPhotoId photo ID to use as primary photo if creating new album
     * @return album ID (photoset ID)
     * @throws FlickrUploadException if album operations fail
     */
    public String ensureAlbum(String albumName, String primaryPhotoId) throws FlickrUploadException {
        log.debug("Ensuring album exists: {}", albumName);

        try {
            PhotosetsInterface photosetsInterface = flickrClient.getPhotosetsInterface();
            String userNsid = flickrProperties.getUser().getNsid();

            // Get all user's photosets
            Photosets photosets = photosetsInterface.getList(userNsid);

            // Search for existing album (case-insensitive)
            for (Photoset photoset : photosets.getPhotosets()) {
                if (photoset.getTitle().equalsIgnoreCase(albumName)) {
                    log.info("Album found: albumId={}, title={}",
                        photoset.getId(), photoset.getTitle());
                    return photoset.getId();
                }
            }

            // Album not found, create new one
            log.info("Album not found, creating new album: {}", albumName);
            Photoset newPhotoset = photosetsInterface.create(
                albumName,
                "Created by flickr-upldr",
                primaryPhotoId
            );

            log.info("Album created successfully: albumId={}, title={}",
                newPhotoset.getId(), newPhotoset.getTitle());

            return newPhotoset.getId();

        } catch (FlickrException e) {
            log.error("Failed to ensure album exists: albumName={}, error={}",
                albumName, e.getMessage(), e);
            throw new FlickrUploadException(
                "Failed to ensure album exists: " + albumName,
                e.getMessage(),
                e
            );
        }
    }

    /**
     * Adds photo to existing album.
     *
     * @param photoId photo ID to add
     * @param albumId album ID (photoset ID) to add photo to
     * @throws FlickrUploadException if adding photo fails
     */
    public void addPhotoToAlbum(String photoId, String albumId) throws FlickrUploadException {
        log.debug("Adding photo to album: photoId={}, albumId={}", photoId, albumId);

        try {
            PhotosetsInterface photosetsInterface = flickrClient.getPhotosetsInterface();
            photosetsInterface.addPhoto(albumId, photoId);

            log.info("Photo added to album successfully: photoId={}, albumId={}",
                photoId, albumId);

        } catch (FlickrException e) {
            log.error("Failed to add photo to album: photoId={}, albumId={}, error={}",
                photoId, albumId, e.getMessage(), e);
            throw new FlickrUploadException(
                "Failed to add photo to album",
                e.getMessage(),
                e
            );
        }
    }
}
